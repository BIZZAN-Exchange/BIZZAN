package com.bizzan.bitrade.dto;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString(callSuper = true)
public class AgentWalletDTO extends BaseMemberDTO{

    private Long id;

    private String coinUnit ;

    private BigDecimal balance;
}
