package com.example.batchwithoutweb.chunktask.config.db.reader;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batchwithoutweb.chunktask.domain.JdbcCustomerV3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JdbcPagingConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;
    private final int chunkSize = 2;

    @Bean
    public Job v8JdbcPagingBatchJob() throws Exception {
        return new JobBuilder("v8JdbcPagingBatchJob", jobRepository)
            .start(v8JdbcPagingStep1())
            .build();
    }

    @Bean
    public Step v8JdbcPagingStep1() throws Exception {
        return new StepBuilder("v8JdbcPagingStep1", jobRepository)
            .<JdbcCustomerV3, JdbcCustomerV3>chunk(chunkSize, transactionManager)
            .reader(v8JdbcPagingItemReader())
            .writer(chunk -> {
                log.info("--------------write-----------------");
                chunk.forEach(item -> log.info("item={}", item));
            }).build();
    }
    @Bean
    public ItemReader<? extends JdbcCustomerV3>v8JdbcPagingItemReader() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("first_name", "A%");

        return new JdbcPagingItemReaderBuilder<JdbcCustomerV3>()
            .name("v8JdbcPagingItemReader")
            .pageSize(chunkSize)
            .dataSource(dataSource)
            .rowMapper(new BeanPropertyRowMapper<>(JdbcCustomerV3.class))
            .queryProvider(createQueryProvider())
            .parameterValues(parameters)
            .build();
    }

    @Bean
    public PagingQueryProvider createQueryProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean queryProviderFactory = new SqlPagingQueryProviderFactoryBean();
        queryProviderFactory.setDataSource(dataSource);
        queryProviderFactory.setSelectClause("id, first_name, last_name, birth_date");
        queryProviderFactory.setFromClause("FROM customer");
        queryProviderFactory.setWhereClause("WHERE first_name LIKE :first_name");

        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);

        queryProviderFactory.setSortKeys(sortKeys);
        return queryProviderFactory.getObject();
    }

}
