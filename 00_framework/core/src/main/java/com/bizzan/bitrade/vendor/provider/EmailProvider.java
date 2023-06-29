package com.bizzan.bitrade.vendor.provider;


import com.bizzan.bitrade.util.MessageResult;
import org.springframework.scheduling.annotation.Async;

import java.util.Map;

public interface EmailProvider {
    /**
     * 发送邮件
     *
     * @param email  邮箱
     * @param content 邮件内容
     * @return
     * @throws Exception
     */
    @Async
    MessageResult sendEmail(String email, String code, String subject, String templateName) throws Exception;

}
