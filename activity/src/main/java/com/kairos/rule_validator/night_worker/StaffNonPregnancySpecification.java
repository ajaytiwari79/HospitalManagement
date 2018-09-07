package com.kairos.rule_validator.night_worker;


import com.kairos.enums.Gender;
import com.kairos.rule_validator.activity.AbstractActivitySpecification;
import com.kairos.user.staff.StaffDTO;

import java.util.List;

public class StaffNonPregnancySpecification extends AbstractActivitySpecification<StaffDTO> {


    @Override
    public boolean isSatisfied(StaffDTO staffDTO) {
        return (staffDTO.getGender().equals(Gender.MALE) || (staffDTO.getGender().equals(Gender.FEMALE) && !staffDTO.isPregnant()) );
    }

    @Override
    public void validateRules(StaffDTO staffDTO) {

    }

    @Override
    public List<String> isSatisfiedString(StaffDTO staffDTO) {
        return null;
    }
}
