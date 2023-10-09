package com.example.batchwithoutweb.singletask.tasklet;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ExecutionContextTasklet1 implements Tasklet {
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("----------------------------------------------------------------");
        String stepName = chunkContext.getStepContext().getStepName();
        log.info("{} has been executed", stepName);
        StepExecution stepExecution = contribution.getStepExecution();
        JobExecution jobExecution = stepExecution.getJobExecution();
        ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();
        ExecutionContext stepExecutionContext = stepExecution.getExecutionContext();

        String jobName = chunkContext.getStepContext()
            .getStepExecution()
            .getJobExecution()
            .getJobInstance()
            .getJobName();
        if(jobExecutionContext.get("jobName") == null){
            jobExecutionContext.put("jobName", jobName);
        }
        if(stepExecutionContext.get("stepName") == null){
            stepExecutionContext.put("stepName", stepName);
        }

        log.info("jobName={}", jobExecutionContext.get("jobName"));
        log.info("stepName={}", stepExecutionContext.get("stepName"));

        return RepeatStatus.FINISHED;
    }
}
