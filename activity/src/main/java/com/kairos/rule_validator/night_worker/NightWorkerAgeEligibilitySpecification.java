package com.kairos.rule_validator.night_worker;

import com.kairos.rule_validator.activity.AbstractActivitySpecification;
import com.kairos.user.staff.StaffDTO;

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
    public void validateRules(StaffDTO staffDTO) {

    }

    @Override
    public List<String> isSatisfiedString(StaffDTO staffDTO) {
        return Collections.emptyList();
    }

}
