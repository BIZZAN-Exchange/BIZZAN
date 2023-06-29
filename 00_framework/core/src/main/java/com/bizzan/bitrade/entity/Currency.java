package com.bizzan.bitrade.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Hevin  E-mail:bizzanhevin@gmail.com
 * @description
 * @date 2021/12/29 14:14
 */
@Entity
@Data
@Table(name = "currency")
@ToString
public class Currency {
    @Id
    @Excel(name = "id", orderNum = "1", width = 20)
    private Long id;

    private String fullName;


    private String symbol;


    private BigDecimal rate;


    private String imageUrl;


    private Integer sort;


    private Integer status;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
