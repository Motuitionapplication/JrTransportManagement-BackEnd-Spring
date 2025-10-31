package com.playschool.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
	    "com.playschool.management", 
	    "com.playschool.management.security.jwt"
	})
@EntityScan("com.playschool.management.entity")
@EnableJpaRepositories("com.playschool.management.repository")
public class JrTransportManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(JrTransportManagementApplication.class, args);
    }
}
