package com.kairos.activity.spec.night_worker;

import com.kairos.activity.persistence.model.night_worker.NightWorkerUnitSettings;
import com.kairos.activity.spec.AbstractActivitySpecification;
import com.kairos.response.dto.web.StaffDTO;

import java.time.LocalDate;
import java.time.Period;

public class NightWorkerAgeEligibilitySpecification extends AbstractActivitySpecification<StaffDTO> {

    private Integer eligibleMinAge;
    private Integer eligibleMaxAge;

    public NightWorkerAgeEligibilitySpecification(Integer eligibleMinAge, Integer eligibleMaxAge){
        this.eligibleMinAge = eligibleMinAge;
        this.eligibleMaxAge = eligibleMaxAge;
    }

    @Override
    public boolean isSatisfied(StaffDTO staffDTO) {
        int age = Period.between(staffDTO.getDateOfBirth(), LocalDate.now()).getYears();
        return (age >= eligibleMinAge &&  age <= eligibleMaxAge);
    }
}
