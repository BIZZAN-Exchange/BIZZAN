package com.bizzan.bitrade.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * 国家
 *
 * @author Hevin  E-mail:bizzanhevin@gmail.com
 * @date 2020年02月10日
 */
@Data
@Entity
public class Country {
    /**
     * 中文名称
     */
    @Id
    private String zhName;

    /**
     * 英文名称
     */
    private String enName;
    /**
     * 英文名称
     */
    private String countryImageUrl;

    /**
     * 区号
     */
    private String areaCode;
    /**
     * 语言
     */
    private String language;

    /**
     * 当地货币缩写
     */
    private String localCurrency;

    private int sort;

    private int delFlag;

    @Transient
    private String name ;
}
