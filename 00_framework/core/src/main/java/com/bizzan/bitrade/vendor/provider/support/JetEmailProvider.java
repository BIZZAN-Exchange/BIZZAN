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
import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
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
public class JetEmailProvider implements EmailProvider {

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

    public JetEmailProvider() {
    }

    public JetEmailProvider(String e_accessKeyId, String e_accessSecret) {
        this.e_accessKeyId = e_accessKeyId;
        this.e_accessSecret = e_accessSecret;
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

        MailjetClient client;
        MailjetRequest request;
        client = new MailjetClient(ClientOptions.builder().apiKey(this.e_accessKeyId).apiSecretKey(this.e_accessSecret).build());
        request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray()
                        .put(new JSONObject()
                                .put(Emailv31.Message.FROM, new JSONObject()
                                        .put("Email", emailFromAddress)
                                        .put("Name", emailAlias))
                                .put(Emailv31.Message.TO, new JSONArray().put(new JSONObject().put("Email", email).put("Name", "You")))
                                .put(Emailv31.Message.SUBJECT, subject)
                                .put(Emailv31.Message.TEXTPART, "Greetings from Mailjet!")
                                .put(Emailv31.Message.HTMLPART, html)));
        MailjetResponse response = client.post(request);
        return MessageResult.success();
    }

}