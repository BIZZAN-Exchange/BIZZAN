package com.bizzan.bitrade.config;

import com.bizzan.bitrade.vendor.provider.SMSProvider;
import com.bizzan.bitrade.vendor.provider.support.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmsProviderConfig {

    @Value("${sms.gateway:}")
    private String gateway;
    @Value("${sms.username:}")
    private String username;
    @Value("${sms.password:}")
    private String password;
    @Value("${sms.account:}")
    private String account;

    @Value("${sms.sign:}")
    private String sign;
    @Value("${sms.internationalGateway:}")
    private String internationalGateway;
    @Value("${sms.internationalUsername:}")
    private String internationalUsername;
    @Value("${sms.internationalPassword:}")
    private String internationalPassword;
    @Value("${access.key.id:}")
    private String accessKey;
    @Value("${access.key.secret:}")
    private String accessSecret;

    //阿里云短信调用
    @Value("${aliyun.mail-sms.region}")
    private String ali_Region;
    @Value("${aliyun.mail-sms.access-key-id}")
    private String ali_accessKeyId;
    @Value("${aliyun.mail-sms.access-secret}")
    private String ali_accessSecret;
    @Value("${aliyun.mail-sms.sms-sign}")
    private String ali_smsSign;
    @Value("${aliyun.mail-sms.sms-template}")
    private String ali_smsTemplate;

    @Bean
    public SMSProvider getSMSProvider(@Value("${sms.driver:}") String driverName) {
        SMSProvider provider = null;
        switch (driverName){
            case "aliyun":
                provider = new AliyunSMSGlobeProvider(ali_accessKeyId, ali_accessSecret);
                break;
            case "mxtong":
                provider = new MxtongSMSProvider(username, password,account,sign);
                break;
            case "test":
                provider = new TestSMSGlobeProvider(ali_accessKeyId, ali_accessSecret);
                break;
            case "saiyou":
                provider = new SaiyouSMSProvider(username, password, sign, gateway);
                break;
            case "feige":
                provider = new FeigeSMSProvider(sign,username,password);
                break;
        }
        return provider;
    }
}
