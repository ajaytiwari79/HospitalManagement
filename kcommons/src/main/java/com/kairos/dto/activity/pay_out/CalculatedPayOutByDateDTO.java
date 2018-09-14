package com.kairos.dto.activity.pay_out;


import org.joda.time.LocalDate;


/*
* Created By Pradeep singh rajawat
*  Date-27/01/2018
*
* */
public class CalculatedPayOutByDateDTO {

    private LocalDate date;
    private int payOutMin;

    public CalculatedPayOutByDateDTO(LocalDate date, int payOutMin) {
        this.date = date;
        this.payOutMin = payOutMin;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getPayOutMin() {
        return payOutMin;
    }

    public void setPayOutMin(int payOutMin) {
        this.payOutMin = payOutMin;
    }
}
