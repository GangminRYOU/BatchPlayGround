package com.example.batchwithoutweb.chunktask.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batchwithoutweb.chunktask.domain.Customer;
import com.example.batchwithoutweb.chunktask.mapper.CustomerFieldSetMapper;
import com.example.batchwithoutweb.chunktask.mapper.DefaultLineMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class FlatFileConfigV2 {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    
    @Bean
    public Job v2FlatFileBatchJob(){
        return new JobBuilder("v2FlatFileBatchJob", jobRepository)
            .start(v2FlatFileStep1())
            .next(v2FlatFileStep2())
            .build();
    }

    //DefaultLineMapper를 기본으로 사용한다.

    @Bean
    public ItemReader v2FlatFileItemReader(){
        return new FlatFileItemReaderBuilder<Customer>()
            .name("v2FlatFileItemReader")
            .resource(new ClassPathResource("/customer.csv"))
            //.fieldSetMapper(new CustomerFieldSetMapper())
            .fieldSetMapper(new BeanWrapperFieldSetMapper<>())
            .targetType(Customer.class)
            .linesToSkip(1)
            .delimited().delimiter(",")
            .names("name", "age", "year")
            .build();
    }

    @Bean
    public Step v2FlatFileStep1(){
        return new StepBuilder("v2FlatFileStep1", jobRepository)
            .<String, Customer>chunk(5, transactionManager)
            .reader(v2FlatFileItemReader())
            .writer(new ItemWriter<Customer>() {
                @Override
                public void write(Chunk<? extends Customer> chunk) throws Exception {
                    log.info("items={}", chunk.getItems());
                }
            })
            .build();
    }

    @Bean
    public Step v2FlatFileStep2(){
        return new StepBuilder("v2FlatFileStep2", jobRepository)
            .tasklet(new Tasklet() {
                @Override
                public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                    log.info("-----------------------------------------------------------");
                    String stepName = chunkContext.getStepContext().getStepName();
                    log.info("stepName={}", stepName);
                    return RepeatStatus.FINISHED;
                }
            }, transactionManager)
            .build();
    }
}
