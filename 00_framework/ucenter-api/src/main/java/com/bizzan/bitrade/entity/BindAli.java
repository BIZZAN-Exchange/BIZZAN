package com.bizzan.bitrade.entity;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author Hevin QQ:390330302 E-mail:xunibidev@gmail.com
 * @date 2020年01月16日
 */
@Data
public class BindAli {
    @NotBlank(message = "{BindAli.realName.null}")
    private String realName;
    @NotBlank(message = "{BindAli.ali.null}")
    private String ali;
    @NotBlank(message = "{BindAli.jyPassword.null}")
    private String jyPassword;
    /** 支付宝收款二维码 */
    private String qrCodeUrl;
}
