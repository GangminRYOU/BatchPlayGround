package com.example.batchwithoutweb.chunktask.processor;

import org.springframework.batch.item.ItemProcessor;

import com.example.batchwithoutweb.chunktask.config.processor.ProcessorInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomItemProcessorV5 implements ItemProcessor<ProcessorInfo, ProcessorInfo> {
    @Override
    public ProcessorInfo process(ProcessorInfo item) throws Exception {
        log.info("id={} -> V5 has been called", item.getId());
        return item;
    }
}
