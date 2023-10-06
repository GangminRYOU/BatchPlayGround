package com.example.batchwithoutweb.config.simplejob;

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
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class StepJobConfig {

    @Bean
    public Job v2BatchJob(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return new JobBuilder("v2BatchJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(v2Step1(jobRepository, transactionManager))
            .next(v2Step2(jobRepository, transactionManager))
            .next(v2Step3(jobRepository, transactionManager))
            .build();
    }

    @Bean
    public Step v2Step1(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return new StepBuilder("v2Step1", jobRepository)
            .tasklet(new Tasklet() {
                @Override
                public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                    log.info("v2Step1 has been executed");
                    return RepeatStatus.FINISHED;
                }
            }, transactionManager)
            .build();
    }

    @Bean
    public Step v2Step2(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return new StepBuilder("v2Step2", jobRepository)
            .<String, String>chunk(3, transactionManager)
            .reader(() -> null)
            .processor(item -> null)
            .writer((ItemStreamWriter<String>)chunk -> {
            })
            .build();
    }

    @Bean
    public Step v2Step3(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return new StepBuilder("v2Step3", jobRepository)
            .partitioner(v2Step1(jobRepository ,transactionManager))
            .gridSize(2)
            .build();
    }

    @Bean
    public Step v2Step4(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return new StepBuilder("v2Step4", jobRepository)
            .job(job(jobRepository, transactionManager))
            .build();
    }

    //JobStep과 FlowStep은 Step안에서 job과 flow를 실행시킨다는 특수한 구성을 가짐
    @Bean
    public Job job(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("v2BatchJob1-2", jobRepository)
            .start(v2Step1(jobRepository, transactionManager))
            .next(v2Step2(jobRepository, transactionManager))
            .next(v2Step3(jobRepository, transactionManager))
            .build();
    }

    @Bean
    public org.springframework.batch.core.job.flow.Flow v2Flow(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("v2Flow");
        return flowBuilder.start(v2Step2(jobRepository, transactionManager)).build();
    }
}
