package com.kairos.rule_validator.night_worker;


import com.kairos.enums.Gender;
import com.kairos.rule_validator.activity.AbstractActivitySpecification;
import com.kairos.dto.user.staff.StaffDTO;

import java.util.List;

public class StaffNonPregnancySpecification extends AbstractActivitySpecification<StaffDTO> {


    @Override
    public boolean isSatisfied(StaffDTO staffDTO) {
        return (staffDTO.getGender().equals(Gender.MALE) || (staffDTO.getGender().equals(Gender.FEMALE) && !staffDTO.isPregnant()) );
    }

    @Override
    public void validateRules(StaffDTO staffDTO) {
        //This is override method
    }

    @Override
    public List<String> isSatisfiedString(StaffDTO staffDTO) {
        return null;
    }
}
