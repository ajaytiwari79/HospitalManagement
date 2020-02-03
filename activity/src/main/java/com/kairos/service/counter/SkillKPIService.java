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
        if(selectedFromDate.equals(selectedToDate)){
            List<StaffPersonalDetail> staffPersonalDetails = staffSkillMap.get(selectedFromDate.toString());
            for(StaffPersonalDetail staffPersonalDetail :staffPersonalDetails){
                if(staffPersonalDetail.getId().equals(staffId)){
                    for(SkillLevelDTO skillLevelDTO :staffPersonalDetail.getSkills()){
                        DateTimeInterval dateTimeInterval = new DateTimeInterval(skillLevelDTO.getStartDate(),skillLevelDTO.getEndDate());
                        if(ObjectUtils.isNull(skillLevelDTO.getEndDate())) {
                            if (selectedFromDate.isAfter(skillLevelDTO.getStartDate())&&ObjectUtils.isNull(skillLevelDTO.getEndDate())) {
                                count++;
                            }
                        }
                        if(ObjectUtils.isNotNull(skillLevelDTO.getEndDate())) {
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


}
