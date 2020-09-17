package com.kairos.service.staffing_level;

import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.staffing_level.StaffingLevel;
import com.kairos.persistence.repository.staffing_level.StaffingLevelMongoRepository;

import javax.inject.Inject;
import java.util.List;

public class StaffingLevelCalculationService {
    @Inject
    private StaffingLevelMongoRepository staffingLevelMongoRepository;

    public boolean valid(Shift shift){

        List<StaffingLevel> staffingLevels=staffingLevelMongoRepository.findByUnitIdAndDates(shift.getUnitId(),shift.getActivities().get(0).getStartDate(),shift.getActivities().get(shift.getActivities().size()-1).getEndDate());
        for(StaffingLevel staffingLevel:staffingLevels){

        }
        return  true;
    }
}
