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
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batchwithoutweb.chunktask.domain.JdbcCustomerV3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class JdbcCursorConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;
    private final int chunkSize = 10;

    @Bean
    public Job v6JdbcCursorBatchJob(){
        return new JobBuilder("v6JdbcCursorBatchJob", jobRepository)
            .start(v6JdbcCursorStep1())
            .build();
    }

    @Bean
    public Step v6JdbcCursorStep1(){
        return new StepBuilder("v6JdbcCursorStep1", jobRepository)
            .<JdbcCustomerV3, JdbcCustomerV3>chunk(chunkSize, transactionManager)
            .reader(jdbcCursorItemReader())
            .writer(new ItemWriter<JdbcCustomerV3>() {
                @Override
                public void write(Chunk<? extends JdbcCustomerV3> chunk) throws Exception {
                    chunk.forEach(item -> log.info("item={}", item));
                }
            }).build();
    }

    @Bean
    public ItemReader<JdbcCustomerV3> jdbcCursorItemReader(){
        return new JdbcCursorItemReaderBuilder<JdbcCustomerV3>()
            .name("jdbcCursorItemReader")
            .fetchSize(chunkSize)
            .sql("SELECT id, first_name, last_name, birth_date FROM customer WHERE first_name LIKE ? ORDER BY last_name, first_name")
            .beanRowMapper(JdbcCustomerV3.class)
            .queryArguments("A%")
            .dataSource(dataSource)
            .build();
    }
}
