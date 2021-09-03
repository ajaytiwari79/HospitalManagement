package com.kairos.persistence.model.country;

import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeneralSettings extends MongoBaseEntity {
    private Long unitId;
    private Long countryId;
    private boolean shiftCreationAllowForStaff;
    private boolean shiftCreationAllowForManagement;
    private boolean stopBrickSettingAllow;
    private List<BigInteger> selectedPhaseIds = new ArrayList<>();
    private int stopBrickOverStaffingDurationInPer;

}
