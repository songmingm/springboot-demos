package com.example.bigfile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@SpringBootApplication
public class BigFileDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(BigFileDemoApplication.class, args);
    }

}
