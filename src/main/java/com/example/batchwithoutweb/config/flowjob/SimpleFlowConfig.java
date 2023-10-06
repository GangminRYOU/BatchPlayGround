package com.example.batchwithoutweb.config.flowjob;

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
public class SimpleFlowConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job v13BatchFlowJob(){
        return new JobBuilder("v13FlowJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(v13Step1())
            .on("COMPLETED").to(v13Step2())
            .from(v13Step1())
            .on("FAILED").to(v13Flow1())
            .end()
            .build();
    }

    private Flow v13Flow1() {
        FlowBuilder<Flow> v11Flow1 = new FlowBuilder<>("v11Flow1");
        return v11Flow1.start(v13Step2())
            .on("*")
            .to(v13Step3())
            .build();
    }

    @Bean
    public Step v13Step1() {
        return new StepBuilder("v13Step1", jobRepository)
            .tasklet(new Tasklet() {
                @Override
                public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                    String stepName = chunkContext.getStepContext().getStepName();
                    log.info("stepName : {} has been executed!", stepName);
                    //throw new RuntimeException("step1 was failed");
                    return RepeatStatus.FINISHED;
                }
            }, transactionManager)
            .build();
    }

    @Bean
    public Step v13Step2() {
        return new StepBuilder("v13Step2", jobRepository)
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

    private Step v13Step3() {
        return new StepBuilder("v13Step3", jobRepository)
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
