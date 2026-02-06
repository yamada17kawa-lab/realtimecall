package com.nuliyang;


import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

//引入的exception依赖中使用了mp
//但网关服务又不需要mp
//所以需要排除DataSourceAutoConfiguration
@SpringBootApplication
@Slf4j
@EnableScheduling
@MapperScan("com.nuliyang.store")
public class SignalApplication {

    public static void main(String[] args) {
        SpringApplication.run(SignalApplication.class, args);
        log.info("信令服务启动成功……");
    }
}
