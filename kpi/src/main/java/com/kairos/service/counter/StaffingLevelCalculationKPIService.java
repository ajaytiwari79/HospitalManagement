package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.staffing_level.StaffingLevelActivity;
import com.kairos.dto.activity.staffing_level.StaffingLevelInterval;
import com.kairos.dto.activity.staffing_level.presence.StaffingLevelDTO;
import com.kairos.dto.user.team.TeamDTO;
import com.kairos.enums.FilterType;
import com.kairos.enums.kpi.CalculationType;
import com.kairos.persistence.repository.counter.CounterHelperRepository;
import com.kairos.utils.counter.KPIUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created By G.P.Ranjan on 24/12/19
 **/
@Service
public class StaffingLevelCalculationKPIService implements KPIService{
    @Inject
    private CounterHelperRepository counterHelperRepository;
    @Inject
    private KPIBuilderCalculationService kpiBuilderCalculationService;

    public double getStaffingLevelCalculationData(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = kpiCalculationRelatedInfo.getShifts().stream().filter(shift -> dateTimeInterval.overlaps(new DateTimeInterval(shift.getStartDate(), shift.getEndDate()))).collect(Collectors.toList());
        KPIBuilderCalculationService.ShiftActivityCriteria shiftActivityCriteria = kpiBuilderCalculationService.getShiftActivityCriteria(kpiCalculationRelatedInfo);
        KPIBuilderCalculationService.FilterShiftActivity filterShiftActivity = kpiBuilderCalculationService.new FilterShiftActivity(shiftWithActivityDTOS,shiftActivityCriteria,false).invoke();
        Set<BigInteger> activityIds;
        if(ObjectUtils.isNotNull(staffId)) {
            StaffKpiFilterDTO staff = kpiCalculationRelatedInfo.getStaffIdAndStaffKpiFilterMap().get(staffId);
            if(ObjectUtils.isCollectionEmpty(staff.getTeams())){
                return 0;
            }else if(kpiCalculationRelatedInfo.getFilterBasedCriteria().containsKey(FilterType.TEAM_TYPE) && kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.TEAM_TYPE).size() == 1){
                activityIds = staff.getTeams().stream().filter(teamDTO -> teamDTO.getTeamType().toString().equals(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.TEAM_TYPE).get(0))).map(TeamDTO::getActivityId).collect(Collectors.toSet());
            } else{
                activityIds = staff.getTeams().stream().map(TeamDTO::getActivityId).collect(Collectors.toSet());
            }
            if(ObjectUtils.isCollectionEmpty(activityIds)){
                return 0;
            }
        }else {
            activityIds = shiftActivityCriteria.getTeamActivityIds();
        }
        List<StaffingLevelDTO> staffingLevels = counterHelperRepository.findByUnitIdAndDates(kpiCalculationRelatedInfo.getUnitId(),dateTimeInterval.getStartDate(), DateUtils.asDate(DateUtils.asLocalDate(dateTimeInterval.getEndDate()).minusDays(1)));
        long staffingLevelData = 0;
        boolean isPresenceStaffingLevelData = CalculationType.PRESENCE_UNDER_STAFFING.equals(kpiCalculationRelatedInfo.getCalculationType()) || CalculationType.PRESENCE_OVER_STAFFING.equals(kpiCalculationRelatedInfo.getCalculationType());
        for (StaffingLevelDTO staffingLevel : staffingLevels) {
            staffingLevelData += getStaffingLevelData(kpiCalculationRelatedInfo, activityIds, filterShiftActivity, isPresenceStaffingLevelData, staffingLevel);
        }
        return isPresenceStaffingLevelData ? KPIUtils.getValueWithDecimalFormat(DateUtils.getHoursByMinutes(staffingLevelData)) : staffingLevelData;
    }

    private long getStaffingLevelData(KPICalculationRelatedInfo kpiCalculationRelatedInfo, Set<BigInteger> activityIds, KPIBuilderCalculationService.FilterShiftActivity filterShiftActivity, boolean isPresenceStaffingLevelData, StaffingLevelDTO staffingLevel) {
        long staffingLevelData;
        if (isPresenceStaffingLevelData) {
            staffingLevelData = getStaffingLevelCalculationData(staffingLevel.getPresenceStaffingLevelInterval(), CalculationType.PRESENCE_UNDER_STAFFING.equals(kpiCalculationRelatedInfo.getCalculationType()), activityIds, staffingLevel.getStaffingLevelSetting().getDefaultDetailLevelMinutes());
        }else{
            staffingLevelData = getStaffingLevelCalculationData(staffingLevel.getAbsenceStaffingLevelInterval(), CalculationType.ABSENCE_UNDER_STAFFING.equals(kpiCalculationRelatedInfo.getCalculationType()), activityIds, 1);
        }
        return staffingLevelData;
    }

    private long getStaffingLevelCalculationData(List<StaffingLevelInterval> staffingLevelIntervals, boolean getUnderStaffingData, Set<BigInteger> activityIds, int multiplyBy){
        long staffingData = 0 ;
        if (ObjectUtils.isCollectionNotEmpty(staffingLevelIntervals)) {
            for (StaffingLevelInterval staffingLevelInterval : staffingLevelIntervals) {
                staffingData += getUnderStaffingData ? getUnderStaffingLevelData(staffingLevelInterval, activityIds, multiplyBy) : getOverStaffingLevelData(staffingLevelInterval, activityIds, multiplyBy);
            }
        }
        return staffingData;
    }

    private long getUnderStaffingLevelData(StaffingLevelInterval staffingLevelInterval, Set<BigInteger> activityIds, int multiplyBy) {
        long underStaffingLevelData = 0;
        if(ObjectUtils.isCollectionNotEmpty(activityIds)){
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
        if(ObjectUtils.isCollectionNotEmpty(activityIds)){
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

    public Map<Integer, Long> getPresenceStaffingLevelDataPerHour(DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = kpiCalculationRelatedInfo.getShifts().stream().filter(shift -> dateTimeInterval.overlaps(new DateTimeInterval(shift.getStartDate(), shift.getEndDate()))).collect(Collectors.toList());
        KPIBuilderCalculationService.ShiftActivityCriteria shiftActivityCriteria = kpiBuilderCalculationService.getShiftActivityCriteria(kpiCalculationRelatedInfo);
        KPIBuilderCalculationService.FilterShiftActivity filterShiftActivity = kpiBuilderCalculationService.new FilterShiftActivity(shiftWithActivityDTOS,shiftActivityCriteria,false).invoke();
        Set<BigInteger> activityIds = shiftActivityCriteria.getTeamActivityIds();
        List<StaffingLevelDTO> staffingLevels = counterHelperRepository.findByUnitIdAndDates(kpiCalculationRelatedInfo.getUnitId(),dateTimeInterval.getStartDate(), DateUtils.asDate(DateUtils.asLocalDate(dateTimeInterval.getEndDate()).minusDays(1)));
        Map<Integer,Long> staffingLevelDataMap = new HashMap<>();
        for (StaffingLevelDTO staffingLevel : staffingLevels) {
            setStaffingLevelDataPerHour(kpiCalculationRelatedInfo, activityIds, filterShiftActivity, staffingLevel, staffingLevelDataMap);
        }
        return staffingLevelDataMap;
    }

    private void setStaffingLevelDataPerHour(KPICalculationRelatedInfo kpiCalculationRelatedInfo, Set<BigInteger> activityIds, KPIBuilderCalculationService.FilterShiftActivity filterShiftActivity, StaffingLevelDTO staffingLevel, Map<Integer,Long> staffingLevelDataMap) {
        if (ObjectUtils.isCollectionNotEmpty(staffingLevel.getPresenceStaffingLevelInterval())) {
            updateCountForPresenceStaffingLevel(kpiCalculationRelatedInfo, activityIds, staffingLevel, staffingLevelDataMap);
        }
    }

    private void updateCountForPresenceStaffingLevel(KPICalculationRelatedInfo kpiCalculationRelatedInfo, Set<BigInteger> activityIds, StaffingLevelDTO staffingLevel, Map<Integer, Long> staffingLevelDataMap) {
        for (StaffingLevelInterval staffingLevelInterval : staffingLevel.getPresenceStaffingLevelInterval()) {
            long staffingLevelData = CalculationType.PRESENCE_UNDER_STAFFING.equals(kpiCalculationRelatedInfo.getCalculationType()) ? getUnderStaffingLevelData(staffingLevelInterval, activityIds, staffingLevel.getStaffingLevelSetting().getDefaultDetailLevelMinutes()) : getOverStaffingLevelData(staffingLevelInterval, activityIds, staffingLevel.getStaffingLevelSetting().getDefaultDetailLevelMinutes());
            Integer hourNumber = staffingLevelInterval.getSequence() / 4;
            if(staffingLevelDataMap.containsKey(hourNumber)){
                staffingLevelDataMap.put(hourNumber,staffingLevelDataMap.get(hourNumber)+staffingLevelData);
            }else{
                staffingLevelDataMap.put(hourNumber,staffingLevelData);
            }
        }
    }

    public <E, T> Map<T, E> getPresenceStaffingLevelCalculationPerHour(KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        DateTimeInterval totalDataInterval = new DateTimeInterval(kpiCalculationRelatedInfo.getDateTimeIntervals().get(0).getStartDate(), kpiCalculationRelatedInfo.getDateTimeIntervals().get(kpiCalculationRelatedInfo.getDateTimeIntervals().size() - 1).getEndDate());
        Map<Integer,Long> staffingLevelDataMap = getPresenceStaffingLevelDataPerHour(totalDataInterval, kpiCalculationRelatedInfo);
        Map<T,E> staffingLevelMapPerHour = new HashMap<>();
        for (Map.Entry<Integer, Long> integerLongEntry : staffingLevelDataMap.entrySet()) {
            String dateTime = DateUtils.getLocalTimeByFormat(DateUtils.getLocalDateTime(kpiCalculationRelatedInfo.getApplicableKPI().getDateForKPISetCalculation(),integerLongEntry.getKey(),0,0));
            staffingLevelMapPerHour.put((T) dateTime, (E) KPIUtils.getValueWithDecimalFormat(DateUtils.getHoursByMinutes(integerLongEntry.getValue())));
        }
        return staffingLevelMapPerHour;
    }

    @Override
    public <T> double get(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo, T t) {
        return getStaffingLevelCalculationData(staffId, dateTimeInterval, kpiCalculationRelatedInfo);
    }
}
