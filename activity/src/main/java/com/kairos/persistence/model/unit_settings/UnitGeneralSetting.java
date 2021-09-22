package com.kairos.persistence.model.unit_settings;

import com.kairos.enums.TimeBankLimitsType;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnitGeneralSetting extends MongoBaseEntity {
    private TimeBankLimitsType timeBankLimitsType;
    private Long unitId;
}
