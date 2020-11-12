package com.bizzan.bitrade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author Hevin QQ:390330302 E-mail:xunibidev@gmail.com
 * @create 2018年12月07日
 */
@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy=true)
public class WebApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}
