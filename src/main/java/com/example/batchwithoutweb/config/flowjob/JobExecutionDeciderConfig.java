package com.example.batchwithoutweb.config.flowjob;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batchwithoutweb.config.decider.CustomDecider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class JobExecutionDeciderConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job v10BatchJob(){
        return new JobBuilder("v10BatchJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(v10Step1())
            .next(decider())
            .from(decider()).on("ODD").to(v10OddStep1())
            .from(decider()).on("EVEN").to(v10EvenStep1())
            .end()
            .build();

    }

    @Bean
    public JobExecutionDecider decider() {
        return new CustomDecider();
    }

    @Bean
    public Step v10Step1(){
        return new StepBuilder("v10Step1", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                String stepName = chunkContext.getStepContext().getStepName();
                log.info("<<<<< {} has beeen executed >>>>>>", stepName);
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }

    @Bean
    public Step v10EvenStep1(){
        return new StepBuilder("v10EvenStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                String stepName = chunkContext.getStepContext().getStepName();
                log.info("<<<<< {} has beeen executed >>>>>>", stepName);
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }
    @Bean
    public Step v10OddStep1(){
        return new StepBuilder("v10OddStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                String stepName = chunkContext.getStepContext().getStepName();
                log.info("<<<<< {} has beeen executed >>>>>>", stepName);
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }
}
