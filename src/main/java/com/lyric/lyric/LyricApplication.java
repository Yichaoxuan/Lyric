package com.lyric.lyric;

import com.lyric.lyric.Service.weather.GetWeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class LyricApplication {

       private static final Logger logger = LoggerFactory.getLogger(LyricApplication.class);


    public static void main(String[] args) {
        SpringApplication.run(LyricApplication.class, args);
    }

    /**
     * 应用启动完成后异步执行天气获取任务
     * 避免阻塞应用启动流程
     *
     * @param getWeatherService 天气服务
     * @return ApplicationRunner 实例
     */
    @Bean
    public ApplicationRunner weatherInitializationRunner(GetWeatherService getWeatherService) {
        return args -> {
            logger.info("=== 应用启动完成，将异步执行初始天气获取任务 ===");

            // 异步执行，不阻塞启动流程
            new Thread(() -> {
                try {
                    getWeatherService.processWeatherForDiary();
                    logger.info("=== 初始天气获取任务执行完成 ===");
                } catch (Exception e) {
                    logger.error("=== 初始天气获取任务执行失败 ===", e);
                }
            }, "Weather-Init-Task").start();
        };
    }
}
