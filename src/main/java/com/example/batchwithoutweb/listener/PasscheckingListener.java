package com.example.batchwithoutweb.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class PasscheckingListener
    implements org.springframework.batch.core.StepExecutionListener {
    @Override
    public void beforeStep(StepExecution stepExecution) {
        StepExecutionListener.super.beforeStep(stepExecution);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        String exitCode = stepExecution.getExitStatus().getExitCode();
        if(!exitCode.equals(ExitStatus.FAILED.getExitCode())){
            return new ExitStatus("PASS");
        }
        return null;
    }
}
