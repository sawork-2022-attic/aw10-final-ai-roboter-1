package com.micropos.products;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.web.config.EnableSpringDataWebSupport;


@SpringBootApplication
@EnableDiscoveryClient
@EnableCaching
public class ProductsApplication {

    public static void main(String[] args) {
//        SpringApplication.run(ProductsApplication.class, args);
        SpringApplication application = new SpringApplication(ProductsApplication.class);
        application.setWebApplicationType(WebApplicationType.REACTIVE);
        application.run(args);
    }

}