package com.kairos.config.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

@EnableAsync
@Configuration
class AsyncConfiguration implements AsyncConfigurer {


    @Bean(name ="executorService",destroyMethod ="shutdown")
    public ExecutorService executorService(){
        return  Executors.newWorkStealingPool(5);

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
