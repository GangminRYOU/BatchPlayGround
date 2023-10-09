package com.example.batchwithoutweb.chunktask.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomItemStreamWriter implements ItemStreamWriter {
    @Override
    public void write(Chunk chunk) throws Exception {
        chunk.forEach(item -> log.info("item={}", item));
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        log.info("Connection Opened");
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        log.info("a Chunk has been written");
    }

    @Override
    public void close() throws ItemStreamException {
        log.info("Connection closed");
    }
}
