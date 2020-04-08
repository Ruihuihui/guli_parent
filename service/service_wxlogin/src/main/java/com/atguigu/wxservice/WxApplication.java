package com.atguigu.wxservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"com.atguigu"})
@SpringBootApplication//取消数据源自动配置
@MapperScan("com.atguigu.wxservice.mapper")
public class WxApplication {
    public static void main(String[] args) {
        SpringApplication.run(WxApplication.class, args);
    }

}
