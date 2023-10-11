package com.example.batchwithoutweb.chunktask.config.db.reader;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batchwithoutweb.chunktask.config.service.CustomService;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ItemReaderAdapterConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;
    private final EntityManagerFactory entityManagerFactory;
    private final int chunkSize = 10;

    @Bean
    public Job v10ItemReaderAdapterBatchJob(){
        return new JobBuilder("v10ItemReaderAdapterBatchJob", jobRepository)
            .start(v10ItemReaderAdapterStep1())
            .build();
    }

    @Bean
    public Step v10ItemReaderAdapterStep1(){
        return new StepBuilder("v10ItemReaderAdapterStep1", jobRepository)
            .<String, String>chunk(chunkSize, transactionManager)
            .reader(ItemReaderAdapter())
            .writer(new ItemWriter<String>() {
                @Override
                public void write(Chunk<? extends String> chunk) throws Exception {
                    chunk.forEach(item -> log.info("item={}", item));
                }
            }).build();
    }

    @Bean
    public ItemReader<? extends String> ItemReaderAdapter(){
        ItemReaderAdapter<String> reader = new ItemReaderAdapter<>();
        reader.setTargetObject(customService());
        reader.setTargetMethod("customRead");
        return reader;
    }

    @Bean
    public Object customService() {
        return new CustomService();
    }
}
