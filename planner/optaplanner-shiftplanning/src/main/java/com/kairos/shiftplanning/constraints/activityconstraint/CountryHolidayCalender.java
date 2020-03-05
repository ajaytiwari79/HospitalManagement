package com.kairos.shiftplanning.constraints.activityconstraint;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * @author pradeep
 * @date - 18/12/18
 */
@NoArgsConstructor
@Getter
@Setter
public class CountryHolidayCalender {
    private Long id;
    private boolean reOccuring;
    private String holidayType;
    private boolean isEnabled = true;
    private String googleCalId;
    private String holidayTitle;
    private LocalDate holidayDate;
    private String dayType;
    private LocalTime startTime;
    private LocalTime endTime;
    private String description;
    private Long dayTypeId;
    private String colorCode;

    public CountryHolidayCalender(LocalDate holidayDate, LocalTime startTime, LocalTime endTime) {
        this.holidayDate = holidayDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

}
