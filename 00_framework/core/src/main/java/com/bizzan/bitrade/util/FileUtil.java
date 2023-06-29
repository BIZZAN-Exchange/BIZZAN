package com.bizzan.bitrade.util;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.bizzan.bitrade.service.LocaleMessageSourceService;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

public class FileUtil<E> {
    @Autowired
    private LocaleMessageSourceService msService;

    public  MessageResult exportExcel(HttpServletRequest request, HttpServletResponse response, List<E> list, String name) throws Exception{
        if(list.isEmpty()){
            return  MessageResult.error(-1,msService.getMessage(msService.getMessage("NO_DATA")));
        }
        String physicalPath = request.getSession().getServletContext().getRealPath("/")+"excel/";
        String fileName = physicalPath+name+".xlsx";
        File savefile = new File(physicalPath);
        if (!savefile.exists()) {
            savefile.mkdirs();
        }
        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), list.get(0).getClass(), list);
        FileOutputStream fos = new FileOutputStream(fileName);
        workbook.write(fos);
        fos.close();
        response.setContentType("multipart/form-data");
        response.setHeader("Content-Disposition", "attachment;filename="+name+".xlsx");
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        OutputStream out = response.getOutputStream();
        File file = new File(fileName);
        InputStream in = new FileInputStream(file);
        int data=in.read();
        while(data!=-1){
            out.write(data);
            data=in.read();
        }
        in.close();
        out.close();
        file.delete();
        return  MessageResult.success();
    }

    /**
     * MultipartFile 转 File
     *
     * @param file
     * @throws Exception
     */
    public static File multipartFileToFile(MultipartFile file) throws Exception {

        File toFile = null;
        if (file.equals("") || file.getSize() <= 0) {
            file = null;
        } else {
            InputStream ins = null;
            ins = file.getInputStream();
            toFile = new File(file.getOriginalFilename());
            inputStreamToFile(ins, toFile);
            ins.close();
        }
        return toFile;
    }

    //获取流文件
    private static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除本地临时文件
     * @param file
     */
    public static void deleteTempFile(File file) {
        if (file != null) {
            File del = new File(file.toURI());
            del.delete();
        }
    }

}
