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
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batchwithoutweb.chunktask.domain.JdbcCustomerV3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JdbcBatchItemWriterConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    @Bean
    public Job v5JdbcWriterBatchJob(){
        return new JobBuilder("v5JdbcWriterBatchJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(v5JdbcWriteStep1())
            .build();
    }


    @Bean
    public Step v5JdbcWriteStep1(){
        return new StepBuilder("v5JdbcWriteStep1", jobRepository)
            .<JdbcCustomerV3, JdbcCustomerV3>chunk(10, transactionManager)
            .reader(v5JdbcPagingReader())
            .writer(v5JdbcBatchItemWriter())
            .build();
    }

    @Bean
    public ItemWriter<? super JdbcCustomerV3> v5JdbcBatchItemWriter() {
        return new JdbcBatchItemWriterBuilder<JdbcCustomerV3>()
            .dataSource(dataSource)
            .sql("insert into customer2 values(:id, :firstName, :lastName, :birthDate)")
            .beanMapped()
            .build();
    }

    @Bean
    public ItemReader<? extends JdbcCustomerV3> v5JdbcPagingReader(){
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
