package com.kairos.dto.activity.time_bank;


/*
* Created By Pradeep singh rajawat
*  Date-27/01/2018
*
* */
public class TimeBankByDateDTO {

    private int timeBankChangeMinutes;
    private long accumulatedTimebankMinutes;


    public TimeBankByDateDTO(int timeBankChangeMinutes, long accumulatedTimebankMinutes) {
        this.timeBankChangeMinutes = timeBankChangeMinutes;
        this.accumulatedTimebankMinutes = accumulatedTimebankMinutes;
    }

    public TimeBankByDateDTO() {
    }

    public int getTimeBankChangeMinutes() {
        return timeBankChangeMinutes;
    }

    public void setTimeBankChangeMinutes(int timeBankChangeMinutes) {
        this.timeBankChangeMinutes = timeBankChangeMinutes;
    }

    public long getAccumulatedTimebankMinutes() {
        return accumulatedTimebankMinutes;
    }

    public void setAccumulatedTimebankMinutes(long accumulatedTimebankMinutes) {
        this.accumulatedTimebankMinutes = accumulatedTimebankMinutes;
    }
}
