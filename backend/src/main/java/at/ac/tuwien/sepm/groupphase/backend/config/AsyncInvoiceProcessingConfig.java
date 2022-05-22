package at.ac.tuwien.sepm.groupphase.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncInvoiceProcessingConfig implements AsyncConfigurer {

    /**
     * Executor for processing invoices (e.g. pdf generation, email notifications, ...)
     */
    @Override
    @Bean(name = "invoiceProcessingPoolTaskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1); // after X threads the executor prefers to add to the queue
        executor.setMaxPoolSize(3); // in special cases the core pool size gets exceeded, then there is a maximum of Y threads available
        executor.setThreadNamePrefix("invoice-processing-");
        executor.initialize();
        return executor;
    }
}
