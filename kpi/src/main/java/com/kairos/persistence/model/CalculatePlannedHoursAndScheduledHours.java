package com.kairos.persistence.model;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.StaffEmploymentDetails;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.service.counter.TimeBankService;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalculatePlannedHoursAndScheduledHours {
    public CalculatePlannedHoursAndScheduledHours(TimeBankService timeBankService, HashMap<Object, Object> objectObjectHashMap, Object o) {

    }
    public List<ShiftActivityDTO> getShiftActivityByBreak(List<ShiftActivityDTO> activities, List<ShiftActivityDTO> breakActivities) {
        return null;
    }
    public Number getAndUpdateCtaBonusMinutes(DateTimeInterval dateTimeInterval, CTARuleTemplateDTO ruleTemplate, ShiftActivityDTO shiftActivityDTO, StaffEmploymentDetails staffEmploymentDetails, Map<BigInteger, DayTypeDTO> dayTypeDTOMap) {
        return null;
    }
}
