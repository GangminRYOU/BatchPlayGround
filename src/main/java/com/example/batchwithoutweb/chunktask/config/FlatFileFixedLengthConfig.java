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
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batchwithoutweb.chunktask.domain.Customer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class FlatFileFixedLengthConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job v3FlatFileBatchJob(){
        return new JobBuilder("v3FlatFileBatchJob", jobRepository)
            .start(v3FlatFileStep1())
            .next(v3FlatFileStep2())
            .build();
    }

    //DefaultLineMapper를 기본으로 사용한다.

    @Bean
    public ItemReader v3FlatFileItemReader(){
        return new FlatFileItemReaderBuilder<Customer>()
            .name("v3FlatFileItemReader")
            .resource(new FileSystemResource("C:\\Users\\dbrkd\\GitFolder\\PrivateProject\\BatchWithOutWeb\\src\\main\\resources\\customer.txt"))
            .fieldSetMapper(new BeanWrapperFieldSetMapper<>())
            .targetType(Customer.class)
            .linesToSkip(1)
            .fixedLength()
            //범위를 정하지 않으면 끝까지 다 읽는다.
            .addColumns(new Range(1,5))
            .addColumns(new Range(6,7))
            .addColumns(new Range(8,11))
            .strict(false)
            .names("name", "age", "year")
            .build();
    }

    @Bean
    public Step v3FlatFileStep1(){
        return new StepBuilder("v3FlatFileStep1", jobRepository)
            .<String, Customer>chunk(5, transactionManager)
            .reader(v3FlatFileItemReader())
            .writer(new ItemWriter<Customer>() {
                @Override
                public void write(Chunk<? extends Customer> chunk) throws Exception {
                    log.info("items={}", chunk.getItems());
                }
            })
            .build();
    }

    @Bean
    public Step v3FlatFileStep2(){
        return new StepBuilder("v3FlatFileStep2", jobRepository)
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
