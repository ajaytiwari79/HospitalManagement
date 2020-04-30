package com.kairos.dto.activity.period;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

/**
 * Created by prerna on 30/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class PeriodSettingsDTO {
    private BigInteger id;
    private Long unitId;
    private Long parentOrgId;
    // upto when presence shift can be entered for request phase only
    private int presenceLimitInYear;
    // upto when absence shift can be entered for request phase only
    private int absenceLimitInYear;

    public PeriodSettingsDTO(BigInteger id, Long unitId, int presenceLimitInYear, int absenceLimitInYear) {
        this.id = id;
        this.unitId = unitId;
        this.presenceLimitInYear = presenceLimitInYear;
        this.absenceLimitInYear = absenceLimitInYear;

    }
}
