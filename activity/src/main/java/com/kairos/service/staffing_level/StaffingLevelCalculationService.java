package com.kairos.service.staffing_level;

import com.kairos.commons.utils.ObjectUtils;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.staffing_level.StaffingLevel;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.staffing_level.StaffingLevelMongoRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.newArrayList;
import static org.mockito.ArgumentMatchers.isNull;

@Service
public class StaffingLevelCalculationService {
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private StaffingLevelMongoRepository staffingLevelMongoRepository;

    private boolean validateStaffingLevel(StaffingLevel staffingLevel, Shift shift,Shift oldShift){
        List<StaffingLevel> staffingLevels=staffingLevelMongoRepository.findByUnitIdAndDates(shift.getUnitId(),shift.getStartDate(),shift.getEndDate());
        boolean isShiftUpdated= oldShift != null && shift.isShiftUpdated(oldShift);
        List<Shift> shifts = shift.getId()==null ? shiftMongoRepository.findShiftBetweenDurationAndUnitIdAndDeletedFalseAndIdNotEqualTo(shift.getStartDate(),shift.getEndDate(), shift.getUnitId(), shift.getId()) : shiftMongoRepository.findShiftBetweenDurationAndUnitIdAndDeletedFalse(shift.getStartDate(),shift.getEndDate(), newArrayList(shift.getUnitId()));
        List[] shiftActivities=shift.getShiftActivitiesForValidatingStaffingLevel(ObjectUtils.isNull(shift.getId())?null:isShiftUpdated?oldShift:shift);


        return true;
    }
}
