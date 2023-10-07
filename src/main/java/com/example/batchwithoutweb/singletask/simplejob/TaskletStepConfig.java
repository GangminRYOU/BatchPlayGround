package com.example.batchwithoutweb.singletask.simplejob;

import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batchwithoutweb.singletask.tasklet.CustomTasklet;

@Configuration
public class TaskletStepConfig {
    @Bean
    public Job v3BatchJob(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return new JobBuilder("v3BatchJob", jobRepository)
            .start(taskStep(jobRepository, transactionManager))
            .incrementer(new RunIdIncrementer())
            .build();
    }

    @Bean
    public Step taskStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("v3TaskStep1", jobRepository)
            .tasklet(new CustomTasklet(),transactionManager)
            .build();
    }

    @Bean
    public Step chunkStep(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return new StepBuilder("v3ChunkStep1", jobRepository)
            .<String, String>chunk(10, transactionManager)
            .reader(new ListItemReader<>(List.of("item1","item2","item3","item4","item5","item6")))
            .processor(new ItemProcessor<String, String>() {
                @Override
                public String process(String item) throws Exception {
                   return item.toUpperCase();
                }
            })
            .writer(new ItemWriter<String>() {
                @Override
                public void write(Chunk<? extends String> chunk) throws Exception {
                    List<? extends String> items = chunk.getItems();
                    items.forEach(System.out::println);
                }
            })
            .build();
    }
}
