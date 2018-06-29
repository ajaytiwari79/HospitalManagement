package com.kairos.user.country.agreement.cta.cta_response;

import java.util.ArrayList;
import java.util.List;

public class CTARuleTemplateDayTypeDTO {
    private Long id;
    private Long dayType;
    private List<Long> countryHolidayCalenders =new ArrayList<>();
    public CTARuleTemplateDayTypeDTO() {
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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
