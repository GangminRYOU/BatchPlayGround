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
public class FlowAPIConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job v12BatchFlowJob(){
        return new JobBuilder("v12FlowJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(v12Flow1())
                .on("COMPLETED")
                .to(v12Flow2())
            .from(v12Flow1())
                .on("FAILED")
                .to(v12Flow3())
            .end()
            .build();
    }

    @Bean
    public Flow v12Flow1() {
        FlowBuilder<Flow> v12Flow1 = new FlowBuilder<>("v12Flow1");
        return v12Flow1.start(v12Step1())
            .next(v12Step2())
            .build();
    }

    @Bean
    public Flow v12Flow2() {
        FlowBuilder<Flow> v12Flow2 = new FlowBuilder<>("v12Flow2");
        return v12Flow2.start(v12Flow3())
            .next(v12Step5())
            .next(v12Step6())
            .build();
    }

    @Bean
    public Flow v12Flow3() {
        FlowBuilder<Flow> v12Flow3 = new FlowBuilder<>("v12Flow3");
        return v12Flow3.start(v12Step3())
            .next(v12Step4())
            .build();
    }

    @Bean
    public Step v12Step1() {
        return new StepBuilder("v12Step1", jobRepository)
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
    public Step v12Step2() {
        return new StepBuilder("v12Step2", jobRepository)
            .tasklet(new Tasklet() {
                @Override
                public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                    String stepName = chunkContext.getStepContext().getStepName();
                    log.info("stepName : {} has been executed!", stepName);
                    throw new RuntimeException("step2 has been failed");
                    //return RepeatStatus.FINISHED;
                }
            }, transactionManager)
            .build();
    }

    private Step v12Step3() {
        return new StepBuilder("v12Step3", jobRepository)
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

    private Step v12Step4() {
        return new StepBuilder("v12Step4", jobRepository)
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

    private Step v12Step5() {
        return new StepBuilder("v12Step5", jobRepository)
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

    private Step v12Step6() {
        return new StepBuilder("v12Step6", jobRepository)
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
