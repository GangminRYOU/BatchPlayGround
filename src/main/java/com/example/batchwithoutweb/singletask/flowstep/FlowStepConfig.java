package com.example.batchwithoutweb.singletask.flowstep;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
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
public class FlowStepConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job v14BatchFlowJob(){
        return new JobBuilder("v14FlowJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(v14FlowStep())
            .next(v14Step2())
            .build();
    }

    private Step v14FlowStep() {
        return new StepBuilder("v14FlowStep1", jobRepository)
            .flow(v14Flow1())
            .build();
    }

    private Flow v14Flow1() {
        FlowBuilder<Flow> v14Flow1 = new FlowBuilder<>("v14Flow1");
        return v14Flow1.start(v14Step1())
            .build();
    }

    @Bean
    public Step v14Step1() {
        return new StepBuilder("v14Step1", jobRepository)
            .tasklet(new Tasklet() {
                @Override
                public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                    String stepName = chunkContext.getStepContext().getStepName();
                    log.info("stepName : {} has been executed!", stepName);
                    throw new RuntimeException("step1 was failed");
                    //return RepeatStatus.FINISHED;
                }
            }, transactionManager)
            .build();
    }

    @Bean
    public Step v14Step2() {
        return new StepBuilder("v14Step2", jobRepository)
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

    private Step v14Step3() {
        return new StepBuilder("v14Step3", jobRepository)
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
