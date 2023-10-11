package com.example.batchwithoutweb.chunktask.config.json;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batchwithoutweb.chunktask.domain.CustomerV2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class JsonConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job v5JsonBatchJob(){
        return new JobBuilder("v5JsonBatchJob", jobRepository)
            .start(v4JsonStep1())
            .build();
    }

    @Bean
    public Step v4JsonStep1(){
        return new StepBuilder("v4JsonStep1", jobRepository)
            .<CustomerV2, CustomerV2>chunk(3, transactionManager)
            .reader(jsonItemReader())
            .writer(new ItemWriter<CustomerV2>() {
                @Override
                public void write(Chunk<? extends CustomerV2> chunk) throws Exception {
                    chunk.forEach(item -> log.info("item={}", item));
                }
            }).build();
    }

    @Bean
    public ItemReader<? extends CustomerV2> jsonItemReader(){
        return new JsonItemReaderBuilder<CustomerV2>()
            .name("jsonItemReader")
            .resource(new ClassPathResource("/customer.json"))
            .jsonObjectReader(new JacksonJsonObjectReader<>(CustomerV2.class))
            .build();
    }



}
