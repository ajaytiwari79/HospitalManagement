package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.staffing_level.StaffingLevelDto;
import com.kairos.dto.activity.staffing_level.StaffingLevelInterval;
import com.kairos.dto.user.staff.StaffDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.kpi.CalculationType;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.staffing_level.StaffingLevel;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.staffing_level.StaffingLevelService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.commons.utils.DateUtils.getHoursByMinutes;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.enums.kpi.CalculationType.ABSENCE_UNDER_STAFFING;
import static com.kairos.enums.kpi.CalculationType.PRESENCE_UNDER_STAFFING;
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
    @Inject
    private UserIntegrationService userIntegrationService;

    public double getStaffingLevelCalculationData(Long staffId, DateTimeInterval dateTimeInterval, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo, boolean isPresenceStaffingLevelData) {
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = kpiCalculationRelatedInfo.getShiftsByStaffIdAndInterval(staffId, dateTimeInterval);
        KPIBuilderCalculationService.ShiftActivityCriteria shiftActivityCriteria = kpiBuilderCalculationService.getShiftActivityCriteria(kpiCalculationRelatedInfo);
        KPIBuilderCalculationService.FilterShiftActivity filterShiftActivity = kpiBuilderCalculationService.new FilterShiftActivity(shiftWithActivityDTOS,shiftActivityCriteria,false).invoke();
        List<StaffingLevel> staffingLevels = staffingLevelService.findByUnitIdAndDates(kpiCalculationRelatedInfo.getUnitId(),dateTimeInterval.getStartDate(),dateTimeInterval.getEndDate());
        List<Long> staffIds = filterShiftActivity.getShifts().stream().map(shift-> shift.getStaffId()).collect(Collectors.toList());
        List<LocalDate> dateList = staffingLevels.stream().map(staffingLevel -> asLocalDate(staffingLevel.getCurrentDate())).collect(Collectors.toList());
        Map<String, List<StaffDTO>> staffDTOSMap = userIntegrationService.getSkillIdAndLevelByStaffIds(UserContext.getUserDetails().getCountryId(), staffIds, dateList);
        double staffingLevelData = 0 ;
        if(isCollectionNotEmpty(filterShiftActivity.getShifts())) {
            for (StaffingLevel staffingLevel : staffingLevels) {
                Map<Long, List<Map<String, Object>>> staffSkillsMap = staffDTOSMap.get(asLocalDate(staffingLevel.getCurrentDate()).toString()).stream().collect(Collectors.toMap(k -> k.getId(), v -> v.getSkillInfo()));
                staffingLevelService.updatePresenceStaffingLevelAvailableStaffCount(staffingLevel, ObjectMapperUtils.copyPropertiesOfCollectionByMapper(shiftWithActivityDTOS, Shift.class), staffSkillsMap);
                staffingLevelData += isPresenceStaffingLevelData ? getPresenceStaffingLevelCalculationData(staffingLevel, kpiCalculationRelatedInfo) : getAbsenceStaffingLevelCalculationData(staffingLevel, kpiCalculationRelatedInfo);
            }
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
