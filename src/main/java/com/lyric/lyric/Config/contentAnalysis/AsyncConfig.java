package com.lyric.lyric.Config.contentAnalysis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步配置类
 * 用于配置和管理应用程序中的异步任务执行器
 *
 * @author Yichaoxuan
 * @since 2025-11-24
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * AI分析任务执行器
     * 创建一个专门用于AI分析任务的线程池执行器
     *
     * @return Executor 线程池执行器实例
     */
    @Bean(name = "aiAnalysisExecutor")
    public Executor aiAnalysisExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数为3
        executor.setCorePoolSize(3);
        // 设置最大线程数为10
        executor.setMaxPoolSize(10);
        // 设置队列容量为50
        executor.setQueueCapacity(50);
        // 设置线程名称前缀
        executor.setThreadNamePrefix("AI-Analysis-");
        // 设置拒绝策略为调用者运行策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 初始化执行器
        executor.initialize();
        return executor;
    }
}