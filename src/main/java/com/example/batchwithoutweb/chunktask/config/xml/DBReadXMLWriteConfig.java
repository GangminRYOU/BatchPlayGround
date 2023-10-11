package com.example.batchwithoutweb.chunktask.config.xml;

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
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
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
public class DBReadXMLWriteConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    @Bean
    public Job v3XMLWriteBatchJob(){
        return new JobBuilder("v3XMLWriteBatchJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(v3XMLWriteStep1())
            .build();
    }


    @Bean
    public Step v3XMLWriteStep1(){
        return new StepBuilder("v3XMLWriteStep1", jobRepository)
            .<JdbcCustomerV3, JdbcCustomerV3>chunk(10, transactionManager)
            .reader(jdbcPagingItemReader())
            .writer(v3XMLWriter())
            .build();
    }

    @Bean
    public ItemWriter<? super JdbcCustomerV3> v3XMLWriter() {
        return new StaxEventItemWriterBuilder<JdbcCustomerV3>()
            .name("v3XMLWriter")
            .marshaller(itemMarshaller())
            .resource(new FileSystemResource("C:\\Users\\dbrkd\\GitFolder\\PrivateProject\\BatchWithOutWeb\\src\\main\\resources\\customerV2.xml"))
            .rootTagName("customer")
            .build();
    }

    @Bean
    public Marshaller itemMarshaller() {
        Map<String, Class<?>> aliases = new HashMap<>();
        aliases.put("customer", JdbcCustomerV3.class);
        aliases.put("id", Long.class);
        aliases.put("firstName", String.class);
        aliases.put("lastName", String.class);
        aliases.put("birthDate", String.class);

        XStreamMarshaller xStreamMarshaller = new XStreamMarshaller();
        xStreamMarshaller.setAliases(aliases);

        return xStreamMarshaller;
    }

    @Bean
    public ItemReader<? extends JdbcCustomerV3> jdbcPagingItemReader(){
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
