package com.micropos.delivery;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@EntityScan(basePackages = {"com.micropos.model"})
@SpringBootApplication(scanBasePackages = {"com.micropos.*"})
@EnableDiscoveryClient
public class DeliveryApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(DeliveryApplication.class);
        application.setWebApplicationType(WebApplicationType.REACTIVE);
        application.run(args);
    }

    private static final boolean NON_DURABLE = false;
    private static final String QUEUE_NAME = "OrderQueue";

    @Bean
    public Queue orderQueue() {
        return new Queue(QUEUE_NAME, NON_DURABLE);
    }


}
