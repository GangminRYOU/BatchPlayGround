package com.example.batchwithoutweb.singletask.simplejob;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batchwithoutweb.singletask.tasklet.ExecutionContextTasklet1;
import com.example.batchwithoutweb.singletask.tasklet.ExecutionContextTasklet2;
import com.example.batchwithoutweb.singletask.tasklet.ExecutionContextTasklet3;
import com.example.batchwithoutweb.singletask.tasklet.ExecutionContextTasklet4;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ExecutionContextConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ExecutionContextTasklet1 tasklet1;
    private final ExecutionContextTasklet2 tasklet2;
    private final ExecutionContextTasklet3 tasklet3;
    private final ExecutionContextTasklet4 tasklet4;


    @Bean
    public Job executionContextBatchJob(){
        return new JobBuilder("executionContextBatchJob", jobRepository)
            //.incrementer(new RunIdIncrementer())
            .start(executionContextBatchStep1())
            .next(executionContextBatchStep2())
            .next(executionContextBatchStep3())
            .next(executionContextBatchStep4())
            .build();
    }

    @Bean
    public Step executionContextBatchStep1(){
        return new StepBuilder("executionContextBatchStep1", jobRepository)
            .tasklet(tasklet1, transactionManager)
            .build();
    }

    @Bean
    public Step executionContextBatchStep2(){
        return new StepBuilder("executionContextBatchStep2", jobRepository)
            .tasklet(tasklet2, transactionManager)
            .build();
    }

    @Bean
    public Step executionContextBatchStep3(){
        return new StepBuilder("executionContextBatchStep3", jobRepository)
            .tasklet(tasklet3, transactionManager)
            .build();
    }

    @Bean
    public Step executionContextBatchStep4(){
        return new StepBuilder("executionContextBatchStep4", jobRepository)
            .tasklet(tasklet4, transactionManager)
            .build();
    }
}
