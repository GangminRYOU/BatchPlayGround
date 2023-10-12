package com.example.batchwithoutweb.chunktask.config.service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class v7CustomService<T> {

    public void customWrite(T item){
      log.info("item={}", item);
    }
}
