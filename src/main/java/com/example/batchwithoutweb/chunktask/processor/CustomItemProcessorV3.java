package com.example.batchwithoutweb.chunktask.processor;

import org.springframework.batch.item.ItemProcessor;

public class CustomItemProcessorV3 implements ItemProcessor<String, String> {
    int cnt = 0;
    @Override
    public String process(String item) throws Exception {
        cnt++;
        return (item + cnt).toUpperCase();
    }
}
