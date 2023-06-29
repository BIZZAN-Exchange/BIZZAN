package com.bizzan.bitrade.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.entity.Coin;
import com.bizzan.bitrade.entity.CoinThumb;
import com.bizzan.bitrade.processor.CoinProcessor;
import com.bizzan.bitrade.processor.CoinProcessorFactory;
import com.bizzan.bitrade.service.CoinService;
import com.bizzan.bitrade.service.ExchangeCoinService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.*;

/**
 * 币种汇率管理
 */
@Component
@Slf4j
@ToString
public class CoinExchangeRate {
    @Getter
    @Setter
    private BigDecimal usdCnyRate = new BigDecimal("6.45");

    @Getter
    @Setter
    private BigDecimal usdtCnyRate = new BigDecimal("6.42");

    @Getter
    @Setter
    private BigDecimal usdJpyRate = new BigDecimal("110.02");
    @Getter
    @Setter
    private BigDecimal usdHkdRate = new BigDecimal("7.8491");
    @Getter
    @Setter
    private BigDecimal sgdCnyRate = new BigDecimal("5.08");
    @Setter
    private CoinProcessorFactory coinProcessorFactory;


    private Map<String,BigDecimal> ratesMap = new HashMap<String,BigDecimal>(){{
        put("CNY",new BigDecimal("6.36"));
        put("JPY",new BigDecimal("6.36"));
        put("TWD",new BigDecimal("6.40"));
        put("USD",new BigDecimal("1.00"));
        put("EUR",new BigDecimal("0.91"));
        put("HKD",new BigDecimal("7.81"));
        put("SGD",new BigDecimal("1.36"));
        put("INR",new BigDecimal("82.34"));
    }};

    @Autowired
    private CoinService coinService;
    @Autowired
    private ExchangeCoinService exCoinService;


    public BigDecimal getUsdRate(String symbol) {
        log.info("CoinExchangeRate getUsdRate unit = " + symbol);
        if ("USDT".equalsIgnoreCase(symbol)) {
            log.info("CoinExchangeRate getUsdRate unit = USDT  ,result = ONE");
            return BigDecimal.ONE;
        } else if ("CNY".equalsIgnoreCase(symbol)) {
            log.info("CoinExchangeRate getUsdRate unit = CNY  ,result : 1 divide {}", this.usdtCnyRate);
            BigDecimal bigDecimal = BigDecimal.ONE.divide(usdtCnyRate, 4,BigDecimal.ROUND_DOWN).setScale(4, BigDecimal.ROUND_DOWN);
            return bigDecimal;
        }else if ("BITCNY".equalsIgnoreCase(symbol)) {
            BigDecimal bigDecimal = BigDecimal.ONE.divide(usdCnyRate, 4,BigDecimal.ROUND_DOWN).setScale(4, BigDecimal.ROUND_DOWN);
            return bigDecimal;
        } else if ("ET".equalsIgnoreCase(symbol)) {
            BigDecimal bigDecimal = BigDecimal.ONE.divide(usdCnyRate, 4,BigDecimal.ROUND_DOWN).setScale(4, BigDecimal.ROUND_DOWN);
            return bigDecimal;
        } else if ("JPY".equalsIgnoreCase(symbol)) {
            BigDecimal bigDecimal = BigDecimal.ONE.divide(usdJpyRate, 4,BigDecimal.ROUND_DOWN).setScale(4, BigDecimal.ROUND_DOWN);
            return bigDecimal;
        }else if ("HKD".equalsIgnoreCase(symbol)) {
            BigDecimal bigDecimal = BigDecimal.ONE.divide(usdHkdRate, 4,BigDecimal.ROUND_DOWN).setScale(4, BigDecimal.ROUND_DOWN);
            return bigDecimal;
        }
        String usdtSymbol = symbol.toUpperCase() + "/USDT";
        String btcSymbol = symbol.toUpperCase() + "/BTC";
        String ethSymbol = symbol.toUpperCase() + "/ETH";

        if (coinProcessorFactory != null) {
            if (coinProcessorFactory.containsProcessor(usdtSymbol)) {
                log.info("Support exchange coin = {}", usdtSymbol);
                CoinProcessor processor = coinProcessorFactory.getProcessor(usdtSymbol);
                if(processor == null) {
                    return BigDecimal.ZERO;
                }
                CoinThumb thumb = processor.getThumb();
                if(thumb == null) {
                    log.info("Support exchange coin thumb is null", thumb);
                    return BigDecimal.ZERO;
                }
                return thumb.getUsdRate();
            } else if (coinProcessorFactory.containsProcessor(btcSymbol)) {
                log.info("Support exchange coin = {}/BTC", btcSymbol);
                CoinProcessor processor = coinProcessorFactory.getProcessor(btcSymbol);
                if(processor == null) {
                    return BigDecimal.ZERO;
                }
                CoinThumb thumb = processor.getThumb();
                if(thumb == null) {
                    log.info("Support exchange coin thumb is null", thumb);
                    return BigDecimal.ZERO;
                }
                return thumb.getUsdRate();
            } else if (coinProcessorFactory.containsProcessor(ethSymbol)) {
                log.info("Support exchange coin = {}/ETH", ethSymbol);
                CoinProcessor processor = coinProcessorFactory.getProcessor(ethSymbol);
                if(processor == null) {
                    return BigDecimal.ZERO;
                }
                CoinThumb thumb = processor.getThumb();
                if(thumb == null) {
                    log.info("Support exchange coin thumb is null", thumb);
                    return BigDecimal.ZERO;
                }
                return thumb.getUsdRate();
            } else {
                return getDefaultUsdRate(symbol);
            }
        } else {
            return getDefaultUsdRate(symbol);
        }
    }

    /**
     * 获取币种设置里的默认价格
     *
     * @param symbol
     * @return
     */
    public BigDecimal getDefaultUsdRate(String symbol) {
        Coin coin = coinService.findByUnit(symbol);
        if (coin != null) {
            return new BigDecimal(coin.getUsdRate());
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getCnyRate(String symbol) {
        if ("CNY".equalsIgnoreCase(symbol)) {
            return BigDecimal.ONE;
        } else if("ET".equalsIgnoreCase(symbol)){
            return BigDecimal.ONE;
        }
        return getUsdRate(symbol).multiply(usdtCnyRate).setScale(2, RoundingMode.DOWN);
    }



    public BigDecimal getJpyRate(String symbol) {
        if ("JPY".equalsIgnoreCase(symbol)) {
            return BigDecimal.ONE;
        }
        return getUsdRate(symbol).multiply(usdJpyRate).setScale(2, RoundingMode.DOWN);
    }

    public BigDecimal getHkdRate(String symbol) {
        if ("HKD".equalsIgnoreCase(symbol)) {
            return BigDecimal.ONE;
        }
        return getUsdRate(symbol).multiply(usdHkdRate).setScale(2, RoundingMode.DOWN);
    }


    /**
     * 每5分钟同步一次价格
     *
     * @throws UnirestException
     */

    @Scheduled(cron = "0 */5 * * * *")
    public void syncUsdtCnyPrice() {
        log.info("开始同步OTC");

        JSONObject jsonObject = new JSONObject();
        List<String> coins= new ArrayList<>();
        coins.add("USDT");
        jsonObject.put("assets", coins);

        jsonObject.put("tradeType", "BUY");
        jsonObject.put("fromUserRole", "USER");

        Set<String> currencies = ratesMap.keySet();
        for (String currency : currencies) {
            jsonObject.put("fiatCurrency", currency);
            // binance
            String urlOk="https://p2p.binance.com/bapi/c2c/v2/public/c2c/adv/quoted-price";
            try {
                HttpResponse<JsonNode> resp = Unirest.post(urlOk).header("accept", "application/json").header("content-type", "application/json").body(jsonObject.toJSONString()).asJson();
                if(resp.getStatus() == 200) { //正确返回
                    JSONObject ret = JSON.parseObject(resp.getBody().toString());
                    if("000000".equals(ret.getString("code"))){
                        JSONArray data = ret.getJSONArray("data");
                        if(data!=null && data.size()>0){
                            JSONObject res = (JSONObject) data.get(0);
                            BigDecimal referencePrice = res.getBigDecimal("referencePrice");
                            ratesMap.put(currency,referencePrice);
                            if("CNY".equals(currency)){
                                usdCnyRate = referencePrice;
                                usdtCnyRate = referencePrice;
                            }else if("JPY".equals(currency)){
                                usdJpyRate = referencePrice;
                            }else if("HKD".equals(currency)){
                                usdHkdRate = referencePrice;
                            }else if("SGD".equals(currency)){
                                sgdCnyRate = referencePrice;
                            }
                        }
                    }
                }

            } catch (UnirestException e) {
                log.info("开始同步OTC报错");
                log.error(e.toString());
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {

        Map<String,BigDecimal> ratesMap = new HashMap<String,BigDecimal>(){{
            put("CNY",new BigDecimal("6.36"));
        }};

        JSONObject jsonObject = new JSONObject();
        List<String> coins= new ArrayList<>();
        coins.add("USDT");
        jsonObject.put("assets", coins);
        jsonObject.put("tradeType", "BUY");
        jsonObject.put("fromUserRole", "USER");

        Set<String> currencies = ratesMap.keySet();
        for (String currency : currencies) {
            jsonObject.put("fiatCurrency", "CNY");
            // okex接口
            String urlOk="https://p2p.binance.com/bapi/c2c/v2/public/c2c/adv/quoted-price";
            Unirest.setProxy(new HttpHost("172.21.112.246", 7890));
            try {
                HttpResponse<JsonNode> resp = Unirest.post(urlOk).header("accept", "application/json").header("content-type", "application/json").body(jsonObject.toJSONString()).asJson();
                if(resp.getStatus() == 200) { //正确返回
                    JSONObject ret = JSON.parseObject(resp.getBody().toString());
                    if("000000".equals(ret.getString("code"))){
                        JSONArray data = ret.getJSONArray("data");
                        if(data!=null && data.size()>0){
                            JSONObject res = (JSONObject) data.get(0);
                            BigDecimal referencePrice = res.getBigDecimal("referencePrice");
                            System.out.println(referencePrice);

                        }
                    }

                }
            } catch (UnirestException e) {
                log.info("开始同步OTC报错");
                log.error(e.toString());
                e.printStackTrace();
            }
        }
    }

    /**
     * 每30分钟同步一次价格
     *
     * @throws UnirestException
     */

    @Scheduled(cron = "0 */30 * * * *")
    public void syncPrice() throws UnirestException {

        String url = "http://web.juhe.cn:8080/finance/exchange/frate?key=0330f6e51631ee1c0c4696a49201cb94";
        //如有报错 请自行官网申请获取汇率 或者默认写死
        HttpResponse<JsonNode> resp = Unirest.get(url)
                .queryString("key", "0330f6e51631ee1c0c4696a49201cb94")
                .asJson();
        log.info("forex result:{}", resp.getBody());
        JSONObject ret = JSON.parseObject(resp.getBody().toString());

        if(ret.getIntValue("resultcode") == 200) {
            JSONArray result = ret.getJSONArray("result");
            result.forEach(json -> {
                JSONObject obj = (JSONObject) json;
                if ("USDCNY".equals(obj.getString("code"))) {
                    setUsdCnyRate(new BigDecimal(obj.getDouble("price")).setScale(2, RoundingMode.DOWN));
                    log.info(obj.toString());
                } else if ("USDJPY".equals(obj.getString("code"))) {
                    setUsdJpyRate(new BigDecimal(obj.getDouble("price")).setScale(2, RoundingMode.DOWN));
                    log.info(obj.toString());
                }
                else if ("USDHKD".equals(obj.getString("code"))) {
                    setUsdHkdRate(new BigDecimal(obj.getDouble("price")).setScale(2, RoundingMode.DOWN));
                    log.info(obj.toString());
                }
	            /*
	            else if("SGDCNH".equals(obj.getString("code"))){
	                setSgdCnyRate(new BigDecimal(obj.getDouble("price")).setScale(2,RoundingMode.DOWN));
	                log.info(obj.toString());
	            }
	            */

            });
        }
    }

    public Map<String, BigDecimal> getAllRate(String symbol) {
        Map<String,BigDecimal> result = new HashMap<>();
        for (String currency : ratesMap.keySet()) {
            if ("CNY".equalsIgnoreCase(symbol)) {
                result.put(currency,BigDecimal.ONE);
                continue;
            } else if("ET".equalsIgnoreCase(symbol)){
                result.put(currency,BigDecimal.ONE);
                continue;
            }
            BigDecimal usdtRate = ratesMap.get(currency);
            BigDecimal rate = getUsdRate(symbol).multiply(usdtRate).setScale(2, RoundingMode.DOWN);
            result.put(currency,rate);
        }
        return result;
    }
}
