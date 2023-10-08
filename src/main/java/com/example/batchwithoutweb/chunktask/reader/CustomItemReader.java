package com.example.batchwithoutweb.chunktask.reader;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import com.example.batchwithoutweb.chunktask.domain.Customer;


public class CustomItemReader implements ItemReader<Customer> {
    private List<Customer> list;

    public CustomItemReader(List<Customer> list) {
        this.list = new ArrayList<>(list);
    }

    @Override
    public Customer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if(!list.isEmpty()){
            return list.remove(0);
        }
        //더이상 읽을 item이 없다.
        return null;
    }
}
