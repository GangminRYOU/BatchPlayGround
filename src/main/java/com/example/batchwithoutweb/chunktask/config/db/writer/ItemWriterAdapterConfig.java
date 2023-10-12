package com.example.batchwithoutweb.chunktask.config.db.writer;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batchwithoutweb.chunktask.config.service.v7CustomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ItemWriterAdapterConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job v7ItemWriterAdapterBatchJob(){
        return new JobBuilder("v7ItemWriterAdapterBatchJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(v7ItemWriterAdapterStep1())
            .build();
    }

    @Bean
    public Step v7ItemWriterAdapterStep1(){
        return new StepBuilder("v7ItemWriterAdapterStep1", jobRepository)
            .<String, String>chunk(10, transactionManager)
            .reader(new ItemReader<String>() {
                int i = 0;
                @Override
                public String read() throws
                    Exception,
                    UnexpectedInputException,
                    ParseException,
                    NonTransientResourceException {
                    i++;
                    return i > 10 ? null : "item" + i;
                }
            })
            .writer(v7ItemWriterAdapter())
            .build();
    }

    @Bean
    public ItemWriter<? super String> v7ItemWriterAdapter() {
        ItemWriterAdapter<String> itemWriterAdapter = new ItemWriterAdapter<>();
        itemWriterAdapter.setTargetObject(v7CustomService());
        itemWriterAdapter.setTargetMethod("customWrite");
        return itemWriterAdapter;
    }

    @Bean
    public v7CustomService v7CustomService() {
        return new v7CustomService();
    }
}
