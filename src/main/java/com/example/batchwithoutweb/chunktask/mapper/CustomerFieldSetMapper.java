package com.example.batchwithoutweb.chunktask.mapper;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import com.example.batchwithoutweb.chunktask.domain.Customer;

public class CustomerFieldSetMapper implements FieldSetMapper<Customer> {
    @Override
    public Customer mapFieldSet(FieldSet fieldSet) throws BindException {
        if(fieldSet == null){
            return null;
        }
        Customer customer = new Customer();
        customer.setName(fieldSet.readString("name"));
        customer.setAge(fieldSet.readInt("age"));
        customer.setYear(fieldSet.readString("year"));
        return customer;
    }
}
