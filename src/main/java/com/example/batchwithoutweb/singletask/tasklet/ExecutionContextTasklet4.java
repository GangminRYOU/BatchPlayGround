package com.example.batchwithoutweb.singletask.tasklet;

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
public class ExecutionContextTasklet4 implements Tasklet {
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("----------------------------------------------------------------");
        String stepName = chunkContext.getStepContext().getStepName();
        log.info("{} has been executed", stepName);
        StepExecution stepExecution = contribution.getStepExecution();
        ExecutionContext jobExecutionContext = stepExecution.getJobExecution().getExecutionContext();
        log.info("name={}", jobExecutionContext.get("name"));
        return RepeatStatus.FINISHED;
    }
}
