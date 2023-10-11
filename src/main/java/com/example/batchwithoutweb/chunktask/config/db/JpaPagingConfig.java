package com.example.batchwithoutweb.chunktask.config.db;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batchwithoutweb.chunktask.domain.JpaPageCustomerV5;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class JpaPagingConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;
    private final EntityManagerFactory entityManagerFactory;
    private final int chunkSize = 10;

    @Bean
    public Job v9JpaPagingBatchJob(){
        return new JobBuilder("v9JpaPagingBatchJob", jobRepository)
            .start(v9JpaPagingStep1())
            .build();
    }

    @Bean
    public Step v9JpaPagingStep1(){
        return new StepBuilder("v9JpaPagingStep1", jobRepository)
            .<JpaPageCustomerV5, JpaPageCustomerV5>chunk(chunkSize, transactionManager)
            .reader(jpaPagingItemReader())
            .writer(new ItemWriter<JpaPageCustomerV5>() {
                @Override
                public void write(Chunk<? extends JpaPageCustomerV5> chunk) throws Exception {
                    chunk.forEach(item -> log.info("location={}", item.getAddress().getLocation()));
                }
            }).build();
    }

    @Bean
    public ItemReader<? extends JpaPageCustomerV5> jpaPagingItemReader(){
        return new JpaPagingItemReaderBuilder<JpaPageCustomerV5>()
            .name("jpaPagingItemReader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(chunkSize)
            .queryString("select c from JpaPageCustomerV5 c join fetch c.address")
            .build();
    }
}
