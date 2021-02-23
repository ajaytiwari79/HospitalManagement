package com.kairos.dto.activity.shift;

import com.kairos.enums.shift.CoverShiftCriteria;
import com.kairos.enums.shift.GeneralCoverShiftCriteria;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class CoverShiftSettingDTO implements Serializable {
    private static final long serialVersionUID = -7528282585274834199L;
    private BigInteger id;
    private Long unitId;
    private Long countryId;
    private Set<Long> employmentTypeIds;
    private Set<Long> tagIds;
    private Set<BigInteger> sickTimeTypeIds;
    private Short maxLimitPendingRequest;//Staffs with more than x(input field) pending invitations are not eligible : default value is 5
    private Set<CoverShiftCriteria> coverShiftCriteria;
    private Map<GeneralCoverShiftCriteria, Short> generalCoverShiftCriteriaValue;

}
