package com.bizzan.bitrade.controller;

import com.bizzan.bitrade.service.CurrencyService;
import com.bizzan.bitrade.util.MessageResult;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;


/**
 * @author Hevin  E-mail:bizzanhevin@gmail.com
 * @Description: coin
 * @date 2021/4/214:20
 */
@RestController
@RequestMapping("currency")
public class CurrencyController extends BaseController {
    @Autowired
    private CurrencyService currencyService;


    @GetMapping(value = "/findAll")
    public MessageResult findAll() {
        return success(currencyService.findAll());
    }


}
