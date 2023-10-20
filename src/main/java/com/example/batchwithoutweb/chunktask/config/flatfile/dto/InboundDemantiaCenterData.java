package com.example.batchwithoutweb.chunktask.config.flatfile.dto;

import java.time.YearMonth;


import lombok.Builder;
import lombok.Getter;

@Getter
public class InboundDemantiaCenterData {
    private Clinic clinic;
    private Management management;
    private DataIssueAuthorityDto dataIssueAuthorityDto;
    private WorkersDto workersDto;
    private String centerName;
    private String centerCategory;
    private CenterAddress centerAddress;
    private YearMonth builtDate;
    private Double buildingArea;
    private String amenities;
    private String mainDemantiaProgram;

    @Builder
    public InboundDemantiaCenterData(Clinic clinic, Management management, DataIssueAuthorityDto dataIssueAuthorityDto,
        WorkersDto workersDto, String centerName, String centerCategory, CenterAddress centerAddress, YearMonth builtDate,
        Double buildingArea, String amenities, String mainDemantiaProgram) {
        this.clinic = clinic;
        this.management = management;
        this.dataIssueAuthorityDto = dataIssueAuthorityDto;
        this.workersDto = workersDto;
        this.centerName = centerName;
        this.centerCategory = centerCategory;
        this.centerAddress = centerAddress;
        this.builtDate = builtDate;
        this.buildingArea = buildingArea;
        this.amenities = amenities;
        this.mainDemantiaProgram = mainDemantiaProgram;
    }
}
