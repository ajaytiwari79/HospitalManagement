package com.kairos.activity.spec.night_worker;

import com.kairos.activity.persistence.model.night_worker.NightWorkerUnitSettings;
import com.kairos.activity.spec.AbstractActivitySpecification;

import java.time.LocalDate;
import java.time.Period;

public class NightWorkerAgeEligibilitySpecification extends AbstractActivitySpecification<NightWorkerUnitSettings> {

    private LocalDate dateOfBirth;

    public NightWorkerAgeEligibilitySpecification(LocalDate dateOfBirth){
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public boolean isSatisfied(NightWorkerUnitSettings nightWorkerUnitSettings) {
        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();
        return (age >= nightWorkerUnitSettings.getEligibleMinAge() &&  age <= nightWorkerUnitSettings.getEligibleMaxAge());
    }
}
