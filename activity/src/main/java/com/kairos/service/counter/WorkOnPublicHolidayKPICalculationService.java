package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.commons.utils.DateUtils.asLocalTime;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.enums.shift.ShiftType.PRESENCE;

@Service
public class WorkOnPublicHolidayKPICalculationService implements KPIService {
    @Override
    public <T> double get(Long staffId, DateTimeInterval dateTimeInterval, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo, T t) {
        return getWorkedOnPublicHolidayCount(staffId,dateTimeInterval,kpiCalculationRelatedInfo);
    }

    public long getWorkedOnPublicHolidayCount(Long staffId, DateTimeInterval dateTimeInterval, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        int workedOnPublicHolidayCount = 0;
        List<ShiftWithActivityDTO> shiftWithActivityDTOS = kpiCalculationRelatedInfo.getShiftsByStaffIdAndInterval(staffId, dateTimeInterval, false);
        shiftWithActivityDTOS = shiftWithActivityDTOS.stream().filter(shift -> PRESENCE.equals(shift.getShiftType())).collect(Collectors.toList());
        if (isCollectionNotEmpty(kpiCalculationRelatedInfo.getHolidayCalenders()) && isCollectionNotEmpty(shiftWithActivityDTOS)) {
            List<ShiftWithActivityDTO> shiftsInHoliday = shiftWithActivityDTOS.stream().filter(shift -> shiftInHoliday(shift, kpiCalculationRelatedInfo.getHolidayCalenders())).collect(Collectors.toList());
            workedOnPublicHolidayCount = shiftsInHoliday.size();
        }
        return workedOnPublicHolidayCount;
    }

    private boolean shiftInHoliday(ShiftWithActivityDTO shift, List<CountryHolidayCalenderDTO> holidayCalenders) {
        for (CountryHolidayCalenderDTO holidayCalender : holidayCalenders) {
            if (holidayCalender.getHolidayDate().isEqual(asLocalDate(shift.getStartDate())) &&
                    (isNull(holidayCalender.getStartTime()) ||
                            (!holidayCalender.getStartTime().isAfter(asLocalTime(shift.getStartDate())) && !holidayCalender.getEndTime().isBefore(asLocalTime(shift.getStartDate()))))
            ) {
                return true;
            }
        }
        return false;
    }
}
