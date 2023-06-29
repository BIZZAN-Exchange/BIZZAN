package com.bizzan.bitrade.vendor.provider.support;

import com.bizzan.bitrade.dto.SmsDTO;
import com.bizzan.bitrade.service.SmsService;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.vendor.provider.SMSProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * 第一信息短信接口（http://www.1xinxi.cn）
 * @author Hevin  E-mail:bizzanhevin@gmail.com
 *
 */
@Slf4j
public class MxtongSMSProvider implements SMSProvider {
    private String username;
    private String password;
    private String account;
	private String sign;

    @Autowired
    private SmsService smsService;

    public MxtongSMSProvider(String username, String password, String account,String sign) {
    	this.username = username;
    	this.password = password;
    	this.account = account;
		this.sign = sign;
    }
    
    public static String getName() {
        return "mxtong";
    }
    
	@Override
	public MessageResult sendSingleMessage(String mobile, String content) throws Exception {
//		SmsDTO smsDTO = smsService.getByStatus();
//        if("mxtong".equals(smsDTO.getSmsName())){
		return sendMessage(mobile,content,null);
//        }
//        return null;
	}

	@Override
	public MessageResult sendVerifyMessage(String mobile, String verifyCode) throws Exception {
		String content = formatVerifyCode(verifyCode);
		return sendSingleMessage(mobile, content);
	}
	@Override
	public MessageResult sendMessageByTempId(String mobile, String content, String templateId) throws Exception {
		return null;
	}


	public MessageResult sendMessage(String mobile, String content,SmsDTO smsDTO) throws Exception{
        log.info("sms content={}", content);
		StringBuffer sb = new StringBuffer("http://www.mxtong.cn:8080/GateWay/Services.asmx/DirectSend?");
		sb.append("UserID="+username);
		sb.append("&Account="+account);
		sb.append("&Password="+password);
		sb.append("&Phones="+mobile);
		sb.append("&Content="+URLEncoder.encode("您的验证码为" + content + "，十分钟内有效，如非本人操作，请忽略。【"+this.sign+"】","UTF-8"));
		sb.append("&SendType=1");
		sb.append("&SendTime=");
		sb.append("&PostFixNumber=");
		// 创建url对象
		//String temp = new String(sb.toString().getBytes("GBK"),"UTF-8");
		URL url = new URL(sb.toString());

		// 打开url连接
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		// 设置url请求方式 ‘post’
		connection.setRequestMethod("GET");

		// 发送
		InputStream is =url.openStream();

		//转换返回值（‘0,2019062814515653465244228,0,1,0,提交成功’）
		String returnStr = convertStreamToString(is);
		
		log.info(" mobile : " + mobile + "content : " + content);
		log.info("send result: "+returnStr);
		
		return parseResult(returnStr);
    }
	
    private MessageResult parseResult(String result) {
        //返回示例：{"code":"0","msg":"SUCCESS","smUuid":"18863_1_0_15738776414_1_XaOQ74O_1"}
		//<ROOT xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns="JobSendedDescription">
		//  <RetCode>Sucess</RetCode>
		//  <JobID>132950428</JobID>
		//  <OKPhoneCounts>1</OKPhoneCounts>
		//  <StockReduced>1</StockReduced>
		//  <ErrPhones />
		//</ROOT>
		MessageResult mr = new MessageResult(500, "系统错误");
	    if(result.indexOf("<RetCode>Sucess</RetCode>")>1){
		   mr.setCode(0);
		   mr.setMessage("SUCCESS");
	    }
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
		log.info("不支持");
		MessageResult mr = new MessageResult(500, "不支持");
		return mr;
	}
}
