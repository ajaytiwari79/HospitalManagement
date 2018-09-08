package com.kairos.user.country.time_slot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Jasgeet on 12/9/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeSlotsDeductionDTO {
    private int dayShiftTimeDeduction; //in percentage

    private int nightShiftTimeDeduction ; //in percentage

    public int getDayShiftTimeDeduction() {
        return dayShiftTimeDeduction;
    }

    public void setDayShiftTimeDeduction(int dayShiftTimeDeduction) {
        this.dayShiftTimeDeduction = dayShiftTimeDeduction;
    }

    public int getNightShiftTimeDeduction() {
        return nightShiftTimeDeduction;
    }

    public void setNightShiftTimeDeduction(int nightShiftTimeDeduction) {
        this.nightShiftTimeDeduction = nightShiftTimeDeduction;
    }
}
