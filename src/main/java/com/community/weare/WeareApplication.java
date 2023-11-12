package com.community.weare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
public class WeareApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeareApplication.class, args);
    }

}
