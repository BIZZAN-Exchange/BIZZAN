package com.bizzan.bitrade.vendor.provider.support;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.ClientException;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.bizzan.bitrade.dto.SmsDTO;
import com.bizzan.bitrade.service.SmsService;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.vendor.provider.SMSProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TestSMSGlobeProvider implements SMSProvider {
    private String ali_accessKeyId; // accessKeyId
    private String ali_accessSecret; // accessSecret

    @Autowired
    private SmsService smsService;

    public TestSMSGlobeProvider(String ali_accessKeyId, String ali_accessSecret) {
        this.ali_accessKeyId = ali_accessKeyId;
        this.ali_accessSecret = ali_accessSecret;
    }

    public static String getName() {
        return "test";
    }

    @Override
    public MessageResult sendSingleMessage(String mobile, String content) throws Exception {
        SmsDTO smsDTO = smsService.getByStatus();
        return sendMessage(mobile,content,smsDTO);
    }

    @Override
    public MessageResult sendMessageByTempId(String mobile, String content, String templateId) throws Exception {
        return null;
    }

    public MessageResult sendMessage(String mobile, String content,SmsDTO smsDTO) throws Exception{

        log.info("sendMessage, TEST短信=={},{}", mobile, content);

        return parseResult(content);

    }

    @Override
    public MessageResult sendInternationalMessage(String content, String phone) throws IOException, DocumentException {

        log.info("sendMessage, TEST短信=={},{}", phone, content);

        MessageResult messageResult = MessageResult.success();
        messageResult.setMessage(content);
        return messageResult;
    }

    private MessageResult parseResult(String result) {
        // 返回参数形式：
//        {
//                "Message":"OK",
//                "RequestId":"2184201F-BFB3-446B-B1F2-C746B7BF0657",
//                "BizId":"197703245997295588^0",
//                "Code":"OK"
//        }
        MessageResult mr = new MessageResult(0, result);
        return mr;
    }

    /**
     * 转换返回值类型为UTF-8格式.
     * @param is
     * @return
     */
    public String convertStreamToString(InputStream is) {
        StringBuilder sb1 = new StringBuilder();
        byte[] bytes = new byte[4096];
        int size = 0;

        try {
            while ((size = is.read(bytes)) > 0) {
                String str = new String(bytes, 0, size, "UTF-8");
                sb1.append(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb1.toString();
    }

    @Override
    public MessageResult sendCustomMessage(String mobile, String content) throws Exception {
        log.info("阿里云短信不支持自定义文本");
        MessageResult mr = new MessageResult(500, "阿里云短信不支持自定义文本");
        return mr;
    }
}