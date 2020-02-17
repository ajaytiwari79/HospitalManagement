package com.kairos.service.counter;


import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.user.skill.SkillLevelDTO;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetail;
import com.kairos.rest_client.UserIntegrationService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.utils.counter.KPIUtils.getDateTimeIntervals;

@Service
public class SkillKPIService {

    @Inject
    private KPIBuilderCalculationService kpiBuilderCalculationService;
    @Inject
    private UserIntegrationService userIntegrationService;



    public double getCountOfSkillOfStaffIdOnSelectedDate(Long staffId, LocalDate selectedFromDate, LocalDate selectedToDate, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo){
        Map<String, List<StaffPersonalDetail>> staffSkillMap = kpiCalculationRelatedInfo.getSelectedDatesAndStaffDTOSMap();
        List<StaffPersonalDetail> staffPersonalDetails = staffSkillMap.get(selectedFromDate.toString());
        return getCountOfSkill(staffId,selectedFromDate,selectedToDate,staffPersonalDetails);
    }

    private int getCountOfSkill(Long staffId, LocalDate selectedFromDate, LocalDate selectedToDate,List<StaffPersonalDetail> staffPersonalDetails) {
        int count =0;
        DateTimeInterval dateTimeInterval =new DateTimeInterval(selectedFromDate,selectedToDate);
        for(StaffPersonalDetail staffPersonalDetail :staffPersonalDetails){
            if(staffPersonalDetail.getId().equals(staffId)){
                for(SkillLevelDTO skillLevelDTO :staffPersonalDetail.getSkills()){
                    boolean isExistInInterval =dateTimeInterval.contains(skillLevelDTO.getStartDate())||(isNotNull(skillLevelDTO.getEndDate())&&dateTimeInterval.contains(skillLevelDTO.getEndDate()));
                            if ((selectedFromDate.isAfter(skillLevelDTO.getStartDate())&&isNull(skillLevelDTO.getEndDate()))||isExistInInterval) {
                                count++;
                            }
                        }
                }
            }

        return count;
    }

}
