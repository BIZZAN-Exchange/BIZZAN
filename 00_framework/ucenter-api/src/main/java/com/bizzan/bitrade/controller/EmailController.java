package com.bizzan.bitrade.controller;

import com.bizzan.bitrade.constant.SysConstant;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.transform.AuthMember;
import com.bizzan.bitrade.service.LocaleMessageSourceService;
import com.bizzan.bitrade.service.MemberService;
import com.bizzan.bitrade.util.GeneratorUtil;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.vendor.provider.EmailProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import static com.bizzan.bitrade.constant.SysConstant.EMAIL_WITHDRAW_MONEY_CODE_PREFIX;
import static com.bizzan.bitrade.constant.SysConstant.SESSION_MEMBER;
import static com.bizzan.bitrade.util.MessageResult.error;
import static com.bizzan.bitrade.util.MessageResult.success;

/**
 * @author Hevin  E-mail:bizzanhevin@gmail.com
 * @date 2021年01月08日
 */
@Slf4j
@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MemberService memberService;
    @Resource
    private LocaleMessageSourceService localeMessageSourceService;

    @Autowired
    private EmailProvider emailProvider;

    /**
     * 重置交易密码验证码
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/transaction/code", method = RequestMethod.POST)
    public MessageResult sendResetTransactionCode(@SessionAttribute(SESSION_MEMBER) AuthMember user) throws Exception {
        Member member = memberService.findOne(user.getId());
        String email = member.getEmail();
        Assert.hasText(member.getEmail(), localeMessageSourceService.getMessage("NOT_BIND_EMAIL"));
        String randomCode = String.valueOf(GeneratorUtil.getRandomNumber(100000, 999999));
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String key = SysConstant.EMAIL_TRANSACTION_CODE_PREFIX + email;
        if (valueOperations.get(key) != null) {
            return error(localeMessageSourceService.getMessage("EMAIL_ALREADY_SEND"));
        }
        try {
            emailProvider.sendEmail(email, randomCode,null,"bindCodeEmail.ftl");
            valueOperations.set(key, randomCode, 10, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
            return error(localeMessageSourceService.getMessage("SEND_FAILED"));
        }
        return success(localeMessageSourceService.getMessage("SENT_SUCCESS_TEN"));
    }

    /**
     * 提币验证码
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/withdraw/code", method = RequestMethod.POST)
    public MessageResult withdrawCode(@SessionAttribute(SESSION_MEMBER) AuthMember user) throws Exception {
        Member member = memberService.findOne(user.getId());
        String email = member.getEmail();
        Assert.hasText(member.getEmail(), localeMessageSourceService.getMessage("NOT_BIND_EMAIL"));
        String randomCode = String.valueOf(GeneratorUtil.getRandomNumber(100000, 999999));
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String key = EMAIL_WITHDRAW_MONEY_CODE_PREFIX + email;
        if (valueOperations.get(key) != null) {
            return error(localeMessageSourceService.getMessage("EMAIL_ALREADY_SEND"));
        }
        try {
            emailProvider.sendEmail(email, randomCode,null,"bindCodeEmail.ftl");
            valueOperations.set(key, randomCode, 10, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
            return error(localeMessageSourceService.getMessage("SEND_FAILED"));
        }
        return success(localeMessageSourceService.getMessage("SENT_SUCCESS_TEN"));
    }

    /**
     * 更改登录密码验证码
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/update/password/code", method = RequestMethod.POST)
    public MessageResult updatePasswordCode(@SessionAttribute(SESSION_MEMBER) AuthMember user) throws Exception {
        Member member = memberService.findOne(user.getId());
        String email = member.getEmail();
        Assert.hasText(member.getEmail(), localeMessageSourceService.getMessage("NOT_BIND_EMAIL"));
        String randomCode = String.valueOf(GeneratorUtil.getRandomNumber(100000, 999999));
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String key = SysConstant.EMAIL_UP_PWD_CODE_PREFIX + email;
        if (valueOperations.get(key) != null) {
            return error(localeMessageSourceService.getMessage("EMAIL_ALREADY_SEND"));
        }
        try {
            emailProvider.sendEmail(email, randomCode,null,"bindCodeEmail.ftl");
            valueOperations.set(key, randomCode, 10, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
            return error(localeMessageSourceService.getMessage("SEND_FAILED"));
        }
        return success(localeMessageSourceService.getMessage("SENT_SUCCESS_TEN"));
    }

}
