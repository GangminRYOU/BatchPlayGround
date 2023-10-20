package com.example.batchwithoutweb.chunktask.config.flatfile.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Management {
    private String authoritiesPhonNum;
    private String authorityName;

    @Builder
    public Management(String authoritiesPhonNum, String authorityName) {
        this.authoritiesPhonNum = authoritiesPhonNum;
        this.authorityName = authorityName;
    }
}
