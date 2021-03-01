package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.entity.Alipay;
import com.bizzan.bitrade.entity.BankInfo;
import com.bizzan.bitrade.entity.WechatPay;

import lombok.Builder;
import lombok.Data;

/**
 * @author Hevin QQ:390330302 E-mail:xunibidev@gmail.com
 * @date 2020年01月20日
 */
@Builder
@Data
public class PayInfo {
    private String realName;
    private Alipay alipay;
    private WechatPay wechatPay;
    private BankInfo bankInfo;
}
