package com.kairos.persistence.model.unit_settings;

import com.kairos.enums.ProtectedDaysOffUnitSettings;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.javers.core.metamodel.annotation.Entity;

import java.math.BigInteger;

/**
 * Created By G.P.Ranjan on 1/7/19
 **/
@Entity
@Getter
@Setter
@NoArgsConstructor
public class ProtectedDaysOffSetting extends MongoBaseEntity {
    private Long unitId;
    private ProtectedDaysOffUnitSettings protectedDaysOffUnitSettings;

    public ProtectedDaysOffSetting(BigInteger id, Long unitId, ProtectedDaysOffUnitSettings protectedDaysOffUnitSettings){
        super.id=id;
        this.unitId=unitId;
        this.protectedDaysOffUnitSettings=protectedDaysOffUnitSettings;
    }

}
