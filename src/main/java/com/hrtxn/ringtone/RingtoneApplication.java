package com.hrtxn.ringtone;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan({"com.hrtxn.ringtone.project.**"})
@EnableScheduling
@SpringBootApplication
public class RingtoneApplication {

    public static void main(String[] args) {
        SpringApplication.run(RingtoneApplication.class, args);
    }

}
