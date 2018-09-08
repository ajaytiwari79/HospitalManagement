package com.kairos.activity.time_bank;


import org.joda.time.LocalDate;


/*
* Created By Pradeep singh rajawat
*  Date-27/01/2018
*
* */
public class CalculatedTimeBankByDateDTO {

    private LocalDate date;
    private int timeBankMin;

    public CalculatedTimeBankByDateDTO(LocalDate date, int timeBankMin) {
        this.date = date;
        this.timeBankMin = timeBankMin;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getTimeBankMin() {
        return timeBankMin;
    }

    public void setTimeBankMin(int timeBankMin) {
        this.timeBankMin = timeBankMin;
    }
}
