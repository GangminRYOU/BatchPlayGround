package com.example.batchwithoutweb.chunktask.config.flatfile.writer;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batchwithoutweb.chunktask.domain.Customer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class FlatFilesDelimitedConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;


    @Bean
    public Job v1FlatFileWriterBatchJob(){
        return new JobBuilder("v1FlatFileWriterBatchJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(v1FlatFileWriterStep1())
            .build();
    }

    @Bean
    public Step v1FlatFileWriterStep1(){
        return new StepBuilder("v1FlatFileWriterStep1", jobRepository)
            .<Customer, Customer>chunk(10, transactionManager)
            .reader(v1ListItemReader())
            .writer(v1FlatFileItemWriter())
            .build();
    }

    @Bean
    public ItemWriter<? super Customer> v1FlatFileItemWriter() {
        return new FlatFileItemWriterBuilder<Customer>()
            .name("v1FlatFileItemWriter")
            .resource(new FileSystemResource("C:\\Users\\dbrkd\\GitFolder\\PrivateProject\\BatchWithOutWeb\\src\\main\\resources\\customer.txt"))
            .append(true)
            .delimited()
            .delimiter("|")
            .names(new String[]{"name", "age", "year"})
            .build();
    }

    @Bean
    public ItemReader<? extends Customer> v1ListItemReader() {
        List<Customer> customers = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            customers.add(new Customer("user" + i, 40 + i, 2000 + i + ""));
        }
        ListItemReader<Customer> customerListItemReader = new ListItemReader<>(customers);
        return customerListItemReader;
    }
}
