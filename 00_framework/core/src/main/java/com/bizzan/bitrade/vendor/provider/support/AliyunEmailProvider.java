package com.bizzan.bitrade.vendor.provider.support;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.aliyuncs.dm.model.v20151123.SingleSendMailResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.vendor.provider.EmailProvider;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.util.HashMap;
import java.util.Map;


@Slf4j
public class AliyunEmailProvider implements EmailProvider {

    // 邮件配置
    private IClientProfile profile = null;
    private IAcsClient client = null;

    @Value("${aliyun.mail-sms.from-address}")
    private String emailFromAddress;
    @Value("${aliyun.mail-sms.from-alias}")
    private String emailAlias;
    @Value("${aliyun.mail-sms.email-tag}")
    private String emailTag;

    private String e_Region;
    private String e_accessKeyId;
    private String e_accessSecret;

    private SingleSendMailRequest request = new SingleSendMailRequest();

    public AliyunEmailProvider() {
    }

    public AliyunEmailProvider(String e_Region, String e_accessKeyId, String e_accessSecret) {

        profile = DefaultProfile.getProfile(e_Region, e_accessKeyId, e_accessSecret);
        if(!"cn-hangzhou".equals(e_Region)) {
            try {
                DefaultProfile.addEndpoint("dm." + e_Region + ".aliyuncs.com", e_Region, "Dm", "dm." + e_Region + ".aliyuncs.com");
            } catch (ClientException e) {
                e.printStackTrace();
            }
        }
        client = new DefaultAcsClient(profile);
    }

    @Override
    @Async
    public MessageResult sendEmail(String email,String code,String subject, String templateName) throws Exception {
        Map<String, Object> model = new HashMap<>(16);
        model.put("code", code);
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);
        cfg.setClassForTemplateLoading(this.getClass(), "/templates");
        Template template = cfg.getTemplate(templateName);
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
//        helper.setText(html, true);
        if(!"cn-hangzhou".equals(e_Region)) {
            request.setVersion("2017-06-22");
        }
        request.setAccountName(emailFromAddress);
        request.setFromAlias(emailAlias);
        request.setAddressType(1);
        request.setTagName(emailTag);
        request.setReplyToAddress(true);
        request.setToAddress(email);
        //可以给多个收件人发送邮件，收件人之间用逗号分开，批量发信建议使用BatchSendMailRequest方式
        //request.setToAddress(“邮箱1,邮箱2”);
        request.setSubject(subject);
        //如果采用byte[].toString的方式的话请确保最终转换成utf-8的格式再放入htmlbody和textbody，若编码不一致则会被当成垃圾邮件。
        //注意：文本邮件的大小限制为3M，过大的文本会导致连接超时或413错误
        request.setHtmlBody(html);
        //SDK 采用的是http协议的发信方式, 默认是GET方法，有一定的长度限制。
        //若textBody、htmlBody或content的大小不确定，建议采用POST方式提交，避免出现uri is not valid异常
        request.setMethod(MethodType.POST);
        //开启需要备案，0关闭，1开启
        //request.setClickTrace(“0”);
        //如果调用成功，正常返回httpResponse；如果调用失败则抛出异常，需要在异常中捕获错误异常码；错误异常码请参考对应的API文档;
        SingleSendMailResponse httpResponse = client.getAcsResponse(request);
        log.info("send email for {},content:{}", email, html);
        return MessageResult.success();
    }
}