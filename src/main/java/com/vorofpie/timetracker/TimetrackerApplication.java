package com.vorofpie.timetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class TimetrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimetrackerApplication.class, args);
    }

}
