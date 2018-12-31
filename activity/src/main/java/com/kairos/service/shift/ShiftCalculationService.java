package com.kairos.service.shift;

import com.kairos.dto.activity.shift.StaffUnitPositionDetails;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.staff.unit_position.StaffUnitPositionUnitDataWrapper;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.Day;
import org.apache.commons.collections.CollectionUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstants.EVERYDAY;

/**
 * CreatedBy vipulpandey on 7/12/18
 **/
public class ShiftCalculationService {
    public static void setDayTypeToCTARuleTemplate(StaffUnitPositionDetails staffAdditionalInfoDTO, List<DayTypeDTO> dayTypeDTOS, Map<Long, List<LocalDate>> publicHolidays) {
        Map<Long, List<Day>> daytypesMap = dayTypeDTOS.stream().collect(Collectors.toMap(k -> k.getId(), v -> v.getValidDays()));
        staffAdditionalInfoDTO.getCtaRuleTemplates().forEach(ctaRuleTemplateDTO -> {
            Set<DayOfWeek> dayOfWeeks = new HashSet<>();
            List<LocalDate> publicHolidaysDates= new ArrayList<>();
            for (Long dayTypeId : ctaRuleTemplateDTO.getDayTypeIds()) {
                List<Day> currentDay = daytypesMap.get(dayTypeId);
                currentDay.forEach(day -> {
                    if (!day.name().equals(EVERYDAY)) {
                        dayOfWeeks.add(DayOfWeek.valueOf(day.name()));
                    } else {
                        dayOfWeeks.addAll(Arrays.asList(DayOfWeek.values()));
                    }
                });
                //List<LocalDate> publicHoliday = publicHolidays.get(dayTypeId);
                /*if (CollectionUtils.isNotEmpty(publicHoliday)) {
                    publicHolidaysDates.addAll(publicHoliday);
                }*/
            }
            //ctaRuleTemplateDTO.setPublicHolidays(publicHolidaysDates);
            ctaRuleTemplateDTO.setDays(new ArrayList<>(dayOfWeeks));
        });
    }

}
