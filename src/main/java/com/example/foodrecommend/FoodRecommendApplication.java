package com.example.foodrecommend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.example.foodrecommend.mapper")
@SpringBootApplication
@EnableScheduling
public class FoodRecommendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodRecommendApplication.class, args);
    }

}
