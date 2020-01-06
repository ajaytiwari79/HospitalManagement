package com.kairos.rule_validator.night_worker;


import com.kairos.enums.Gender;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetail;
import com.kairos.rule_validator.activity.AbstractActivitySpecification;

import java.util.List;

public class StaffNonPregnancySpecification extends AbstractActivitySpecification<StaffPersonalDetail> {


    @Override
    public boolean isSatisfied(StaffPersonalDetail staffDTO) {
        return (staffDTO.getGender().equals(Gender.MALE) || (staffDTO.getGender().equals(Gender.FEMALE) && !staffDTO.isPregnant()) );
    }

    @Override
    public void validateRules(StaffPersonalDetail staffDTO) {
        //This is override method
    }

    @Override
    public List<String> isSatisfiedString(StaffPersonalDetail staffDTO) {
        return null;
    }
}
