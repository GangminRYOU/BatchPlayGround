package com.example.batchwithoutweb.chunktask.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batchwithoutweb.chunktask.reader.CustomItemStreamReader;
import com.example.batchwithoutweb.chunktask.writer.CustomItemStreamWriter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class IteamStreamChunkConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job v6ChunkJob(){
        return new JobBuilder("v6ChunkJob", jobRepository)
            .start(v6ChunkStep1())
            .next(v6ChunkStep2())
            .build();
    }

    @Bean
    public Step v6ChunkStep2() {
        return new StepBuilder("v6ChunkStep2", jobRepository)
            .<String, String>chunk(5, transactionManager)
            .reader(itemReader())
            .writer(itemWriter())
            .build();
    }

    @Bean
    public ItemWriter<? super String> itemWriter() {
       return new CustomItemStreamWriter();
    }

    @Bean
    public CustomItemStreamReader itemReader() {
        List<String> items = new ArrayList<>(10);
        for (int i = 0; i <= 10; i++) {
            items.add(String.valueOf(i));
        }
        return new CustomItemStreamReader(items);
    }

    @Bean
    public Step v6ChunkStep1() {
        return new StepBuilder("v6ChunkStep1", jobRepository)
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
