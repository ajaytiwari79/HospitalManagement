package com.kairos.persistence.model.period;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by prerna on 30/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class PeriodSettings extends MongoBaseEntity {

    private int presenceLimitInYear;
    private int absenceLimitInYear;
    private Long unitId;



    public PeriodSettings(int presenceLimitInYear, int absenceLimitInYear, Long unitId){
        this.presenceLimitInYear = presenceLimitInYear;
        this.absenceLimitInYear = absenceLimitInYear;
        this.unitId = unitId;
    }


}
