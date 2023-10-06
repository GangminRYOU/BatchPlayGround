package com.example.batchwithoutweb.config.jobadditional;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batchwithoutweb.listener.PasscheckingListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CustomExitStatusConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job v9FlowBatchJob(){
        return new JobBuilder("v9FlowBatchJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            //하나의 Transition
            .start(v9Step1())
                .on("FAILED")
                .to(v9Step2())
                .on("PASS")
                .stop()
                .end()
            .build();
    }

    @Bean
    public Step v9Step1() {
        return new StepBuilder("v9Step1", jobRepository)
            .tasklet(new Tasklet() {
                @Override
                public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                    String stepName = chunkContext.getStepContext().getStepName();
                    log.info("<<<<<<<<<<<<<< {} has ben executed >>>>>>>>>>>>>>>", stepName);
                    contribution.getStepExecution().setExitStatus(ExitStatus.FAILED);
                    //throw new RuntimeException("step1 has Failed");
                    return RepeatStatus.FINISHED;
                }
            }, transactionManager)
            .build();
    }

    @Bean
    public Step v9Step2() {
        return new StepBuilder("v9Step2", jobRepository)
            .tasklet(new Tasklet() {
                @Override
                public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                    String stepName = chunkContext.getStepContext().getStepName();
                    log.info("<<<<<<<<<<<<<< {} has ben executed >>>>>>>>>>>>>>>", stepName);
                    return RepeatStatus.FINISHED;
                }
            }, transactionManager)
            .listener(new PasscheckingListener())
            .build();
    }

    @Bean
    public Step v9Step3() {
        return new StepBuilder("v9Step3", jobRepository)
            .tasklet(new Tasklet() {
                @Override
                public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                    String stepName = chunkContext.getStepContext().getStepName();
                    log.info("<<<<<<<<<<<<<< {} has ben executed >>>>>>>>>>>>>>>", stepName);
                    return RepeatStatus.FINISHED;
                }
            }, transactionManager)
            .build();
    }

    @Bean
    public Step v9Step4() {
        return new StepBuilder("v9Step4", jobRepository)
            .tasklet(new Tasklet() {
                @Override
                public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                    String stepName = chunkContext.getStepContext().getStepName();
                    log.info("<<<<<<<<<<<<<< {} has ben executed >>>>>>>>>>>>>>>", stepName);
                    return RepeatStatus.FINISHED;
                }
            }, transactionManager)
            .build();
    }

    @Bean
    public Step v9Step5() {
        return new StepBuilder("v9Step5", jobRepository)
            .tasklet(new Tasklet() {
                @Override
                public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                    String stepName = chunkContext.getStepContext().getStepName();
                    log.info("<<<<<<<<<<<<<< {} has ben executed >>>>>>>>>>>>>>>", stepName);
                    return RepeatStatus.FINISHED;
                }
            }, transactionManager)
            .build();
    }

    @Bean
    public Step v9Step6() {
        return new StepBuilder("v9Step6", jobRepository)
            .tasklet(new Tasklet() {
                @Override
                public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                    String stepName = chunkContext.getStepContext().getStepName();
                    log.info("<<<<<<<<<<<<<< {} has ben executed >>>>>>>>>>>>>>>", stepName);
                    return RepeatStatus.FINISHED;
                }
            }, transactionManager)
            .build();
    }
}
