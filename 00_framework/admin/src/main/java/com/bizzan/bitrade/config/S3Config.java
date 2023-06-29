package com.bizzan.bitrade.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("s3")
public class S3Config {
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String regionsName;


    public String toUrl(String key){
        return String.format("%s://%s.s3.%s.amazonaws.com/%s", "https",bucketName,regionsName,key);
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getRegionsName() {
        return regionsName;
    }

    public void setRegionsName(String regionsName) {
        this.regionsName = regionsName;
    }
}
