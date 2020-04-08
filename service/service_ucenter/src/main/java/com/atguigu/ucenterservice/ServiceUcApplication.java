package com.atguigu.ucenterservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"com.atguigu"})
@SpringBootApplication//取消数据源自动配置
@MapperScan("com.atguigu.ucenterservice.mapper")
@EnableEurekaClient//服务发现
public class ServiceUcApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceUcApplication.class, args);
    }
}
