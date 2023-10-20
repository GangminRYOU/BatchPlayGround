package com.example.batchwithoutweb.chunktask.config.flatfile.fieldsetmapper;

import static com.example.batchwithoutweb.chunktask.config.flatfile.dto.DemantiaCenterColumns.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import com.example.batchwithoutweb.chunktask.config.flatfile.dto.CenterAddress;
import com.example.batchwithoutweb.chunktask.config.flatfile.dto.Clinic;
import com.example.batchwithoutweb.chunktask.config.flatfile.dto.DataIssueAuthorityDto;
import com.example.batchwithoutweb.chunktask.config.flatfile.dto.DemantiaCenterColumns;
import com.example.batchwithoutweb.chunktask.config.flatfile.dto.InboundDemantiaCenterData;
import com.example.batchwithoutweb.chunktask.config.flatfile.dto.Management;
import com.example.batchwithoutweb.chunktask.config.flatfile.dto.WorkersDto;

public class DemantiaClinicMapper implements FieldSetMapper<InboundDemantiaCenterData> {
    private final DateTimeFormatter YearMonthDayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter YearMonthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
    private final static int columns = 22;
    @Override
    public InboundDemantiaCenterData mapFieldSet(FieldSet fieldSet) throws BindException {
        if (fieldSet == null){
            return null;
        }
        return mapInboundDemantiaCenterData(fieldSet);
    }

    private InboundDemantiaCenterData mapInboundDemantiaCenterData(FieldSet fieldSet) {
        CenterAddress centerAddress = mapAddress(fieldSet);
        Clinic clinic = mapClinic(fieldSet);
        Management management = mapManagement(fieldSet);
        WorkersDto workers = createWorkers(fieldSet);
        DataIssueAuthorityDto dataIssueAuthorityDto = mapDataIssueAuthority(fieldSet);
        String builtDate = fieldSet.readString(BUILT_DATE.getColumnIndex());
        YearMonth centerBuiltDate = YearMonth.parse(builtDate, YearMonthFormatter);
        Double squareArea = null;
        String tmp = fieldSet.readString(SQURE_AREA.getColumnIndex());
        if(!tmp.isBlank()){
            squareArea = Double.parseDouble(tmp);
        }
        return InboundDemantiaCenterData.builder()
            .centerAddress(centerAddress)
            .clinic(clinic)
            .management(management)
            .workersDto(workers)
            .dataIssueAuthorityDto(dataIssueAuthorityDto)
            .amenities(fieldSet.readString(AMENITIES.getColumnIndex()))
            .buildingArea(squareArea)
            .centerCategory(fieldSet.readString(CENTER_CATEGORY.getColumnIndex()))
            .centerName(fieldSet.readString(CENTER_NAME.getColumnIndex()))
            .mainDemantiaProgram(fieldSet.readString(MAIN_PROGRAM.getColumnIndex()))
            .builtDate(centerBuiltDate)
            .build();
    }

    private DataIssueAuthorityDto mapDataIssueAuthority(FieldSet fieldSet) {
        String issueDate = fieldSet.readString(DATA_ISSUE_DATE.getColumnIndex());
        LocalDate dataIssuedAt = LocalDate.parse(issueDate, YearMonthDayFormatter);
        return DataIssueAuthorityDto.builder()
            .dataIssuedAt(dataIssuedAt)
            .dataIssueAuthorityCode(fieldSet.readString(DATA_ISSUE_AUTHORITY_CODE.getColumnIndex()))
            .dataIssueAuthorityName(fieldSet.readString(DATA_ISSUE_AUTHORITY_NAME.getColumnIndex()))
            .build();
    }

    private static WorkersDto createWorkers(FieldSet fieldSet) {
        return WorkersDto.builder()
            .socialWorkersNum(fieldSet.readInt(SOCIAL_WORKERS_NUM.getColumnIndex()))
            .doctorNum(fieldSet.readInt(DOCTOR_NUM.getColumnIndex()))
            .nurseNum(fieldSet.readInt(NURSE_NUM.getColumnIndex()))
            .others(fieldSet.readString(OTHERS.getColumnIndex()))
            .build();
    }

    private static Management mapManagement(FieldSet fieldSet) {
        return Management.builder()
            .authoritiesPhonNum(fieldSet.readString(MANAGEMENT_PHONE_NUMBER.getColumnIndex()))
            .authorityName(fieldSet.readString(MANAGEMENT_NAME.getColumnIndex()))
            .build();
    }

    private Clinic mapClinic(FieldSet fieldSet) {
        String consignmentDate = fieldSet.readString(CONSIGNMENT_DATE.getColumnIndex());
        Clinic.ClinicBuilder builder = Clinic.builder()
            .clinicName(fieldSet.readString(OPERATION_AUTHORITY_NAME.getColumnIndex()))
            .representative(fieldSet.readString(OPERATION_REPRESENTATIVE.getColumnIndex()))
            .phoneNum(fieldSet.readString(OPERATION_PHONE_NUMBER.getColumnIndex()));
        if(consignmentDate.isBlank()){
            return builder.build();
        }
        return builder
            .consignmentDate(LocalDate.parse(consignmentDate, YearMonthDayFormatter))
            .build();
    }

    private CenterAddress mapAddress(FieldSet fieldSet) {
        return CenterAddress.builder()
            .roadNameAddress(fieldSet.readString(ROAD_NAME_ADDRESS.getColumnIndex()))
            .lotNumberAddress(fieldSet.readString(LOT_NUMBER_ADDRESS.getColumnIndex()))
            .longitude(fieldSet.readDouble(LONGITUDE.getColumnIndex()))
            .latitude(fieldSet.readDouble(LATITUDE.getColumnIndex()))
            .build();
    }
}
