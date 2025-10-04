package com.starkeys.be.common.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(
        basePackages = "com.starkeys.be.adapter.out.mongo"
)
@Configuration
class MongoDBConfiguration {

}
