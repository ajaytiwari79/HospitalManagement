package com.kairos.dto.activity.client_exception;

import java.math.BigInteger;

/**
 * Created by prabjot on 21/7/17.
 */
public class ClientExceptionCount {
    private BigInteger exceptionTypeId;
    private int exceptionsTodayCount;
    private int exceptionsTomorrowCount;
    private int exceptionsDayAfterTomorrowCount;
    private int exceptionsOneWeekCount;
    private int exceptionsTwoWeekCount;
    private int exceptionsThreeWeekCount;
    private int exceptionsFourWeekCount;

    public BigInteger getExceptionTypeId() {
        return exceptionTypeId;
    }

    public void setExceptionTypeId(BigInteger exceptionTypeId) {
        this.exceptionTypeId = exceptionTypeId;
    }

    public int getExceptionsTodayCount() {
        return exceptionsTodayCount;
    }

    public void setExceptionsTodayCount(int exceptionsTodayCount) {
        this.exceptionsTodayCount = exceptionsTodayCount;
    }

    public int getExceptionsTomorrowCount() {
        return exceptionsTomorrowCount;
    }

    public void setExceptionsTomorrowCount(int exceptionsTomorrowCount) {
        this.exceptionsTomorrowCount = exceptionsTomorrowCount;
    }

    public int getExceptionsDayAfterTomorrowCount() {
        return exceptionsDayAfterTomorrowCount;
    }

    public void setExceptionsDayAfterTomorrowCount(int exceptionsDayAfterTomorrowCount) {
        this.exceptionsDayAfterTomorrowCount = exceptionsDayAfterTomorrowCount;
    }

    public int getExceptionsOneWeekCount() {
        return exceptionsOneWeekCount;
    }

    public void setExceptionsOneWeekCount(int exceptionsOneWeekCount) {
        this.exceptionsOneWeekCount = exceptionsOneWeekCount;
    }

    public int getExceptionsTwoWeekCount() {
        return exceptionsTwoWeekCount;
    }

    public void setExceptionsTwoWeekCount(int exceptionsTwoWeekCount) {
        this.exceptionsTwoWeekCount = exceptionsTwoWeekCount;
    }

    public int getExceptionsThreeWeekCount() {
        return exceptionsThreeWeekCount;
    }

    public void setExceptionsThreeWeekCount(int exceptionsThreeWeekCount) {
        this.exceptionsThreeWeekCount = exceptionsThreeWeekCount;
    }

    public int getExceptionsFourWeekCount() {
        return exceptionsFourWeekCount;
    }

    public void setExceptionsFourWeekCount(int exceptionsFourWeekCount) {
        this.exceptionsFourWeekCount = exceptionsFourWeekCount;
    }

    public ClientExceptionCount(BigInteger exceptionTypeId) {
        this.exceptionTypeId = exceptionTypeId;
    }

    public void resetValues(){
        this.exceptionsTodayCount = 0;
        this.exceptionsTomorrowCount = 0;
        this.exceptionsDayAfterTomorrowCount=0;
        this.exceptionsOneWeekCount=0;
        this.exceptionsTwoWeekCount=0;
        this.exceptionsThreeWeekCount=0;
        this.exceptionsFourWeekCount=0;
    }

    @Override
    public String toString() {
        return "ClientExceptionCount{" +
                "exceptionTypeId=" + exceptionTypeId +
                ", exceptionsTodayCount=" + exceptionsTodayCount +
                ", exceptionsTomorrowCount=" + exceptionsTomorrowCount +
                ", exceptionsDayAfterTomorrowCount=" + exceptionsDayAfterTomorrowCount +
                ", exceptionsOneWeekCount=" + exceptionsOneWeekCount +
                ", exceptionsTwoWeekCount=" + exceptionsTwoWeekCount +
                ", exceptionsThreeWeekCount=" + exceptionsThreeWeekCount +
                ", exceptionsFourWeekCount=" + exceptionsFourWeekCount +
                '}';
    }
}
