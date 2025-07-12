package com.nntk.m2s;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScans({
        @MapperScan("com.nntk.m2s.mp.custom.mapper"),
        @MapperScan("com.nntk.m2s.mp.generate.mapper")
})
@EnableScheduling
public class MqServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MqServerApplication.class, args);
    }

}
