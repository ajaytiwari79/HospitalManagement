package com.kairos.dto.activity.wta;

import java.math.BigInteger;
public class CTAWTAResponseDTO {

    private BigInteger ctaId;
    private String ctaName;
    private BigInteger wtaId;
    private String wtaName;
    private Long unitPositionId;


    public CTAWTAResponseDTO() {


    }
    public CTAWTAResponseDTO(BigInteger ctaId, String ctaName,Long unitPositionId, BigInteger wtaId, String wtaName) {
        this.ctaId = ctaId;
        this.ctaName = ctaName;
        this.wtaId = wtaId;
        this.wtaName = wtaName;
        this.unitPositionId = unitPositionId;


    }
    public BigInteger getCtaId() {
        return ctaId;
    }

    public void setCtaId(BigInteger ctaId) {
        this.ctaId = ctaId;
    }

    public String getCtaName() {
        return ctaName;
    }

    public void setCtaName(String ctaName) {
        this.ctaName = ctaName;
    }

    public BigInteger getWtaId() {
        return wtaId;
    }

    public void setWtaId(BigInteger wtaId) {
        this.wtaId = wtaId;
    }

    public String getWtaName() {
        return wtaName;
    }

    public void setWtaName(String wtaName) {
        this.wtaName = wtaName;
    }
    public Long getUnitPositionId() {
        return unitPositionId;
    }

    public void setUnitPositionId(Long unitPositionId) {
        this.unitPositionId = unitPositionId;
    }

}
