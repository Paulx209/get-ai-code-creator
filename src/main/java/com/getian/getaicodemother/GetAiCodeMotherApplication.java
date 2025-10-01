package com.getian.getaicodemother;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.getian.getaicodemother.mapper")
public class GetAiCodeMotherApplication {

    public static void main(String[] args) {
        SpringApplication.run(GetAiCodeMotherApplication.class, args);
    }

}
