package com.example.batchwithoutweb.chunktask.config;

import java.util.Arrays;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batchwithoutweb.chunktask.domain.Customer;
import com.example.batchwithoutweb.chunktask.reader.CustomItemReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ItemReaderProcessorWriterConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ItemProcessor itemProcessor;
    private final ItemWriter itemWriter;

    @Bean
    public Job v5ChunkJob(){
        return new JobBuilder("v5ChunkJob", jobRepository)
            .start(v5ChunkStep1())
            .next(v5ChunkStep2())
            .build();
    }
    @Bean
    public Step v5ChunkStep1(){
        return new StepBuilder("v5ChunkStep1", jobRepository)
            .<String, String>chunk(2, transactionManager)
            .reader(new ListItemReader<>(Arrays.asList("item1", "item2", "item3", "item4", "item5", "item6")))
            .processor(new ItemProcessor<String, String>() {
                @Override
                public String process(String item) throws Exception {
                    return "my_" + item;
                }
            })
            .writer(new ItemWriter<String>() {
                @Override
                public void write(Chunk<? extends String> chunk) throws Exception {
                    chunk.forEach(item -> log.info("item : {}", item));
                }
            }).build();
    }

    @Bean
    public Step v5ChunkStep2(){
        return new StepBuilder("v5ChunkStep2", jobRepository)
            .<Customer, Customer>chunk(3, transactionManager)
            .reader(v5ChunkItemReader())
            .processor(itemProcessor)
            .writer(itemWriter)
            .build();
    }

    @Bean
    public ItemReader v5ChunkItemReader(){
        return new CustomItemReader(Arrays.asList(new Customer("user1"), new Customer("user2"), new Customer("user3")));
    }

    @Bean
    public Step v5ChunkStep3(){
        return new StepBuilder("v5ChunkStep3", jobRepository)
            .tasklet(new Tasklet() {
                @Override
                public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                    String stepName = chunkContext.getStepContext().getStepName();
                    log.info("{} has been executed", stepName);
                    return RepeatStatus.FINISHED;
                }
            }, transactionManager)
            .build();
    }
}
