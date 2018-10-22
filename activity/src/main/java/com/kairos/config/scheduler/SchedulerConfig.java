package com.kairos.config.scheduler;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EnableAsync
@EnableScheduling
@Configuration
public class SchedulerConfig implements SchedulingConfigurer,AsyncConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduler) {
        scheduler.setScheduler(threadPoolTaskExecutor());

    }

    @Bean(name = "schedulerTaskExecutor", destroyMethod = "shutdown")
    public Executor threadPoolTaskExecutor() {
        return Executors.newScheduledThreadPool(20);
    }

    @Bean(name ="executorService",destroyMethod ="shutdown")
    public ExecutorService executorService(){
        return  Executors.newWorkStealingPool();
    }

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(25);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("MyExecutor-");
        executor.initialize();
        return executor;
    }

}