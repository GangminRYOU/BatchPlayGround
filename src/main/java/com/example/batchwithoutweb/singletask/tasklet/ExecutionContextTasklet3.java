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
public class ExecutionContextTasklet3 implements Tasklet {
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("----------------------------------------------------------------");
        String stepName = chunkContext.getStepContext().getStepName();
        log.info("{} has been executed", stepName);
        //Job이 재시작했을 경우에, 실패한 그 데이터를 다시 가지고 올수 있는지 실패한 데이터를 다시금 활용할 수 있는지 테스트
        StepExecution stepExecution = contribution.getStepExecution();
        ExecutionContext jobExecutionContext = stepExecution.getJobExecution().getExecutionContext();
        if(jobExecutionContext.get("name") == null){
            jobExecutionContext.put("name", "Gangmin");
            throw new RuntimeException("step 3 was failed");
        }
        return RepeatStatus.FINISHED;
    }
}
