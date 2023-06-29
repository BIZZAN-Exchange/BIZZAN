package com.bizzan.er.market.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bizzan.er.market.engine.MarketEngine;

import com.bizzan.er.market.engine.MarketEngineFactory;
import com.bizzan.er.market.engine.MarketEngineHuobi;
import com.bizzan.er.market.engine.MarketEngineOkex;
import com.bizzan.er.market.engine.MarketEngineBinance;
import com.bizzan.er.market.engine.MarketEngineZb;
import com.bizzan.er.market.engine.MarketEngineFxh;
import com.bizzan.er.market.engine.MarketEngineBiki;

@Configuration
public class MarketEngineConfig {
    @Bean
    public MarketEngineFactory marketEngineFactory() {

        MarketEngineFactory factory = new MarketEngineFactory();

        MarketEngine huobiEngine = new MarketEngineHuobi();
        factory.addEngine("Huobi", huobiEngine);

        MarketEngine okexEngine = new MarketEngineOkex();
        factory.addEngine("Okex", okexEngine);

        MarketEngine binanceEngine = new MarketEngineBinance();
        factory.addEngine("Binance", binanceEngine);

		MarketEngine zbEngine = new MarketEngineZb();
		factory.addEngine("Zb", zbEngine);

//		MarketEngine bikiEngine = new MarketEngineBiki();
//		factory.addEngine("Biki", bikiEngine);

//		MarketEngine fxhEngine = new MarketEngineFxh();
//		factory.addEngine("Fxh", fxhEngine);

        return factory;
    }
}
