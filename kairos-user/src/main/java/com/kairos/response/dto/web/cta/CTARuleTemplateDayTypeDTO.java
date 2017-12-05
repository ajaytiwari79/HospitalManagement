package com.kairos.response.dto.web.cta;

import java.util.ArrayList;
import java.util.List;

public class CTARuleTemplateDayTypeDTO {
    private Long dayTypeId;
    private List<Long> countryHolidayCalenderIds =new ArrayList<>();
    public CTARuleTemplateDayTypeDTO() {
    }

    public Long getDayTypeId() {
        return dayTypeId;
    }

    public void setDayTypeId(Long dayTypeId) {
        this.dayTypeId = dayTypeId;
    }

    public List<Long> getCountryHolidayCalenderIds() {
        return countryHolidayCalenderIds;
    }

    public void setCountryHolidayCalenderIds(List<Long> countryHolidayCalenderIds) {
        this.countryHolidayCalenderIds = countryHolidayCalenderIds;
    }
}
