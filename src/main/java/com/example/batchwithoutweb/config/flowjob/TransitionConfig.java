package com.example.batchwithoutweb.config.flowjob;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
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
public class TransitionConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job v8FlowBatchJob(){
        return new JobBuilder("v8FlowBatchJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            //하나의 Transition
            .start(v8Step1())
                .on("FAILED")
                .to(v8Step2())
                .on("FAILED")
                .stop()
            //두번째 Transition
            .from(v8Step1())
            //WildCard보다 FALIED라는 구체적인 값이 먼저 적용된다.
                .on("*")
                .to(v8Step3())
                .next(v8Step4())
            //세번쨰 Transition
            .from(v8Step2())
                .on("*")
                .to(v8Step5())
            //Simple Flow생성
                .end()
            .build();
    }

    @Bean
    public Step v8Step1() {
        return new StepBuilder("v8Step1", jobRepository)
            .tasklet(new Tasklet() {
                @Override
                public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                    String stepName = chunkContext.getStepContext().getStepName();
                    log.info("<<<<<<<<<<<<<< {} has ben executed >>>>>>>>>>>>>>>", stepName);
                    throw new RuntimeException("step1 has Failed");
                    //return RepeatStatus.FINISHED;
                }
            }, transactionManager)
            .build();
    }

    @Bean
    public Step v8Step2() {
        return new StepBuilder("v8Step2", jobRepository)
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
    public Step v8Step3() {
        return new StepBuilder("v8Step3", jobRepository)
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
    public Step v8Step4() {
        return new StepBuilder("v8Step4", jobRepository)
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
    public Step v8Step5() {
        return new StepBuilder("v8Step5", jobRepository)
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
    public Step v8Step6() {
        return new StepBuilder("v8Step6", jobRepository)
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
