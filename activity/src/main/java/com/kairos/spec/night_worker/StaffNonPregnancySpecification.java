package com.kairos.spec.night_worker;


import com.kairos.enums.Gender;
import com.kairos.spec.AbstractActivitySpecification;
import com.kairos.user.staff.StaffDTO;

import java.util.List;

public class StaffNonPregnancySpecification extends AbstractActivitySpecification<StaffDTO> {


    @Override
    public boolean isSatisfied(StaffDTO staffDTO) {
        return (staffDTO.getGender().equals(Gender.MALE) || (staffDTO.getGender().equals(Gender.FEMALE) && !staffDTO.isPregnant()) );
    }

    @Override
    public List<String> isSatisfiedString(StaffDTO staffDTO) {
        return null;
    }
}
