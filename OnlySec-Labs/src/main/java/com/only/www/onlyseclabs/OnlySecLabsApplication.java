package com.only.www.onlyseclabs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.only.www.onlyseclabs.mapper")
public class OnlySecLabsApplication {

    public static void main(String[] args) {

        SpringApplication.run(OnlySecLabsApplication.class, args);
    }

}
