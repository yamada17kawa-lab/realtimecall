package com.nuliyang;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@Slf4j
@EnableFeignClients(basePackages = "com.nuliyang.feign")
public class AuthApplication {

    public static void main(String[] args) {

        SpringApplication.run(AuthApplication.class, args);
        log.info("认证服务启动成功……");
    }
}
