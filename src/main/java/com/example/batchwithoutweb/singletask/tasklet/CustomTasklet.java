package com.example.batchwithoutweb.singletask.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomTasklet implements Tasklet {
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        String stepName = contribution.getStepExecution().getStepName();
        String jobName = chunkContext.getStepContext().getJobName();
        log.info("stepName: {}", stepName);
        log.info("jobName: {}", jobName);
        return RepeatStatus.FINISHED;
    }
}
