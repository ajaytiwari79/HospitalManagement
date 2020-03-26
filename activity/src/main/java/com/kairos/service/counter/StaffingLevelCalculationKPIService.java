package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.staffing_level.StaffingLevelActivity;
import com.kairos.dto.activity.staffing_level.StaffingLevelInterval;
import com.kairos.dto.user.skill.SkillLevelDTO;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetail;
import com.kairos.persistence.model.staffing_level.StaffingLevel;
import com.kairos.service.staffing_level.StaffingLevelService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.enums.FilterType.TEAM_TYPE;
import static com.kairos.enums.kpi.CalculationType.*;
import static com.kairos.utils.counter.KPIUtils.getValueWithDecimalFormat;
import static java.util.stream.Collectors.toMap;


/**
 * Created By G.P.Ranjan on 24/12/19
 **/
@Service
public class StaffingLevelCalculationKPIService implements KPIService{
    @Inject
    private StaffingLevelService staffingLevelService;
    @Inject
    private KPIBuilderCalculationService kpiBuilderCalculationService;

    public double getStaffingLevelCalculationData(Long staffId, DateTimeInterval dateTimeInterval, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = kpiCalculationRelatedInfo.getShifts().stream().filter(shift -> dateTimeInterval.overlaps(new DateTimeInterval(shift.getStartDate(), shift.getEndDate()))).collect(Collectors.toList());
        KPIBuilderCalculationService.ShiftActivityCriteria shiftActivityCriteria = kpiBuilderCalculationService.getShiftActivityCriteria(kpiCalculationRelatedInfo);
        KPIBuilderCalculationService.FilterShiftActivity filterShiftActivity = kpiBuilderCalculationService.new FilterShiftActivity(shiftWithActivityDTOS,shiftActivityCriteria,false).invoke();
        Set<BigInteger> activityIds;
        if(isNotNull(staffId)) {
            StaffKpiFilterDTO staff = kpiCalculationRelatedInfo.getStaffIdAndStaffKpiFilterMap().get(staffId);
            if(isCollectionEmpty(staff.getTeams())){
                return 0;
            }else if(kpiCalculationRelatedInfo.getFilterBasedCriteria().containsKey(TEAM_TYPE) && kpiCalculationRelatedInfo.getFilterBasedCriteria().get(TEAM_TYPE).size() == 1){
                activityIds = staff.getTeams().stream().filter(teamDTO -> teamDTO.getTeamType().toString().equals(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(TEAM_TYPE).get(0))).flatMap(teamDTO -> teamDTO.getActivityIds().stream()).collect(Collectors.toSet());
            } else{
                activityIds = staff.getTeams().stream().flatMap(teamDTO -> teamDTO.getActivityIds().stream()).collect(Collectors.toSet());
            }
            if(isCollectionEmpty(activityIds)){
                return 0;
            }
        }else {
            activityIds = shiftActivityCriteria.getTeamActivityIds();
        }
        List<StaffingLevel> staffingLevels = staffingLevelService.findByUnitIdAndDates(kpiCalculationRelatedInfo.getUnitId(),dateTimeInterval.getStartDate(),asDate(asLocalDate(dateTimeInterval.getEndDate()).minusDays(1)));
        long staffingLevelData = 0;
        boolean isPresenceStaffingLevelData = PRESENCE_UNDER_STAFFING.equals(kpiCalculationRelatedInfo.getCalculationType()) || PRESENCE_OVER_STAFFING.equals(kpiCalculationRelatedInfo.getCalculationType());
        for (StaffingLevel staffingLevel : staffingLevels) {
            staffingLevelData += getStaffingLevelData(kpiCalculationRelatedInfo, activityIds, filterShiftActivity, isPresenceStaffingLevelData, staffingLevel);
        }
        return isPresenceStaffingLevelData ? getValueWithDecimalFormat(getHoursByMinutes(staffingLevelData)) : staffingLevelData;
    }

    private long getStaffingLevelData(KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo, Set<BigInteger> activityIds, KPIBuilderCalculationService.FilterShiftActivity filterShiftActivity, boolean isPresenceStaffingLevelData, StaffingLevel staffingLevel) {
        if (isCollectionNotEmpty(filterShiftActivity.getShifts())) {
            DateTimeInterval staffingLevelInterval = new DateTimeInterval(asLocalDate(staffingLevel.getCurrentDate()), asLocalDate(staffingLevel.getCurrentDate()).plusDays(1));
            List<ShiftWithActivityDTO> currentDateShifts = filterShiftActivity.getShifts().stream().filter(shift -> staffingLevelInterval.overlaps(new DateTimeInterval(shift.getStartDate(), shift.getEndDate()))).collect(Collectors.toList());
            if (isCollectionNotEmpty(currentDateShifts)) {
                Map<Long, List<SkillLevelDTO>> staffSkillsMap = kpiCalculationRelatedInfo.getSelectedDatesAndStaffDTOSMap().get(asLocalDate(staffingLevel.getCurrentDate()).toString()).stream().collect(toMap(StaffPersonalDetail::getId, StaffPersonalDetail::getSkills));
                staffingLevelService.updatePresenceStaffingLevelAvailableStaffCount(staffingLevel, ObjectMapperUtils.copyPropertiesOfCollectionByMapper(currentDateShifts, Shift.class), staffSkillsMap);
            }
        }
        long staffingLevelData;
        if (isPresenceStaffingLevelData) {
            staffingLevelData = getStaffingLevelCalculationData(staffingLevel.getPresenceStaffingLevelInterval(), PRESENCE_UNDER_STAFFING.equals(kpiCalculationRelatedInfo.getCalculationType()), activityIds, staffingLevel.getStaffingLevelSetting().getDefaultDetailLevelMinutes());
        }else{
            staffingLevelData = getStaffingLevelCalculationData(staffingLevel.getAbsenceStaffingLevelInterval(), ABSENCE_UNDER_STAFFING.equals(kpiCalculationRelatedInfo.getCalculationType()), activityIds, 1);
        }
        return staffingLevelData;
    }

    private long getStaffingLevelCalculationData(List<StaffingLevelInterval> staffingLevelIntervals, boolean getUnderStaffingData, Set<BigInteger> activityIds, int multiplyBy){
        long staffingData = 0 ;
        if (isCollectionNotEmpty(staffingLevelIntervals)) {
            for (StaffingLevelInterval staffingLevelInterval : staffingLevelIntervals) {
                staffingData += getUnderStaffingData ? getUnderStaffingLevelData(staffingLevelInterval, activityIds, multiplyBy) : getOverStaffingLevelData(staffingLevelInterval, activityIds, multiplyBy);
            }
        }
        return staffingData;
    }

    private long getUnderStaffingLevelData(StaffingLevelInterval staffingLevelInterval, Set<BigInteger> activityIds, int multiplyBy) {
        long underStaffingLevelData = 0;
        if(isCollectionNotEmpty(activityIds)){
            for (StaffingLevelActivity staffingLevelActivity : staffingLevelInterval.getStaffingLevelActivities()) {
                if(activityIds.contains(staffingLevelActivity.getActivityId()) && staffingLevelActivity.getMinNoOfStaff() > staffingLevelActivity.getAvailableNoOfStaff()){
                    underStaffingLevelData += (staffingLevelActivity.getMinNoOfStaff() - staffingLevelActivity.getAvailableNoOfStaff()) * multiplyBy;
                }
            }
        }else if (staffingLevelInterval.getMinNoOfStaff() > staffingLevelInterval.getAvailableNoOfStaff()) {
            underStaffingLevelData += (staffingLevelInterval.getMinNoOfStaff() - staffingLevelInterval.getAvailableNoOfStaff()) * multiplyBy;
        }
        return underStaffingLevelData;
    }

    private long getOverStaffingLevelData(StaffingLevelInterval staffingLevelInterval, Set<BigInteger> activityIds, int multiplyBy) {
        long overStaffingLevelData = 0;
        if(isCollectionNotEmpty(activityIds)){
            for (StaffingLevelActivity staffingLevelActivity : staffingLevelInterval.getStaffingLevelActivities()) {
                if(activityIds.contains(staffingLevelActivity.getActivityId()) && staffingLevelActivity.getMaxNoOfStaff() < staffingLevelActivity.getAvailableNoOfStaff()){
                    overStaffingLevelData += (staffingLevelActivity.getAvailableNoOfStaff() - staffingLevelActivity.getMaxNoOfStaff()) * multiplyBy;
                }
            }
        }else if (staffingLevelInterval.getMaxNoOfStaff() < staffingLevelInterval.getAvailableNoOfStaff()) {
            overStaffingLevelData += (staffingLevelInterval.getAvailableNoOfStaff() - staffingLevelInterval.getMaxNoOfStaff()) * multiplyBy;
        }
        return overStaffingLevelData;
    }

    public Map<Integer, Long> getPresenceStaffingLevelDataPerHour(DateTimeInterval dateTimeInterval, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = kpiCalculationRelatedInfo.getShifts().stream().filter(shift -> dateTimeInterval.overlaps(new DateTimeInterval(shift.getStartDate(), shift.getEndDate()))).collect(Collectors.toList());
        KPIBuilderCalculationService.ShiftActivityCriteria shiftActivityCriteria = kpiBuilderCalculationService.getShiftActivityCriteria(kpiCalculationRelatedInfo);
        KPIBuilderCalculationService.FilterShiftActivity filterShiftActivity = kpiBuilderCalculationService.new FilterShiftActivity(shiftWithActivityDTOS,shiftActivityCriteria,false).invoke();
        Set<BigInteger> activityIds = shiftActivityCriteria.getTeamActivityIds();
        List<StaffingLevel> staffingLevels = staffingLevelService.findByUnitIdAndDates(kpiCalculationRelatedInfo.getUnitId(),dateTimeInterval.getStartDate(),asDate(asLocalDate(dateTimeInterval.getEndDate()).minusDays(1)));
        Map<Integer,Long> staffingLevelDataMap = new HashMap<>();
        for (StaffingLevel staffingLevel : staffingLevels) {
            setStaffingLevelDataPerHour(kpiCalculationRelatedInfo, activityIds, filterShiftActivity, staffingLevel, staffingLevelDataMap);
        }
        return staffingLevelDataMap;
    }

    private void setStaffingLevelDataPerHour(KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo, Set<BigInteger> activityIds, KPIBuilderCalculationService.FilterShiftActivity filterShiftActivity, StaffingLevel staffingLevel, Map<Integer,Long> staffingLevelDataMap) {
        if (isCollectionNotEmpty(filterShiftActivity.getShifts())) {
            DateTimeInterval staffingLevelInterval = new DateTimeInterval(asLocalDate(staffingLevel.getCurrentDate()), asLocalDate(staffingLevel.getCurrentDate()).plusDays(1));
            List<ShiftWithActivityDTO> currentDateShifts = filterShiftActivity.getShifts().stream().filter(shift -> staffingLevelInterval.overlaps(new DateTimeInterval(shift.getStartDate(), shift.getEndDate()))).collect(Collectors.toList());
            if (isCollectionNotEmpty(currentDateShifts)) {
                Map<Long, List<SkillLevelDTO>> staffSkillsMap = isMapNotEmpty(kpiCalculationRelatedInfo.getSelectedDatesAndStaffDTOSMap()) ? kpiCalculationRelatedInfo.getSelectedDatesAndStaffDTOSMap().get(asLocalDate(staffingLevel.getCurrentDate()).toString()).stream().collect(toMap(StaffPersonalDetail::getId, StaffPersonalDetail::getSkills)) : new HashMap<>();
                staffingLevelService.updatePresenceStaffingLevelAvailableStaffCount(staffingLevel, ObjectMapperUtils.copyPropertiesOfCollectionByMapper(currentDateShifts, Shift.class), staffSkillsMap);
            }
        }
        if (isCollectionNotEmpty(staffingLevel.getPresenceStaffingLevelInterval())) {
            updateCountForPresenceStaffingLevel(kpiCalculationRelatedInfo, activityIds, staffingLevel, staffingLevelDataMap);
        }
    }

    private void updateCountForPresenceStaffingLevel(KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo, Set<BigInteger> activityIds, StaffingLevel staffingLevel, Map<Integer, Long> staffingLevelDataMap) {
        for (StaffingLevelInterval staffingLevelInterval : staffingLevel.getPresenceStaffingLevelInterval()) {
            long staffingLevelData = PRESENCE_UNDER_STAFFING.equals(kpiCalculationRelatedInfo.getCalculationType()) ? getUnderStaffingLevelData(staffingLevelInterval, activityIds, staffingLevel.getStaffingLevelSetting().getDefaultDetailLevelMinutes()) : getOverStaffingLevelData(staffingLevelInterval, activityIds, staffingLevel.getStaffingLevelSetting().getDefaultDetailLevelMinutes());
            Integer hourNumber = staffingLevelInterval.getSequence() / 4;
            if(staffingLevelDataMap.containsKey(hourNumber)){
                staffingLevelDataMap.put(hourNumber,staffingLevelDataMap.get(hourNumber)+staffingLevelData);
            }else{
                staffingLevelDataMap.put(hourNumber,staffingLevelData);
            }
        }
    }

    public <E, T> Map<T, E> getPresenceStaffingLevelCalculationPerHour(KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        DateTimeInterval totalDataInterval = new DateTimeInterval(kpiCalculationRelatedInfo.getDateTimeIntervals().get(0).getStartDate(), kpiCalculationRelatedInfo.getDateTimeIntervals().get(kpiCalculationRelatedInfo.getDateTimeIntervals().size() - 1).getEndDate());
        Map<Integer,Long> staffingLevelDataMap = getPresenceStaffingLevelDataPerHour(totalDataInterval, kpiCalculationRelatedInfo);
        Map<T,E> staffingLevelMapPerHour = new HashMap<>();
        for (Map.Entry<Integer, Long> integerLongEntry : staffingLevelDataMap.entrySet()) {
            String dateTime = getLocalTimeByFormat(getLocalDateTime(kpiCalculationRelatedInfo.getApplicableKPI().getDateForKPISetCalculation(),integerLongEntry.getKey(),0,0));
            staffingLevelMapPerHour.put((T) dateTime, (E) getValueWithDecimalFormat(getHoursByMinutes(integerLongEntry.getValue())));
        }
        return staffingLevelMapPerHour;
    }

    @Override
    public <T> double get(Long staffId, DateTimeInterval dateTimeInterval, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo, T t) {
        return getStaffingLevelCalculationData(staffId, dateTimeInterval, kpiCalculationRelatedInfo);
    }
}
