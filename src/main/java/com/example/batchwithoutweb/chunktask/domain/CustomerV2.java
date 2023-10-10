package com.example.batchwithoutweb.chunktask.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerV2 {
    Long id;
    String name;
    Integer age;
}
