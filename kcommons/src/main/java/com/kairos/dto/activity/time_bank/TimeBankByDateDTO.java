package com.kairos.dto.activity.time_bank;


/*
* Created By Pradeep singh rajawat
*  Date-27/01/2018
*
* */
public class TimeBankByDateDTO {

    private int timeBankChangeMinutes;
    private long accumulatedTimebankMinutes;
    private long expectedTimebankMinutes;
    private long publishedBalancesMinutes;


    public TimeBankByDateDTO(int timeBankChangeMinutes, long accumulatedTimebankMinutes,long expectedTimebankMinutes,long publishedBalancesMinutes) {
        this.timeBankChangeMinutes = timeBankChangeMinutes;
        this.accumulatedTimebankMinutes = accumulatedTimebankMinutes;
        this.expectedTimebankMinutes = expectedTimebankMinutes;
        this.publishedBalancesMinutes = publishedBalancesMinutes;
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

    public long getExpectedTimebankMinutes() {
        return expectedTimebankMinutes;
    }

    public void setExpectedTimebankMinutes(long expectedTimebankMinutes) {
        this.expectedTimebankMinutes = expectedTimebankMinutes;
    }

    public long getPublishedBalancesMinutes() {
        return publishedBalancesMinutes;
    }

    public void setPublishedBalancesMinutes(long publishedBalancesMinutes) {
        this.publishedBalancesMinutes = publishedBalancesMinutes;
    }
}
