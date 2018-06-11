package com.kairos.activity.spec.night_worker;

import com.kairos.activity.spec.AbstractActivitySpecification;
import com.kairos.response.dto.web.StaffDTO;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class NightWorkerAgeEligibilitySpecification extends AbstractActivitySpecification<StaffDTO> {

    private Integer eligibleMinAge;
    private Integer eligibleMaxAge;

    public NightWorkerAgeEligibilitySpecification(Integer eligibleMinAge, Integer eligibleMaxAge){
        this.eligibleMinAge = eligibleMinAge;
        this.eligibleMaxAge = eligibleMaxAge;
    }

    @Override
    public boolean isSatisfied(StaffDTO staffDTO) {
        if(Optional.ofNullable(staffDTO.getDateOfBirth()).isPresent()){
            int age = Period.between(staffDTO.getDateOfBirth(), LocalDate.now()).getYears();
            return (age >= eligibleMinAge &&  age <= eligibleMaxAge);
        } else {
            return false;
        }
    }

    @Override
    public List<String> isSatisfiedString(StaffDTO staffDTO) {
        return Collections.emptyList();
    }

}
