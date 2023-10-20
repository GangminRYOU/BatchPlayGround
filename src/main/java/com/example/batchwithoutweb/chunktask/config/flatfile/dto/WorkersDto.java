package com.example.batchwithoutweb.chunktask.config.flatfile.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class WorkersDto {
    private int doctorNum;
    private int nurseNum;
    private int socialWorkersNum;
    private String others;

    @Builder
    public WorkersDto(int doctorNum, int nurseNum, int socialWorkersNum, String others) {
        this.doctorNum = doctorNum;
        this.nurseNum = nurseNum;
        this.socialWorkersNum = socialWorkersNum;
        this.others = others;
    }
}
