package com.example.batchwithoutweb.chunktask.config.flatfile.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CenterAddress {
    private String roadNameAddress;
    private String lotNumberAddress;
    private Double longitude;
    private Double latitude;

    @Builder
    public CenterAddress(String roadNameAddress, String lotNumberAddress, Double longitude, Double latitude) {
        this.roadNameAddress = roadNameAddress;
        this.lotNumberAddress = lotNumberAddress;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
