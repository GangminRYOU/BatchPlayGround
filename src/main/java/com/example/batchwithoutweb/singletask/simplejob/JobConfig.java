package com.example.batchwithoutweb.singletask.simplejob;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batchwithoutweb.singletask.incrementer.CustomJobParametersIncrementer;

@Configuration
public class JobConfig {
    @Bean
    @Qualifier
    public Job batchJob1(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return new JobBuilder("batchJob1", jobRepository)
            .incrementer(new CustomJobParametersIncrementer())
            .incrementer(new RunIdIncrementer())
            .start(step1(jobRepository, transactionManager))
            .next(step2(jobRepository, transactionManager))
            .next(step3(jobRepository, transactionManager))
            //.validator(new CustomJobParamtersValidator())
            .preventRestart()
            //.validator(new DefaultJobParametersValidator(new String[]{"name", "date"}, new String[]{"count"}))
            .build();
    }
    @Bean
    public Job batchJob2(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return new JobBuilder("batchJob2", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(flow(jobRepository, transactionManager))
            .next(step3(jobRepository, transactionManager))
            .end()
            .build();
    }
    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return new StepBuilder("step1", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                System.out.println("step 1 has been executed");
                return RepeatStatus.FINISHED;
            }, transactionManager).build();
    }
    @Bean
    public Step step2(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return new StepBuilder("step2", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                //하나의 Step이 Fail이면, Job자체가 Failed 되고, Fail 이후의 Step은 실행되지 않는다.
                chunkContext.getStepContext().getStepExecution().setStatus(BatchStatus.FAILED);
                contribution.setExitStatus(ExitStatus.STOPPED);
                System.out.println("step 2 has been executed");
                return RepeatStatus.FINISHED;
            }, transactionManager).build();
    }

    @Bean
    public Flow flow(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flow");
        return flowBuilder
            .start(step5(jobRepository, transactionManager))
            .next(step6(jobRepository, transactionManager))
            .build();
    }

    @Bean
    public Step step5(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return new StepBuilder("step5", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                System.out.println("step 5 has been executed");
                return RepeatStatus.FINISHED;
            }, transactionManager).build();
    }

    @Bean
    public Step step6(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return new StepBuilder("step6", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                System.out.println("step 6 has been executed");
                return RepeatStatus.FINISHED;
            }, transactionManager).build();
    }

    @Bean
    public Step step3(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return new StepBuilder("step3", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                System.out.println("step 3 has been executed");
                return RepeatStatus.FINISHED;
            }, transactionManager).build();
    }
}
