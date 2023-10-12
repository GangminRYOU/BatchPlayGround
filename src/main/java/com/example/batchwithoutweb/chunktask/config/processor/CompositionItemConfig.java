package com.example.batchwithoutweb.chunktask.config.processor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batchwithoutweb.chunktask.config.service.v7CustomService;
import com.example.batchwithoutweb.chunktask.processor.CustomItemProcessorV3;
import com.example.batchwithoutweb.chunktask.processor.CustomItemProcessorV4;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CompositionItemConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job v1CompositionProcessorBatchJob(){
        return new JobBuilder("v1CompositionProcessorBatchJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(v1CompositionItemStep1())
            .build();
    }

    @Bean
    public Step v1CompositionItemStep1(){
        return new StepBuilder("v1CompositionItemStep1", jobRepository)
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
            .processor(v1CompositionItemProcessor())
            .writer(new ItemWriter<String>() {
                @Override
                public void write(Chunk<? extends String> chunk) throws Exception {
                    chunk.forEach(item -> log.info("item={}", item));
                }
            })
            .build();
    }

    @Bean
    public ItemProcessor<? super String, String> v1CompositionItemProcessor() {
        List processorList = new ArrayList<>();
        processorList.add(new CustomItemProcessorV3());
        processorList.add(new CustomItemProcessorV4());
        return new CompositeItemProcessorBuilder<>()
            .delegates(processorList)
            .build();
    }
}
