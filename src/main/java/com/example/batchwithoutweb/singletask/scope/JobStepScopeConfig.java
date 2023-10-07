package com.example.batchwithoutweb.singletask.scope;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batchwithoutweb.singletask.listener.CustomStepExecutionListener;
import com.example.batchwithoutweb.singletask.listener.CustomJobListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JobStepScopeConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job v15ScopeJob(){
        return new JobBuilder("v15ScopeJob", jobRepository)
            .start(v15Step1(null))
            .next(v15Step2())
            .next(v15Step3())
            .listener(new CustomJobListener())
            .build();
    }

    @Bean
    @JobScope
    public Step v15Step1(@Value("#{jobParameters['message']}") String message){
        log.info("message is lazily updated : {}", message);
        return new StepBuilder("v15Step1", jobRepository)
            .tasklet(v15Tasklet1(null, null), transactionManager)
            .listener(new CustomStepExecutionListener())
            .build();
    }

    @Bean
    @StepScope
    public Tasklet v15Tasklet1(
        @Value("#{jobExecutionContext['name']}") String name,
        @Value("#{stepExecutionContext['name2']}") String name2
        ) {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                log.info("{} was lazily updated from jobExecutionContext", name);
                log.info("{} was lazily updated from stepExecutionContext", name2);
                String stepName = chunkContext.getStepContext().getStepName();
                log.info("<<<<<< {} has been executed >>>>>>", stepName);
                return RepeatStatus.FINISHED;
            }
        };
    }

    @Bean
    public Step v15Step2(){
        return new StepBuilder("v15Step2", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                String stepName = chunkContext.getStepContext().getStepName();
                log.info("<<<<<< {} has been executed >>>>>>", stepName);
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }

    @Bean
    public Step v15Step3(){
        return new StepBuilder("v15Step3", jobRepository)
            .tasklet(v15Tasklet2(null), transactionManager)
            .build();
    }

    @Bean
    @StepScope
    public Tasklet v15Tasklet2(@Value("#{stepExecutionContext['name3']}") String name3){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                log.info("{} was lazily updated from stepExecutionContext", name3);
                String stepName = chunkContext.getStepContext().getStepName();
                log.info("<<<<<< {} has been executed >>>>>>", stepName);
                return RepeatStatus.FINISHED;
            }
        };
    }
}
