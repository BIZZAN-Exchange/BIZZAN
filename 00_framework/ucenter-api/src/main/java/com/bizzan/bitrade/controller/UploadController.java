package com.bizzan.bitrade.controller;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.bizzan.bitrade.config.AliyunConfig;
import com.bizzan.bitrade.config.S3Config;
import com.bizzan.bitrade.service.LocaleMessageSourceService;
import com.bizzan.bitrade.util.FileUtil;
import com.bizzan.bitrade.util.GeneratorUtil;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.util.UploadFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@Slf4j
public class UploadController {

    private String allowedFormat = ".jpg,.gif,.png,.jpeg";

    @Autowired
    private AliyunConfig aliyunConfig;
    @Autowired
    private S3Config s3Config;
    @Autowired
    private LocaleMessageSourceService sourceService;

    @Value("${oss.name}")
    private String ossName;

    /**
     * 上传图片到阿里云OSS
     *
     * @param request
     * @param response
     * @param file
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "upload/oss/image", method = RequestMethod.POST)
    @ResponseBody
    public MessageResult uploadOssImage(HttpServletRequest request, HttpServletResponse response,
                                        @RequestParam("file") MultipartFile file) throws IOException {
        log.info(request.getSession().getServletContext().getResource("/").toString());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        Assert.isTrue(ServletFileUpload.isMultipartContent(request), sourceService.getMessage("FORM_FORMAT_ERROR"));
        Assert.isTrue(file != null, sourceService.getMessage("NOT_FIND_FILE"));
        log.info("该文件的文件流为转为类型>>>>>>>>>>>"+UploadFileUtil.getFileHeader(file.getInputStream()));
        String fileType=UploadFileUtil.getFileType(file.getInputStream());
        System.out.println("fileType="+fileType);
        String directory = new SimpleDateFormat("yyyy/MM/dd/").format(new Date());

        try {
            String fileName = file.getOriginalFilename();
            String suffix = fileName.substring(fileName.lastIndexOf("."), fileName.length());
            System.out.println("suffix="+suffix);
            if (!allowedFormat.contains(suffix.trim().toLowerCase())) {
                return MessageResult.error(sourceService.getMessage("FORMAT_NOT_SUPPORT"));
            }
            if(fileType==null||!allowedFormat.contains(fileType.trim().toLowerCase())){
                return MessageResult.error(sourceService.getMessage("FORMAT_NOT_SUPPORT"));
            }
            String key = directory + GeneratorUtil.getUUID() + suffix;
            System.out.println(key);
            //压缩文件
            String path = request.getSession().getServletContext().getRealPath("/") + "upload/"+file.getOriginalFilename();
            File tempFile=new File(path);
            FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);
            log.info("=================压缩前"+tempFile.length());
            UploadFileUtil.zipWidthHeightImageFile(tempFile,tempFile,425,638,0.7f);
            log.info("=================压缩后"+tempFile.length());

            return doUpload(file,key);

        } catch (OSSException oe) {
            return MessageResult.error(500, oe.getErrorMessage());
        } catch (ClientException ce) {
            System.out.println("Error Message: " + ce.getMessage());
            return MessageResult.error(500, ce.getErrorMessage());
        } catch (Throwable e) {
            e.printStackTrace();
            return MessageResult.error(500, sourceService.getMessage("SYSTEM_ERROR"));
        }
    }


    @RequestMapping(value = "upload/local/image", method = RequestMethod.POST)
    @ResponseBody
    public MessageResult uploadLocalImage(HttpServletRequest request, HttpServletResponse response,
                                        @RequestParam("file") MultipartFile file) throws IOException {
        log.info(request.getSession().getServletContext().getResource("/").toString());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        Assert.isTrue(ServletFileUpload.isMultipartContent(request), sourceService.getMessage("FORM_FORMAT_ERROR"));
        Assert.isTrue(file != null, sourceService.getMessage("NOT_FIND_FILE"));
        //验证文件类型
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."), fileName.length());
        if (!allowedFormat.contains(suffix.trim().toLowerCase())) {
            return MessageResult.error(sourceService.getMessage("FORMAT_NOT_SUPPORT"));
        }
        String result= UploadFileUtil.uploadFile(file,fileName);
        if(result!=null){
            MessageResult mr = new MessageResult(0, sourceService.getMessage("UPLOAD_SUCCESS"));
            mr.setData(result);
            return mr;
        }else{
            MessageResult mr = new MessageResult(0, sourceService.getMessage("FAILED_TO_WRITE"));
            mr.setData(result);
            return mr;
        }
    }


    /**
     * 上传base64处理后的图片
     *
     * @param base64Data
     * @return
     */
    @RequestMapping(value = "/upload/oss/base64", method = RequestMethod.POST)
    @ResponseBody
    public MessageResult base64UpLoad(@RequestParam String base64Data) {
        MessageResult result = new MessageResult();
        try {
            log.debug("上传文件的数据：" + base64Data);
            String dataPrix = "";
            String data = "";
            if (base64Data == null || "".equals(base64Data)) {
                throw new Exception(sourceService.getMessage("NOT_FIND_FILE"));
            } else {
                String[] d = base64Data.split("base64,");
                if (d != null && d.length == 2) {
                    dataPrix = d[0];
                    data = d[1];
                } else {
                    throw new Exception(sourceService.getMessage("DATA_ILLEGAL"));
                }
            }
            log.debug("对数据进行解析，获取文件名和流数据");
            String suffix = "";
            if ("data:image/jpeg;".equalsIgnoreCase(dataPrix)) {//data:image/jpeg;base64,base64编码的jpeg图片数据
                suffix = ".jpg";
            } else if ("data:image/x-icon;".equalsIgnoreCase(dataPrix)) {//data:image/x-icon;base64,base64编码的icon图片数据
                suffix = ".ico";
            } else if ("data:image/gif;".equalsIgnoreCase(dataPrix)) {//data:image/gif;base64,base64编码的gif图片数据
                suffix = ".gif";
            } else if ("data:image/png;".equalsIgnoreCase(dataPrix)) {//data:image/png;base64,base64编码的png图片数据
                suffix = ".png";
            } else {
                throw new Exception(sourceService.getMessage("FORMAT_NOT_SUPPORT"));
            }
            String directory = new SimpleDateFormat("yyyy/MM/dd/").format(new Date());
            String key = directory + GeneratorUtil.getUUID() + suffix;

            //因为BASE64Decoder的jar问题，此处使用spring框架提供的工具包
            byte[] bs = Base64Utils.decodeFromString(data);
            return doUpload(key, bs);
        } catch (Exception e) {
            log.debug("上传失败," + e.getMessage());
            result.setCode(500);
            result.setMessage(e.getMessage());
        }
        return result;
    }


    private MessageResult doUpload(MultipartFile file, String key) {
        log.debug("上传选择" + ossName);
        if("s3".equals(ossName)){
            return s3Upload(file,key);
        }else {
            return ossUpload(file,key);
        }
    }

    private MessageResult doUpload(String key, byte[] bs) {
        if("s3".equals(ossName)){
            return s3Upload(key,bs);
        }else {
            return ossUpload(key,bs);
        }
    }

    private MessageResult s3Upload(MultipartFile file, String key) {
        log.debug("开始上传::" + ossName);
        String[] split =s3Config.getRegionsName().split("-");
        String regionName = "";
        for (String s : split) {
            regionName = regionName + s.toUpperCase()+"_";
        }
        regionName = regionName.substring(0,regionName.length()-1);
        BasicAWSCredentials awsCreds= new BasicAWSCredentials(s3Config.getAccessKeyId(), s3Config.getAccessKeySecret());
        AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion(Regions.valueOf(regionName)).build();

        PutObjectRequest putRequest = null;
        try {
            putRequest = new PutObjectRequest(s3Config.getBucketName(), key, FileUtil.multipartFileToFile(file));
//            AccessControlList acl = new AccessControlList();
//            acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
//            putRequest.setAccessControlList(acl);
            s3.putObject(putRequest);
            String uri = s3Config.toUrl(key);
            MessageResult mr = new MessageResult(0, sourceService.getMessage("UPLOAD_SUCCESS"));
            mr.setData(uri);
            System.out.println("上传成功...");
            return mr;
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResult.error(500, sourceService.getMessage("REQUEST_FAILED"));
        }

    }

    private MessageResult s3Upload(String key, byte[] bs) {
        String[] split =s3Config.getRegionsName().split("-");
        String regionName = "";
        for (String s : split) {
            regionName = regionName + s.toUpperCase()+"_";
        }
        regionName = regionName.substring(0,regionName.length()-1);
        BasicAWSCredentials awsCreds= new BasicAWSCredentials(s3Config.getAccessKeyId(), s3Config.getAccessKeySecret());
        AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion(Regions.valueOf(regionName)).build();

        PutObjectRequest putRequest = null;
        try {
            InputStream is = new ByteArrayInputStream(bs);
            putRequest = new PutObjectRequest(s3Config.getBucketName(), key, is,new ObjectMetadata());
//            AccessControlList acl = new AccessControlList();
//            acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
//            putRequest.setAccessControlList(acl);
            s3.putObject(putRequest);
            String uri = s3Config.toUrl(key);
            MessageResult mr = new MessageResult(0, sourceService.getMessage("UPLOAD_SUCCESS"));
            mr.setData(uri);
            System.out.println("上传成功...");
            return mr;
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResult.error(500, sourceService.getMessage("REQUEST_FAILED"));
        }

    }

    private MessageResult ossUpload(MultipartFile file, String key) {
        OSSClient ossClient = new OSSClient(aliyunConfig.getOssEndpoint(), aliyunConfig.getAccessKeyId(), aliyunConfig.getAccessKeySecret());
        try {
            System.out.println(key);
            ossClient.putObject(aliyunConfig.getOssBucketName(), key, file.getInputStream());
            String uri = aliyunConfig.toUrl(key);
            MessageResult mr = new MessageResult(0, sourceService.getMessage("UPLOAD_SUCCESS"));
            mr.setData(uri);
            return mr;
        } catch (OSSException oe) {
            return MessageResult.error(500, oe.getErrorMessage());
        } catch (ClientException ce) {
            System.out.println("Error Message: " + ce.getMessage());
            return MessageResult.error(500, ce.getErrorMessage());
        } catch (Throwable e) {
            e.printStackTrace();
            return MessageResult.error(500, sourceService.getMessage("REQUEST_FAILED"));
        } finally {
            ossClient.shutdown();
        }
    }

    private MessageResult ossUpload(String key, byte[] bs) {
        OSSClient ossClient = new OSSClient(aliyunConfig.getOssEndpoint(), aliyunConfig.getAccessKeyId(), aliyunConfig.getAccessKeySecret());
        try {
            //使用apache提供的工具类操作流
            InputStream is = new ByteArrayInputStream(bs);
            //FileUtils.writeByteArrayToFile(new File(Global.getConfig(UPLOAD_FILE_PAHT), tempFileName), bs);
            ossClient.putObject(aliyunConfig.getOssBucketName(), key, is);
            String uri = aliyunConfig.toUrl(key);
            MessageResult mr = new MessageResult(0, "上传成功");
            mr.setData(uri);
            log.debug("上传成功,key:{}", key);
            return mr;
        } catch (Exception ee) {
            ee.printStackTrace();
            return MessageResult.error(500, sourceService.getMessage("REQUEST_FAILED"));
        }finally {
            ossClient.shutdown();
        }
    }

}
