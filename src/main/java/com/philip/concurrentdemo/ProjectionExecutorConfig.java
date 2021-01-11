package com.philip.concurrentdemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class ProjectionExecutorConfig {

    @Bean
    public ThreadPoolExecutor projectionExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(24);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(250);
        executor.setThreadNamePrefix("ProjectionExecutor-");
        executor.initialize();
        return executor.getThreadPoolExecutor();
    }

}
