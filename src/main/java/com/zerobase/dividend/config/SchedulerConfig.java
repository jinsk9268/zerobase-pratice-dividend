package com.zerobase.dividend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class SchedulerConfig implements SchedulingConfigurer {
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler threadPool = new ThreadPoolTaskScheduler();

        // 코어 개수 가져오기
        int coreNum = Runtime.getRuntime().availableProcessors();
        threadPool.setPoolSize(coreNum); // * 2 해주거나 + 1 해주기
        threadPool.initialize();

        // 스케쥴러에서 우리가 만든 쓰레드풀 사용
        taskRegistrar.setTaskScheduler(threadPool);
    }
}
