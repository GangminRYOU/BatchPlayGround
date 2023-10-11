package com.example.batchwithoutweb.chunktask.domain;

import lombok.Data;

@Data
public class JdbcCustomerV3 {
    private Long id;
    private String firstName;
    private String lastName;
    private String birthDate;
}
