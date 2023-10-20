package com.example.batchwithoutweb.chunktask.config.flatfile.dto;

import static com.example.batchwithoutweb.chunktask.config.flatfile.dto.InfoType.*;

import lombok.Getter;

@Getter
public enum DemantiaCenterColumns {
    CENTER_NAME(0, CenterType),
    CENTER_CATEGORY(1, CenterType),
    ROAD_NAME_ADDRESS(2, AddressType),
    LOT_NUMBER_ADDRESS(3, AddressType),
    LONGITUDE(4, AddressType),
    LATITUDE(5, AddressType),
    BUILT_DATE(6, CenterType),
    SQURE_AREA(7, CenterType),
    AMENITIES(8, CenterType),
    DOCTOR_NUM(9, WorkersType),
    NURSE_NUM(10, WorkersType),
    SOCIAL_WORKERS_NUM(11, WorkersType),
    OTHERS(12, WorkersType),
    OPERATION_AUTHORITY_NAME(13, ClinicType),
    OPERATION_REPRESENTATIVE(14, ClinicType),
    OPERATION_PHONE_NUMBER(15, ClinicType),
    CONSIGNMENT_DATE(16, ClinicType),
    MAIN_PROGRAM(17, CenterType),
    MANAGEMENT_PHONE_NUMBER(18, ManagementType),
    MANAGEMENT_NAME(19, ManagementType),
    DATA_ISSUE_DATE(20, DataIssueAuthorityType),
    DATA_ISSUE_AUTHORITY_CODE(21, DataIssueAuthorityType),
    DATA_ISSUE_AUTHORITY_NAME(22, DataIssueAuthorityType);

    private final Integer columnIndex;
    private final InfoType infoType;


    DemantiaCenterColumns(Integer columnIndex, InfoType infoType) {
        this.columnIndex = columnIndex;
        this.infoType = infoType;
    }
}
