package com.planner.app;

import com.planner.repository.staffinglevel.StaffingLevelRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.planner")
@EnableAsync
@EnableScheduling
@SpringBootApplication
@EnableMongoRepositories(basePackages ={"com.planner.repository.staffinglevel"})

public class PlanningAppConfig {
    public static void main(String[] args) {
        //ch.qos.logback.classic.turbo.TurboFilter tf=null;
        SpringApplication.run(PlanningAppConfig.class, args);
    }
}
