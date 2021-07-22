package com.kairos.dto.user.country.agreement.cta.cta_response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalTime;

@Getter
@Setter
public class SectorWiseDayTypeInfo implements Serializable {
    private Long sectorId;
    private String sectorName;
    private BigInteger dayTypeId;
    private String holidayType;
    private LocalTime startTime;
    private LocalTime endTime;
}
