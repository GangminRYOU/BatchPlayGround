package com.example.batchwithoutweb.singletask.simplejob;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.job.DefaultJobParametersExtractor;
import org.springframework.batch.core.step.job.JobParametersExtractor;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class JobStepConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JobLauncher jobLauncher;

    @Bean
    public Job parentJob(){
        return new JobBuilder("v5ParentJob1", jobRepository)
            .start(jobStep())
            .next(v5Step2())
            .incrementer(new RunIdIncrementer())
            .build();
    }

    private Step jobStep() {
       return new StepBuilder("jobStep", jobRepository)
           .job(childJob())
           .launcher(jobLauncher)
           .parametersExtractor(jobParametersExtractor())
           .listener(new StepExecutionListener() {
               @Override
               public void beforeStep(StepExecution stepExecution) {
                    stepExecution.getExecutionContext().putString("name", "user1");
               }

               @Override
               public ExitStatus afterStep(StepExecution stepExecution) {
                   return StepExecutionListener.super.afterStep(stepExecution);
               }
           })
           .build();
    }

    //Extractor는 StepExecution안에있는 ExceutionContext라는 Map의 정보를 꺼내오는데, Key가 있어야 꺼내올수 있다.
    private DefaultJobParametersExtractor jobParametersExtractor() {
        DefaultJobParametersExtractor extractor = new DefaultJobParametersExtractor();
        extractor.setKeys(new String[]{"name"});
        return extractor;
    }

    @Bean
    public Job childJob() {
       return new JobBuilder("v5ChildJob1", jobRepository)
           .start(v5Step1())
           .build();
    }

    @Bean
    public Step v5Step1() {
        return new StepBuilder("v5Step1", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                //throw new RuntimeException("step1 was failed");
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }

    @Bean
    public Step v5Step2(){
        return new StepBuilder("v5Step2", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                throw new RuntimeException("step2 has failed");
                //RepeatStatus.FINISHED
            }, transactionManager)
            .build();
    }
}
