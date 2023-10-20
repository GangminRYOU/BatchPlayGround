package com.example.batchwithoutweb.chunktask.config.flatfile.dto;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Clinic {
    private String clinicName;
    private String representative;
    private String phoneNum;
    private LocalDate consignmentDate;

    @Builder
    public Clinic(String clinicName, String representative, String phoneNum, LocalDate consignmentDate) {
        this.clinicName = clinicName;
        this.representative = representative;
        this.phoneNum = phoneNum;
        this.consignmentDate = consignmentDate;
    }
}
