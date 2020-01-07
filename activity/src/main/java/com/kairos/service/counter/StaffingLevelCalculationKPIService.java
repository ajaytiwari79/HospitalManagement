package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.staffing_level.StaffingLevelInterval;
import com.kairos.dto.user.staff.StaffDTO;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.staffing_level.StaffingLevel;
import com.kairos.service.staffing_level.StaffingLevelService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.commons.utils.DateUtils.getHoursByMinutes;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.enums.kpi.CalculationType.*;
import static com.kairos.utils.counter.KPIUtils.getValueWithDecimalFormat;


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
        double staffingLevelData = 0 ;
        boolean isPresenceStaffingLevelData = PRESENCE_UNDER_STAFFING.equals(kpiCalculationRelatedInfo.getCalculationType()) || PRESENCE_OVER_STAFFING.equals(kpiCalculationRelatedInfo.getCalculationType());
        for (StaffingLevel staffingLevel : staffingLevels) {
            if(isCollectionNotEmpty(filterShiftActivity.getShifts())) {
                List<ShiftWithActivityDTO> currentDateShifts = filterShiftActivity.getShifts().stream().filter(shift -> asLocalDate(shift.getStartDate()).equals(asLocalDate(staffingLevel.getCurrentDate()))).collect(Collectors.toList());
                if (isCollectionNotEmpty(currentDateShifts)) {
                    Map<Long, List<Map<String, Object>>> staffSkillsMap = kpiCalculationRelatedInfo.getSelectedDatesAndStaffDTOSMap().get(asLocalDate(staffingLevel.getCurrentDate()).toString()).stream().collect(Collectors.toMap(StaffDTO::getId, StaffDTO::getSkillInfo));
                    staffingLevelService.updatePresenceStaffingLevelAvailableStaffCount(staffingLevel, ObjectMapperUtils.copyPropertiesOfCollectionByMapper(currentDateShifts, Shift.class), staffSkillsMap);
                }
            }
            staffingLevelData += isPresenceStaffingLevelData ? getPresenceStaffingLevelCalculationData(staffingLevel, kpiCalculationRelatedInfo) : getAbsenceStaffingLevelCalculationData(staffingLevel, kpiCalculationRelatedInfo);
        }
        return getValueWithDecimalFormat(staffingLevelData);
    }

    private double getPresenceStaffingLevelCalculationData(StaffingLevel staffingLevel, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo){
        long underStaffingDataInMinutes = 0 ;
        long overStaffingDataInMinutes = 0 ;
        if (isCollectionNotEmpty(staffingLevel.getPresenceStaffingLevelInterval())) {
            for (StaffingLevelInterval staffingLevelInterval : staffingLevel.getPresenceStaffingLevelInterval()) {
                if (staffingLevelInterval.getMinNoOfStaff() > staffingLevelInterval.getAvailableNoOfStaff()) {
                    underStaffingDataInMinutes += (staffingLevelInterval.getMinNoOfStaff() - staffingLevelInterval.getAvailableNoOfStaff()) * staffingLevel.getStaffingLevelSetting().getDefaultDetailLevelMinutes();
                } else if (staffingLevelInterval.getMaxNoOfStaff() < staffingLevelInterval.getAvailableNoOfStaff()) {
                    overStaffingDataInMinutes += (staffingLevelInterval.getAvailableNoOfStaff() - staffingLevelInterval.getMaxNoOfStaff()) * staffingLevel.getStaffingLevelSetting().getDefaultDetailLevelMinutes();
                }
            }
        }
        return getHoursByMinutes(PRESENCE_UNDER_STAFFING.equals(kpiCalculationRelatedInfo.getCalculationType()) ? underStaffingDataInMinutes : overStaffingDataInMinutes);
    }

    private double getAbsenceStaffingLevelCalculationData(StaffingLevel staffingLevel, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo){
        long underStaffingDataInCount = 0 ;
        long overStaffingDataInCount = 0 ;
        if (isCollectionNotEmpty(staffingLevel.getAbsenceStaffingLevelInterval())) {
            for (StaffingLevelInterval staffingLevelInterval : staffingLevel.getAbsenceStaffingLevelInterval()) {
                if (staffingLevelInterval.getMinNoOfStaff() > staffingLevelInterval.getAvailableNoOfStaff()) {
                    underStaffingDataInCount += staffingLevelInterval.getMinNoOfStaff() - staffingLevelInterval.getAvailableNoOfStaff();
                } else if (staffingLevelInterval.getMaxNoOfStaff() < staffingLevelInterval.getAvailableNoOfStaff()) {
                    overStaffingDataInCount += staffingLevelInterval.getAvailableNoOfStaff() - staffingLevelInterval.getMaxNoOfStaff();
                }
            }
        }
        return ABSENCE_UNDER_STAFFING.equals(kpiCalculationRelatedInfo.getCalculationType()) ? underStaffingDataInCount : overStaffingDataInCount;
    }
}
