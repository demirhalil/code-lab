package com.dev.concurrency.infrastructure.config;

import org.slf4j.MDC;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class ConcurrencyConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    /**
     * Tomcat's MaxThreads vs. AcceptCount (Conceptual explanation for the guide):
     * - AcceptCount: The maximum queue length for incoming connection requests when all possible request processing threads are in use.
     * - MaxThreads: The maximum number of request processing threads to be created by this Connector.
     *
     * Interaction with Spring's TaskExecutor:
     * When @Async is used, Tomcat threads hand off tasks to this TaskExecutor's thread pool.
     */
    @Bean(name = "orderTaskExecutor")
    public Executor orderTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(25);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("OrderThread-");
        executor.setTaskDecorator(new MdcTaskDecorator());
        executor.initialize();
        return executor;
    }

    /**
     * Context Propagation: MDC (Mapped Diagnostic Context) decorator.
     * Essential for tracking Correlation-IDs across asynchronous boundaries.
     */
    public static class MdcTaskDecorator implements TaskDecorator {
        @Override
        public Runnable decorate(Runnable runnable) {
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            return () -> {
                try {
                    if (contextMap != null) {
                        MDC.setContextMap(contextMap);
                    }
                    runnable.run();
                } finally {
                    MDC.clear();
                }
            };
        }
    }
}
