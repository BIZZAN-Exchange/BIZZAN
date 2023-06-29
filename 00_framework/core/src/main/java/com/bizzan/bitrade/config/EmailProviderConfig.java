package com.bizzan.bitrade.config;

import com.bizzan.bitrade.vendor.provider.EmailProvider;
import com.bizzan.bitrade.vendor.provider.SMSProvider;
import com.bizzan.bitrade.vendor.provider.support.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailProviderConfig {

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

    @Bean
    public EmailProvider getEmailProvider(@Value("${email.driver:}") String driverName) {
        EmailProvider provider = null;
        switch (driverName){
            case "java":
                provider = new JaveEmailProvider(from,company);
                break;
            case "aliyun":
                provider = new AliyunEmailProvider(e_Region,e_accessKeyId,e_accessSecret);
                break;
            case "test":
                provider = new TestEmailProvider();
                break;
            case "jet":
                provider = new JetEmailProvider(e_accessKeyId, e_accessSecret);
                break;
        }
        return provider;
    }
}
