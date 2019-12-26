package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.staffing_level.StaffingLevelDto;
import com.kairos.dto.activity.staffing_level.StaffingLevelInterval;
import com.kairos.enums.kpi.CalculationType;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.staffing_level.StaffingLevel;
import com.kairos.service.staffing_level.StaffingLevelService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

import static com.kairos.commons.utils.DateUtils.getHoursByMinutes;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.enums.kpi.CalculationType.PRESENCE_UNDER_STAFFING;


/**
 * Created By G.P.Ranjan on 24/12/19
 **/
@Service
public class StaffingLevelCalculationKPIService {
    @Inject
    private StaffingLevelService staffingLevelService;
    @Inject
    private KPIBuilderCalculationService kpiBuilderCalculationService;

    public double getStaffingLevelCalculationData(Long staffId, DateTimeInterval dateTimeInterval, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = kpiCalculationRelatedInfo.getShiftsByStaffIdAndInterval(staffId, dateTimeInterval);
        KPIBuilderCalculationService.ShiftActivityCriteria shiftActivityCriteria = kpiBuilderCalculationService.getShiftActivityCriteria(kpiCalculationRelatedInfo);
        KPIBuilderCalculationService.FilterShiftActivity filterShiftActivity = kpiBuilderCalculationService.new FilterShiftActivity(shiftWithActivityDTOS,shiftActivityCriteria,false).invoke();
        List<StaffingLevel> staffingLevels = staffingLevelService.findByUnitIdAndDates(kpiCalculationRelatedInfo.getUnitId(),dateTimeInterval.getStartDate(),dateTimeInterval.getEndDate());
        long underStaffingDataInMinutes = 0 ;
        long overStaffingDataInMinutes = 0 ;
        if(isCollectionNotEmpty(filterShiftActivity.getShifts())) {
            for (StaffingLevel staffingLevel : staffingLevels) {
                staffingLevelService.updatePresenceStaffingLevelAvailableStaffCount(staffingLevel, ObjectMapperUtils.copyPropertiesOfCollectionByMapper(filterShiftActivity.getShifts(), Shift.class));
                if (isCollectionNotEmpty(staffingLevel.getPresenceStaffingLevelInterval())) {
                    for (StaffingLevelInterval staffingLevelInterval : staffingLevel.getPresenceStaffingLevelInterval()) {
                        if (staffingLevelInterval.getMinNoOfStaff() > staffingLevelInterval.getAvailableNoOfStaff()) {
                            underStaffingDataInMinutes += (staffingLevelInterval.getMinNoOfStaff() - staffingLevelInterval.getAvailableNoOfStaff()) * staffingLevel.getStaffingLevelSetting().getDefaultDetailLevelMinutes();
                        } else if (staffingLevelInterval.getMaxNoOfStaff() < staffingLevelInterval.getAvailableNoOfStaff()) {
                            overStaffingDataInMinutes += (staffingLevelInterval.getAvailableNoOfStaff() - staffingLevelInterval.getMaxNoOfStaff()) * staffingLevel.getStaffingLevelSetting().getDefaultDetailLevelMinutes();
                        }
                    }
                }
            }
        }
        return getHoursByMinutes(PRESENCE_UNDER_STAFFING.equals(kpiCalculationRelatedInfo.getCalculationType()) ? underStaffingDataInMinutes : overStaffingDataInMinutes);
    }
}
