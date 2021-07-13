package com.kairos.service.counter;


import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.user.skill.SkillLevelDTO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;

@Service
public class SkillKPIService implements KPIService{

    @Inject
    private KPIBuilderCalculationService kpiBuilderCalculationService;



    public double getCountOfSkillOfStaffIdOnSelectedDate(Long staffId, LocalDate selectedFromDate, LocalDate selectedToDate, KPICalculationRelatedInfo kpiCalculationRelatedInfo){
        StaffKpiFilterDTO staffKpiFilterDTO = kpiCalculationRelatedInfo.getStaffIdAndStaffKpiFilterMap().get(staffId);
        int count=0;
        if(selectedFromDate.equals(selectedToDate)) {
            count = getCountOfSkillByDay(staffKpiFilterDTO, selectedFromDate, selectedToDate, count, kpiCalculationRelatedInfo);
        }
        else if(!selectedFromDate.equals(selectedToDate)){
            count =getCountOfSkillByMonth(staffKpiFilterDTO,selectedFromDate,selectedToDate);
        }

        return count;
    }

    private int getCountOfSkillByDay(StaffKpiFilterDTO staffKpiFilterDTO, LocalDate selectedFromDate, LocalDate selectedToDate, int count, KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        if(ObjectUtils.isNotNull(kpiCalculationRelatedInfo.getApplicableKPI().getDateForKPISetCalculation())) {
            selectedFromDate = kpiCalculationRelatedInfo.getApplicableKPI().getDateForKPISetCalculation();
            selectedToDate = kpiCalculationRelatedInfo.getApplicableKPI().getDateForKPISetCalculation();
        }
        return getSkillCount(selectedFromDate, selectedToDate, count, staffKpiFilterDTO);
    }

    private int getSkillCount(LocalDate selectedFromDate, LocalDate selectedToDate, int count, StaffKpiFilterDTO staffKpiFilterDTO) {
        for(SkillLevelDTO skillLevelDTO :staffKpiFilterDTO.getSkills()){
            DateTimeInterval dateTimeInterval = new DateTimeInterval(skillLevelDTO.getStartDate(),skillLevelDTO.getEndDate());
            if(selectedFromDate.equals(selectedToDate)) {
                if ((selectedFromDate.isAfter(skillLevelDTO.getStartDate()) || selectedFromDate.equals(skillLevelDTO.getStartDate())) && ObjectUtils.isNull(skillLevelDTO.getEndDate())) {
                    count++;
                }
                count = getCountByInterval(selectedFromDate, selectedToDate, count, skillLevelDTO, dateTimeInterval);
            }

        }
        return count;
    }

    private int getCountByInterval(LocalDate selectedFromDate, LocalDate selectedToDate, int count, SkillLevelDTO skillLevelDTO, DateTimeInterval dateTimeInterval) {
        if (ObjectUtils.isNotNull(skillLevelDTO.getEndDate())) {
            DateTimeInterval dateTimeIntervals = new DateTimeInterval(selectedFromDate,selectedToDate);
            if (dateTimeInterval.containsAndEqualsEndDate(DateUtils.asDate(selectedFromDate))||selectedFromDate.equals(skillLevelDTO.getStartDate())||dateTimeInterval.containsInterval(dateTimeIntervals)) {
                count++;
            }
        }
        return count;
    }


    public int getCountOfSkillByMonth(StaffKpiFilterDTO staffKpiFilterDTO,LocalDate selectedFromDate, LocalDate selectedToDate){
        DateTimeInterval dateTimeIntervalForMonth = new DateTimeInterval(selectedFromDate,selectedToDate);
        int count=0;
        for (SkillLevelDTO skillLevelDTO : staffKpiFilterDTO.getSkills()) {
            count = getCount(selectedFromDate, dateTimeIntervalForMonth, count, skillLevelDTO);
        }
        return count;
    }

    private int getCount(LocalDate selectedFromDate, DateTimeInterval dateTimeIntervalForMonth, int count, SkillLevelDTO skillLevelDTO) {
        DateTimeInterval dateTimeInterval;
        if(ObjectUtils.isNotNull(skillLevelDTO.getEndDate())) {
            dateTimeInterval = new DateTimeInterval(skillLevelDTO.getStartDate(), skillLevelDTO.getEndDate());
            if (dateTimeInterval.containsInterval(dateTimeIntervalForMonth) || dateTimeIntervalForMonth.containsAndEqualsEndDate(DateUtils.asDate(dateTimeInterval.getStartLocalDate())) || dateTimeIntervalForMonth.containsAndEqualsEndDate(DateUtils.asDate(dateTimeInterval.getEndLocalDate()))) {
                count++;
            }
        }
        else if (ObjectUtils.isNull(skillLevelDTO.getEndDate())) {
            if ((dateTimeIntervalForMonth.contains(skillLevelDTO.getStartDate())||((selectedFromDate.isAfter(skillLevelDTO.getStartDate())||selectedFromDate.equals(skillLevelDTO.getStartDate())) && ObjectUtils.isNull(skillLevelDTO.getEndDate())))) {
                count++;
            }
        }
        return count;
    }

    @Override
    public <T> double get(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo, T t) {
        return getCountOfSkillOfStaffIdOnSelectedDate(staffId, asLocalDate(kpiCalculationRelatedInfo.getStartDate()), asLocalDate(kpiCalculationRelatedInfo.getEndDate()), kpiCalculationRelatedInfo);
    }
}
