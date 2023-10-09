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
public class ExecutionContextTasklet2 implements Tasklet {
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("----------------------------------------------------------------");
        String stepName = chunkContext.getStepContext().getStepName();
        log.info("{} has been executed", stepName);
        StepExecution stepExecution = contribution.getStepExecution();
        ExecutionContext stepExecutionContext = stepExecution.getExecutionContext();
        ExecutionContext jobExecutionContext = chunkContext.getStepContext()
            .getStepExecution()
            .getJobExecution()
            .getExecutionContext();
        //만약 step1이 정상적으로 종료되었다면, jobName은 공유 가능
        log.info("jobName={}", jobExecutionContext.get("jobName"));
        //stepExecution은 서로 공유가 되지 않는다.
        log.info("stepName={}", stepExecutionContext.get("stepName"));
        if(stepExecutionContext.get("stepName") == null){
            stepExecutionContext.put("stepName", stepName);
        }
        return RepeatStatus.FINISHED;
    }
}
