package com.example.batchwithoutweb.chunktask.processor;

import org.springframework.batch.item.ItemProcessor;

import com.example.batchwithoutweb.chunktask.domain.JdbcCustomerV3;
import com.example.batchwithoutweb.chunktask.domain.JpaCustomerV3;


public class CustomItemProcessorV2 implements ItemProcessor<JdbcCustomerV3, JpaCustomerV3> {

    @Override
    public JpaCustomerV3 process(JdbcCustomerV3 item) throws Exception {
        return item.toEntity();
    }
}
