package com.atguigu.realtime.gmall0508publisher;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.atguigu.realtime.gmall0508publisher.dao")
public class Gmall0508PublisherApplication {

    public static void main(String[] args) {
        SpringApplication.run(Gmall0508PublisherApplication.class, args);
    }

}
