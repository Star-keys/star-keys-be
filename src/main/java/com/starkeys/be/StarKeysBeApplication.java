package com.starkeys.be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;

// Temp Configuration for ES
@SpringBootApplication(exclude = {ElasticsearchRestClientAutoConfiguration.class})
public class StarKeysBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(StarKeysBeApplication.class, args);
    }

}
