package com.example.batchwithoutweb.chunktask.mapper;

import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.LineTokenizer;

public class DefaultLineMapper<T> implements LineMapper<T> {

    private LineTokenizer tokenizer;
    private FieldSetMapper<T> fieldSetMapper;

    /**
     * 객체와 매핑된 결과를 return
     * @param line to be mapped
     * @param lineNumber of the current line
     * @return
     * @throws Exception
     */
    @Override
    public T mapLine(String line, int lineNumber) throws Exception {
        FieldSet tokens = tokenizer.tokenize(line);
        T t = fieldSetMapper.mapFieldSet(tokens);
        return t;
    }

    public void setTokenizer(LineTokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public void setFieldSetMapper(FieldSetMapper<T> fieldSetMapper) {
        this.fieldSetMapper = fieldSetMapper;
    }
}
