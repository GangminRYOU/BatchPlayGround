package com.example.batchwithoutweb.chunktask.config.db.reader;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batchwithoutweb.chunktask.domain.JpaCustomerV4;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class JpaCursorConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;
    private final EntityManagerFactory entityManagerFactory;
    private final int chunkSize = 5;

    @Bean
    public Job v7JpaCursorBatchJob(){
        return new JobBuilder("v7JpaCursorBatchJob", jobRepository)
            .start(v7JpaCursorStep1())
            .build();
    }

    @Bean
    public Step v7JpaCursorStep1(){
        return new StepBuilder("v7JpaCursorStep1", jobRepository)
            .<JpaCustomerV4, JpaCustomerV4>chunk(chunkSize, transactionManager)
            .reader(jpaCursorItemReader())
            .writer(new ItemWriter<JpaCustomerV4>() {
                @Override
                public void write(Chunk<? extends JpaCustomerV4> chunk) throws Exception {
                    chunk.forEach(item -> log.info("item={}", item));
                }
            }).build();
    }

    @Bean
    public ItemReader<? extends JpaCustomerV4> jpaCursorItemReader(){
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("firstName", "A%");
        return new JpaCursorItemReaderBuilder<JpaCustomerV4>()
            .name("jpaCursorItemReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("SELECT c FROM JpaCustomerV4 c WHERE firstName LIKE :firstName")
            .parameterValues(parameters)
            .build();
    }
}
