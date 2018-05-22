package com.kairos.activity.spec.night_worker;


import com.kairos.activity.spec.AbstractActivitySpecification;
import com.kairos.persistence.model.enums.Gender;
import com.kairos.response.dto.web.StaffDTO;

import java.time.LocalDate;
import java.time.Period;

public class StaffNonPregnancySpecification extends AbstractActivitySpecification<StaffDTO> {


    @Override
    public boolean isSatisfied(StaffDTO staffDTO) {
        return (staffDTO.getGender().equals(Gender.MALE) || (staffDTO.getGender().equals(Gender.FEMALE) && !staffDTO.isPregnant()) );

    }
}
