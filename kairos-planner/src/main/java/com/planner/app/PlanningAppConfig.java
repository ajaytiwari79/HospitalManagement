package com.planner.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.planner")
@EnableAsync
@EnableScheduling
public class PlanningAppConfig {
    public static void main(String[] args) {
        //ch.qos.logback.classic.turbo.TurboFilter tf=null;
        SpringApplication.run(PlanningAppConfig.class, args);
    }
}
