package com.starkeys.be.common.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(
        basePackages = "com.starkeys.be.adapter.out.jpa"
)
@Configuration
class JpaConfiguration {

}
