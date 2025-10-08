package com.getian.getaicodemother;

import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {RedisEmbeddingStoreAutoConfiguration.class})
@MapperScan("com.getian.getaicodemother.mapper")
public class GetAiCodeMotherApplication {

    public static void main(String[] args) {
        SpringApplication.run(GetAiCodeMotherApplication.class, args);
    }

}
