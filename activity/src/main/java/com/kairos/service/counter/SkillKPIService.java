package com.kairos.service.counter;


import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.user.skill.SkillLevelDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetail;
import com.kairos.rest_client.UserIntegrationService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.dto.activity.counter.enums.XAxisConfig.HOURS;

@Service
public class SkillKPIService {

    @Inject
    private KPIBuilderCalculationService kpiBuilderCalculationService;
    @Inject
    private UserIntegrationService userIntegrationService;



    public double getCountOfSkillOfStaffIdOnSelectedDate(Long staffId, LocalDate selectedFromDate, LocalDate selectedToDate, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo){
        Map<String, List<StaffPersonalDetail>> staffSkillMap = userIntegrationService.getSkillIdAndLevelByStaffIds(UserContext.getUserDetails().getCountryId(),kpiCalculationRelatedInfo.getStaffIds(),selectedFromDate,selectedToDate);
        int count=0;

        List<StaffPersonalDetail> staffPersonalDetails = staffSkillMap.get(selectedFromDate.toString());
        if(selectedFromDate.equals(selectedToDate)) {
            count = getCountOfSkillByDay(staffId, selectedFromDate, selectedToDate, count, staffPersonalDetails);
        }
        else if(!selectedFromDate.equals(selectedToDate)){
            count =getCountOfSkillByMonth(staffId,kpiCalculationRelatedInfo,selectedFromDate,selectedToDate);
        }

        return count;
    }

    private int getCountOfSkillByDay(Long staffId, LocalDate selectedFromDate, LocalDate selectedToDate, int count, List<StaffPersonalDetail> staffPersonalDetails) {
        for(StaffPersonalDetail staffPersonalDetail :staffPersonalDetails){
            if(staffPersonalDetail.getId().equals(staffId)){
                for(SkillLevelDTO skillLevelDTO :staffPersonalDetail.getSkills()){
                    DateTimeInterval dateTimeInterval = new DateTimeInterval(skillLevelDTO.getStartDate(),skillLevelDTO.getEndDate());
                    if(selectedFromDate.equals(selectedToDate)) {
                        if (ObjectUtils.isNull(skillLevelDTO.getEndDate())) {
                            if (selectedFromDate.isAfter(skillLevelDTO.getStartDate()) && ObjectUtils.isNull(skillLevelDTO.getEndDate())) {
                                count++;
                            }
                        }
                        if (ObjectUtils.isNotNull(skillLevelDTO.getEndDate())) {
                            if (dateTimeInterval.containsAndEqualsEndDate(asDate(selectedFromDate))) {
                                count++;
                            }

                        }
                    }

                }
            }
        }
        return count;
    }


    public int getCountOfSkillByMonth(Long staffId,KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo, LocalDate selectedFromDate, LocalDate selectedToDate){
        List<StaffPersonalDetail> staffPersonalDetails =userIntegrationService.getAllSkillIdAndLevelByStaffIds(UserContext.getUserDetails().getCountryId(),kpiCalculationRelatedInfo.getStaffIds());
        int count=0;
        DateTimeInterval dateTimeIntervalForMonth = new DateTimeInterval(selectedFromDate,selectedToDate);
        for(StaffPersonalDetail staffPersonalDetail :staffPersonalDetails) {
            if (staffPersonalDetail.getId().equals(staffId)) {
                    for (SkillLevelDTO skillLevelDTO : staffPersonalDetail.getSkills()) {
                        DateTimeInterval dateTimeInterval = new DateTimeInterval(skillLevelDTO.getStartDate(), skillLevelDTO.getEndDate());
                        if (dateTimeIntervalForMonth.containsInterval(dateTimeInterval) || dateTimeIntervalForMonth.containsAndEqualsEndDate(asDate(dateTimeInterval.getStartLocalDate())) || dateTimeIntervalForMonth.containsAndEqualsEndDate(asDate(dateTimeInterval.getEndLocalDate()))) {
                            count++;
                        }
                    }
                }
            }
      return count;
    }





}
