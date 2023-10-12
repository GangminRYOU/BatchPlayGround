package com.example.batchwithoutweb.chunktask.config.db.writer;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batchwithoutweb.chunktask.domain.JdbcCustomerV3;
import com.example.batchwithoutweb.chunktask.domain.JpaCustomerV3;
import com.example.batchwithoutweb.chunktask.processor.CustomItemProcessorV2;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JpaItemWriterConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job v6JpaWriterBatchJob(){
        return new JobBuilder("v6JpaWriterBatchJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(v6JpaWriteStep1())
            .build();
    }


    @Bean
    public Step v6JpaWriteStep1(){
        return new StepBuilder("v6JpaWriteStep1", jobRepository)
            .<JdbcCustomerV3, JpaCustomerV3>chunk(10, transactionManager)
            .reader(v6JpaPagingReader())
            .processor(customItemProcessor())
            .writer(v6JpaBatchItemWriter())
            .build();
    }

    @Bean
    public ItemProcessor<? super JdbcCustomerV3, ? extends JpaCustomerV3> customItemProcessor() {
        return new CustomItemProcessorV2();
    }

    @Bean
    public ItemWriter<? super JpaCustomerV3> v6JpaBatchItemWriter() {
        return new JpaItemWriterBuilder<JpaCustomerV3>()
            .usePersist(true)
            .entityManagerFactory(entityManagerFactory)
            .build();
    }

    @Bean
    public ItemReader<? extends JdbcCustomerV3> v6JpaPagingReader(){
        JdbcPagingItemReader<JdbcCustomerV3> pagingItemReader = new JdbcPagingItemReader<>();
        pagingItemReader.setDataSource(dataSource);
        pagingItemReader.setFetchSize(10);
        pagingItemReader.setRowMapper(new BeanPropertyRowMapper<>(JdbcCustomerV3.class));

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("id, first_name, last_name, birth_date");
        queryProvider.setFromClause("from customer");
        queryProvider.setWhereClause("where first_name like :firstName");

        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);
        queryProvider.setSortKeys(sortKeys);
        pagingItemReader.setQueryProvider(queryProvider);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("firstName", "A%");
        pagingItemReader.setParameterValues(parameters);
        return pagingItemReader;
    }
}
