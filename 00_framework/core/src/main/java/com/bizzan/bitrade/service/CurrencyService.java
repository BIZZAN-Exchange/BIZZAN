package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.CurrencyDao;
import com.bizzan.bitrade.entity.Currency;
import com.bizzan.bitrade.service.Base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author Hevin  E-mail:bizzanhevin@gmail.com
 * @description
 * @date 2021/12/29 14:50
 */
@Service
public class CurrencyService extends BaseService {
    @Autowired
    private CurrencyDao currencyDao;


    @Override
    public List<Currency> findAll() {
        return currencyDao.findAll();
    }

}
