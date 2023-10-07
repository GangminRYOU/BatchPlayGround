package com.example.batchwithoutweb.singletask.jobadditional;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class LimitAllowJobConfig {
    @Bean
    public Job v4BatchJob(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return new JobBuilder("v4BatchJob", jobRepository)
            .start(v2TaskStep1(jobRepository, transactionManager))
            .next(v2TaskStep2(jobRepository, transactionManager))
            //.incrementer(new RunIdIncrementer())
            .build();
    }

    @Bean
    public Step v2TaskStep1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("v4TaskStep1", jobRepository)
            .tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED, transactionManager)
            .allowStartIfComplete(true)
            .build();
    }

    @Bean
    public Step v2TaskStep2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("v4TaskStep2", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                log.info("stepName : {}", contribution.getStepExecution().getStepName());
                log.info("jobName : {}", chunkContext.getStepContext().getJobName());
                throw new RuntimeException("Error occured");
                //return RepeatStatus.FINISHED;
            }, transactionManager)
            //will execute until three times execution
            .startLimit(3)
            .build();
    }
}
