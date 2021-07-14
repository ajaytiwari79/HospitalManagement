package com.kairos.dto.user.country.agreement.cta.cta_response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigInteger;

@Getter
@Setter
public class SectorWiseDayTypeInfo implements Serializable {

    private Long sectorId;
    private String sectorName;
    private BigInteger dayTypeId;
    private String holidayType;
    private Type type=Type.FLOATING;

    public enum Type {
        FIXED,FLOATING
    }

}
