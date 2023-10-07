package com.example.batchwithoutweb.singletask.validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;

public class CustomJobParamtersValidator
    implements org.springframework.batch.core.JobParametersValidator {
    //JobParamters가 매개변수로 넘어온다.
    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        if(parameters.getString("name") == null){
            throw new JobParametersInvalidException("name paramters is not found");
        }
    }
}
