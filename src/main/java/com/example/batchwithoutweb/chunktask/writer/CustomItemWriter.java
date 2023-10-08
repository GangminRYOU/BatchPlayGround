package com.example.batchwithoutweb.chunktask.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.example.batchwithoutweb.chunktask.domain.Customer;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomItemWriter implements ItemWriter<Customer> {
    @Override
    public void write(Chunk<? extends Customer> chunk) throws Exception {
        chunk.forEach(item -> log.info("item={}", item));
    }
}
