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

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;

@Service
public class SkillKPIService {

    @Inject
    private KPIBuilderCalculationService kpiBuilderCalculationService;
    @Inject
    private UserIntegrationService userIntegrationService;



    public double getCountOfSkillOfStaffIdOnSelectedDate(Long staffId, LocalDate selectedFromDate, LocalDate selectedToDate, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo){
        List<StaffPersonalDetail> staffPersonalDetails = userIntegrationService.getAllSkillIdAndLevelByStaffIds(UserContext.getUserDetails().getCountryId(),kpiCalculationRelatedInfo.getStaffIds());
        int count=0;
        if(selectedFromDate.equals(selectedToDate)) {
            count = getCountOfSkillByDay(staffId, selectedFromDate, selectedToDate, count, staffPersonalDetails,kpiCalculationRelatedInfo);
        }
        else if(!selectedFromDate.equals(selectedToDate)){
            count =getCountOfSkillByMonth(staffId,staffPersonalDetails,selectedFromDate,selectedToDate);
        }

        return count;
    }

    private int getCountOfSkillByDay(Long staffId, LocalDate selectedFromDate, LocalDate selectedToDate, int count, List<StaffPersonalDetail> staffPersonalDetails, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        if(ObjectUtils.isNotNull(kpiCalculationRelatedInfo.getApplicableKPI().getDateForKPISetCalculation())) {
            selectedFromDate = kpiCalculationRelatedInfo.getApplicableKPI().getDateForKPISetCalculation();
            selectedToDate = kpiCalculationRelatedInfo.getApplicableKPI().getDateForKPISetCalculation();
        }

        for(StaffPersonalDetail staffPersonalDetail :staffPersonalDetails){
            if(staffPersonalDetail.getId().equals(staffId)){
                for(SkillLevelDTO skillLevelDTO :staffPersonalDetail.getSkills()){
                    DateTimeInterval dateTimeInterval = new DateTimeInterval(skillLevelDTO.getStartDate(),skillLevelDTO.getEndDate());
                    if(selectedFromDate.equals(selectedToDate)) {
                        if (ObjectUtils.isNull(skillLevelDTO.getEndDate())) {
                            if ((selectedFromDate.isAfter(skillLevelDTO.getStartDate())||selectedFromDate.equals(skillLevelDTO.getStartDate())) && ObjectUtils.isNull(skillLevelDTO.getEndDate())) {
                                count++;
                            }
                        }
                        if (ObjectUtils.isNotNull(skillLevelDTO.getEndDate())) {
                            DateTimeInterval dateTimeIntervals = new DateTimeInterval(selectedFromDate,selectedToDate);
                            if (dateTimeInterval.containsAndEqualsEndDate(asDate(selectedFromDate))||selectedFromDate.equals(skillLevelDTO.getStartDate())||dateTimeInterval.containsInterval(dateTimeIntervals)) {
                                count++;
                            }

                        }
                    }

                }
            }
        }
        return count;
    }


    public int getCountOfSkillByMonth(Long staffId, List<StaffPersonalDetail> staffPersonalDetails,LocalDate selectedFromDate, LocalDate selectedToDate){
        DateTimeInterval dateTimeIntervalForMonth = new DateTimeInterval(selectedFromDate,selectedToDate);
        DateTimeInterval dateTimeInterval = new DateTimeInterval();
        int count=0;

        for(StaffPersonalDetail staffPersonalDetail :staffPersonalDetails) {
            if (staffPersonalDetail.getId().equals(staffId)) {
                for (SkillLevelDTO skillLevelDTO : staffPersonalDetail.getSkills()) {
                    if(isNotNull(skillLevelDTO.getEndDate())) {
                        dateTimeInterval = new DateTimeInterval(skillLevelDTO.getStartDate(), skillLevelDTO.getEndDate());
                        if (dateTimeInterval.containsInterval(dateTimeIntervalForMonth) || dateTimeIntervalForMonth.containsAndEqualsEndDate(asDate(dateTimeInterval.getStartLocalDate())) || dateTimeIntervalForMonth.containsAndEqualsEndDate(asDate(dateTimeInterval.getEndLocalDate()))) {
                            count++;
                        }
                    }
                    else if (ObjectUtils.isNull(skillLevelDTO.getEndDate())) {
                        if ((dateTimeIntervalForMonth.contains(skillLevelDTO.getStartDate())||((selectedFromDate.isAfter(skillLevelDTO.getStartDate())||selectedFromDate.equals(skillLevelDTO.getStartDate())) && ObjectUtils.isNull(skillLevelDTO.getEndDate())))) {
                            count++;
                        }
                    }
                }
            }
        }
        return count;
    }

}
