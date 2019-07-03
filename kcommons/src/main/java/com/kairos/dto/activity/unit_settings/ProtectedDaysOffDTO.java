package com.kairos.dto.activity.unit_settings;

import com.kairos.enums.ProtectedDaysOffUnitSettings;
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
public class ProtectedDaysOffDTO {
    private BigInteger id;
    private Long unitId;
    private ProtectedDaysOffUnitSettings protectedDaysOffUnitSettings;

    public ProtectedDaysOffDTO(BigInteger id,Long unitId,ProtectedDaysOffUnitSettings protectedDaysOffUnitSettings){
        this.id=id;
        this.unitId=unitId;
        this.protectedDaysOffUnitSettings=protectedDaysOffUnitSettings;
    }

    public ProtectedDaysOffDTO(Long unitId,ProtectedDaysOffUnitSettings protectedDaysOffUnitSettings){
        this.unitId=unitId;
        this.protectedDaysOffUnitSettings=protectedDaysOffUnitSettings;
    }
}
