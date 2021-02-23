package com.kairos.persistence.model.shift;

import com.kairos.enums.shift.CoverShiftCriteria;
import com.kairos.enums.shift.GeneralCoverShiftCriteria;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
public class CoverShiftSetting extends MongoBaseEntity {
    private static final long serialVersionUID = 5203658405987724913L;
    private Long unitId;
    private Long countryId;
    private Set<Long> employmentTypeIds;
    private Set<Long> tagIds;
    private Set<BigInteger> sickTimeTypeIds;
    private Short maxLimitPendingRequest;//Staffs with more than x(input field) pending invitations are not eligible : default value is 5
    private Set<CoverShiftCriteria> coverShiftCriteria;
    private Map<GeneralCoverShiftCriteria, Short> generalCoverShiftCriteriaValue;

}
