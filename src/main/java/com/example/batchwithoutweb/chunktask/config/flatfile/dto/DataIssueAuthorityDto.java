package com.example.batchwithoutweb.chunktask.config.flatfile.dto;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DataIssueAuthorityDto {
    private LocalDate dataIssuedAt;
    private String dataIssueAuthorityCode;
    private String dataIssueAuthorityName;

    @Builder
    public DataIssueAuthorityDto(LocalDate dataIssuedAt, String dataIssueAuthorityCode, String dataIssueAuthorityName) {
        this.dataIssuedAt = dataIssuedAt;
        this.dataIssueAuthorityCode = dataIssueAuthorityCode;
        this.dataIssueAuthorityName = dataIssueAuthorityName;
    }
}
