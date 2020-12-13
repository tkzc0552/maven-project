package com.zhm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@MapperScan(basePackages = {"com.zhm.extension.mapper"})
@EntityScan(basePackages = {"com.zhm.*.entity"})
public class BootstrapApplication {
    public static void main(String[] args){
        SpringApplication.run(BootstrapApplication.class,args);
    }
}
