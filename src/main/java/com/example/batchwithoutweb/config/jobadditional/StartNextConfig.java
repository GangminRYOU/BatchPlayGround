package com.example.batchwithoutweb.config.jobadditional;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class StartNextConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job v7BatchFlowJob(){
        return new JobBuilder("v7FlowJob", jobRepository)
            .start(flowA())
            .next(v6StepA())
            .next(flowB())
            .next(v6StepB())
            .end()
            .build();
    }

    @Bean
    public Flow flowB(){
        FlowBuilder<Flow> v6FlowA = new FlowBuilder<>("v6FlowB");
        return v6FlowA.start(v7Step3())
            .next(v6Step4())
            .build();
    }

    @Bean
    public Flow flowA(){
        FlowBuilder<Flow> v6FlowA = new FlowBuilder<>("v6FlowA");
        return v6FlowA.start(v7Step1())
            .next(v7Step2())
            .build();
    }

    @Bean
    public Step v7Step1() {
        return new StepBuilder("v7Step1", jobRepository)
            .tasklet(new Tasklet() {
                @Override
                public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                    String stepName = chunkContext.getStepContext().getStepName();
                    log.info("stepName : {} has been executed!", stepName);
                    contribution.setExitStatus(ExitStatus.FAILED);
                    //throw new RuntimeException("step1 was failed");
                    return RepeatStatus.FINISHED;
                }
            }, transactionManager)
            .build();
    }

    @Bean
    public Step v7Step2() {
        return new StepBuilder("v7Step2", jobRepository)
            .tasklet(new Tasklet() {
                @Override
                public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                    String stepName = chunkContext.getStepContext().getStepName();
                    log.info("stepName : {} has been executed!", stepName);
                    return RepeatStatus.FINISHED;
                }
            }, transactionManager)
            .build();
    }

    private Step v7Step3() {
        return new StepBuilder("v7Step3", jobRepository)
            .tasklet(new Tasklet() {
                @Override
                public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                    String stepName = chunkContext.getStepContext().getStepName();
                    log.info("stepName : {} has been executed!", stepName);
                    return RepeatStatus.FINISHED;
                }
            }, transactionManager)
            .build();
    }

    private Step v6Step4() {
        return new StepBuilder("v7Step4", jobRepository)
            .tasklet(new Tasklet() {
                @Override
                public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                    String stepName = chunkContext.getStepContext().getStepName();
                    log.info("stepName : {} has been executed!", stepName);
                    return RepeatStatus.FINISHED;
                }
            }, transactionManager)
            .build();
    }

    private Step v6StepA() {
        return new StepBuilder("v7StepA", jobRepository)
            .tasklet(new Tasklet() {
                @Override
                public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                    String stepName = chunkContext.getStepContext().getStepName();
                    log.info("stepName : {} has been executed!", stepName);
                    return RepeatStatus.FINISHED;
                }
            }, transactionManager)
            .build();
    }

    private Step v6StepB() {
        return new StepBuilder("v7StepB", jobRepository)
            .tasklet(new Tasklet() {
                @Override
                public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                    String stepName = chunkContext.getStepContext().getStepName();
                    log.info("stepName : {} has been executed!", stepName);
                    return RepeatStatus.FINISHED;
                }
            }, transactionManager)
            .build();
    }
}
