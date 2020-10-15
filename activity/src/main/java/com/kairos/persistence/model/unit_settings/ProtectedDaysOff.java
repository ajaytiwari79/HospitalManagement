package com.kairos.persistence.model.unit_settings;

import com.kairos.enums.ProtectedDaysOffUnitSettings;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

/**
 * Created By G.P.Ranjan on 1/7/19
 **/
@Getter
@Setter
@NoArgsConstructor
public class ProtectedDaysOff extends MongoBaseEntity {
    private Long unitId;
    private ProtectedDaysOffUnitSettings protectedDaysOffUnitSettings;

    public ProtectedDaysOff(BigInteger id, Long unitId, ProtectedDaysOffUnitSettings protectedDaysOffUnitSettings){
        super.id=id;
        this.unitId=unitId;
        this.protectedDaysOffUnitSettings=protectedDaysOffUnitSettings;
    }

}
