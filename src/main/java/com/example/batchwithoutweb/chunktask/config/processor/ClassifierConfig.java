package com.example.batchwithoutweb.chunktask.config.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.batch.item.support.ClassifierCompositeItemProcessor;
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batchwithoutweb.chunktask.processor.CustomItemProcessorV3;
import com.example.batchwithoutweb.chunktask.processor.CustomItemProcessorV4;
import com.example.batchwithoutweb.chunktask.processor.CustomItemProcessorV5;
import com.example.batchwithoutweb.chunktask.processor.CustomItemProcessorV6;
import com.example.batchwithoutweb.chunktask.processor.CustomItemProcessorV7;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ClassifierConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job v2ClassifierProcessorBatchJob(){
        return new JobBuilder("v2ClassifierProcessorBatchJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(v2ClassifierItemStep1())
            .build();
    }

    @Bean
    public Step v2ClassifierItemStep1(){
        return new StepBuilder("v2ClassifierItemStep1", jobRepository)
            .<ProcessorInfo, ProcessorInfo>chunk(10, transactionManager)
            .reader(new ItemReader<ProcessorInfo>() {
                int i = 0;
                @Override
                public ProcessorInfo read() throws
                    Exception,
                    UnexpectedInputException,
                    ParseException,
                    NonTransientResourceException {
                    i++;
                    ProcessorInfo info = ProcessorInfo.builder().id(i).build();
                    return i > 3 ? null : info;
                }
            })
            .processor(v2ClassifierItemProcessor())
            .writer(new ItemWriter<ProcessorInfo>() {
                @Override
                public void write(Chunk<? extends ProcessorInfo> chunk) throws Exception {
                    chunk.forEach(item -> log.info("item={}", item));
                }
            })
            .build();
    }

    @Bean
    public ItemProcessor<? super ProcessorInfo, ProcessorInfo> v2ClassifierItemProcessor() {
        ClassifierCompositeItemProcessor<ProcessorInfo, ProcessorInfo> processor = new ClassifierCompositeItemProcessor<>();
        ProcessorClassifier<ProcessorInfo, ItemProcessor<?, ? extends ProcessorInfo>> classifier = new ProcessorClassifier<>();
        Map<Integer, ItemProcessor<ProcessorInfo, ProcessorInfo>> processorMap = new HashMap<>();
        processorMap.put(1, new CustomItemProcessorV5());
        processorMap.put(2, new CustomItemProcessorV6());
        processorMap.put(3, new CustomItemProcessorV7());
        classifier.setProcessorMap(processorMap);
        processor.setClassifier(classifier);
        return processor;
    }
}
