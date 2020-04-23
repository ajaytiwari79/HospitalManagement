package com.kairos.shiftplanning.constraints.activityconstraint;

import com.kairos.enums.Day;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pradeep
 * @date - 18/12/18
 */
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class DayType {
    private Long id;
    private String name;
    private List<Day> validDays=new ArrayList<>();
    private List<CountryHolidayCalender> countryHolidayCalenders;
    private boolean holidayType;
    private boolean allowTimeSettings;

    public DayType(Long id, String name, List<Day> validDays, List<CountryHolidayCalender> countryHolidayCalenders, boolean holidayType, boolean allowTimeSettings) {
        this.id = id;
        this.name = name;
        this.validDays = validDays;
        this.countryHolidayCalenders = countryHolidayCalenders;
        this.holidayType = holidayType;
        this.allowTimeSettings = allowTimeSettings;
    }


}
