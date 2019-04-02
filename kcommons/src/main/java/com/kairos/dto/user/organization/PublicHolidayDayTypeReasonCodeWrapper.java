package com.kairos.dto.user.organization;

import com.kairos.dto.user.country.day_type.DayType;
import com.kairos.dto.user.reason_code.ReasonCodeWrapper;

import java.util.List;
import java.util.Map;

public class PublicHolidayDayTypeReasonCodeWrapper {

    private List<DayType> dayTypes;
    private ReasonCodeWrapper reasonCodeWrapper;
    private List<Map<String,Object>> publicHolidays;

    public PublicHolidayDayTypeReasonCodeWrapper() {
    }

    public PublicHolidayDayTypeReasonCodeWrapper(List<DayType> dayTypes, ReasonCodeWrapper reasonCodeWrapper, List<Map<String, Object>> publicHolidays) {
        this.dayTypes = dayTypes;
        this.reasonCodeWrapper = reasonCodeWrapper;
        this.publicHolidays = publicHolidays;
    }

    public List<DayType> getDayTypes() { return dayTypes; }

    public void setDayTypes(List<DayType> dayTypes) { this.dayTypes = dayTypes; }

    public ReasonCodeWrapper getReasonCodeWrapper() { return reasonCodeWrapper; }

    public void setReasonCodeWrapper(ReasonCodeWrapper reasonCodeWrapper) { this.reasonCodeWrapper = reasonCodeWrapper; }

    public List<Map<String, Object>> getPublicHolidays() { return publicHolidays; }

    public void setPublicHolidays(List<Map<String, Object>> publicHolidays) { this.publicHolidays = publicHolidays; }
}
