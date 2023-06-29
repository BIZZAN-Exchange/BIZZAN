package com.bizzan.bitrade.vendor.provider.support;

import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.vendor.provider.EmailProvider;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.resource.Emailv31;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.util.HashMap;
import java.util.Map;


@Slf4j
public class TestEmailProvider implements EmailProvider {

    @Value("${spring.mail.username}")
    private String from;
    @Value("${spark.system.name}")
    private String company;

    @Value("${aliyun.mail-sms.region}")
    private String e_Region;
    @Value("${aliyun.mail-sms.access-key-id}")
    private String e_accessKeyId;
    @Value("${aliyun.mail-sms.access-secret}")
    private String e_accessSecret;


    @Override
    @Async
    public MessageResult sendEmail(String email, String code, String subject, String templateName) throws Exception {
        log.info("邮件发送成功{}",code);
        Map<String, Object> model = new HashMap<>(16);
        model.put("code", code);
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);
        cfg.setClassForTemplateLoading(this.getClass(), "/templates");
        Template template = cfg.getTemplate(templateName);
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        log.info("e_Regione ::{}",e_Region);
        log.info(html);
        return MessageResult.success("SUCCESS");
    }
}