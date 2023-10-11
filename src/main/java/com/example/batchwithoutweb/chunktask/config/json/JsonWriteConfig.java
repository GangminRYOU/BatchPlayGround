package com.example.batchwithoutweb.chunktask.config.json;

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
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batchwithoutweb.chunktask.domain.JdbcCustomerV3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JsonWriteConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    @Bean
    public Job v4JsonWriteBatchJob(){
        return new JobBuilder("v4JsonWriteBatchJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(v4JsonWriteStep1())
            .build();
    }


    @Bean
    public Step v4JsonWriteStep1(){
        return new StepBuilder("v4JsonWriteStep1", jobRepository)
            .<JdbcCustomerV3, JdbcCustomerV3>chunk(10, transactionManager)
            .reader(jdbcPagingItemReaderForJson())
            .writer(v4JsonWriter())
            .build();
    }

    @Bean
    public ItemWriter<? super JdbcCustomerV3> v4JsonWriter() {
        return new JsonFileItemWriterBuilder<JdbcCustomerV3>()
            .name("v4JsonWriter")
            .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
            .resource(new FileSystemResource("C:\\Users\\dbrkd\\GitFolder\\PrivateProject\\BatchWithOutWeb\\src\\main\\resources\\customerV3.json"))
            .build();
    }

    @Bean
    public ItemReader<? extends JdbcCustomerV3> jdbcPagingItemReaderForJson(){
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
