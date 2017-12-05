package com.kairos.response.dto.web.cta;

import java.util.ArrayList;
import java.util.List;

public class CTARuleTemplateDayTypeDTO {
    private Long dayType;
    private List<Long> countryHolidayCalenders =new ArrayList<>();
    public CTARuleTemplateDayTypeDTO() {
    }
    public Long getDayType() {
        return dayType;
    }
    public void setDayType(Long dayType) {
        this.dayType = dayType;
    }

    public List<Long> getCountryHolidayCalenders() {
        return countryHolidayCalenders;
    }

    public void setCountryHolidayCalenders(List<Long> countryHolidayCalenders) {
        this.countryHolidayCalenders = countryHolidayCalenders;
    }
}
