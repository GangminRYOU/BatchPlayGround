package com.example.batchwithoutweb.config.decider;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

public class CustomDecider implements JobExecutionDecider {

    private AtomicInteger count = new AtomicInteger(1);

    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        int countVar = count.incrementAndGet();
        if(countVar % 2 == 0){
            return new FlowExecutionStatus("EVEN");
        }else {
            return new FlowExecutionStatus("ODD");
        }
    }
}
