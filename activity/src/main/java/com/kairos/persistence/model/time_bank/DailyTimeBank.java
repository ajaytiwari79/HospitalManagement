package com.kairos.persistence.model.time_bank;

import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

/*
* Created By Pradeep singh rajawat
*  Date-27/01/2018
*
* */
@Document(collection = "time_bank")
public class DailyTimeBank extends MongoBaseEntity{

    private Long unitPositionId;
    private Long staffId;
    //In minutes
    private int totalTimeBankMin;
    private int contractualMin;
    private int unitWorkingDaysInWeek;
    private int totalContractualMinInWeek;
    private int scheduledMin;
    private int timeBankMinBeforeCta;
    private int timeBankMinAfterCta;
    private LocalDate date;
    private List<TimeBankDistribution> timeBankDistributionList;


    public DailyTimeBank(Long unitPositionId, Long staffId, int contractualMin, int unitWorkingDaysInWeek) {
        this.unitPositionId = unitPositionId;
        this.staffId = staffId;
        this.contractualMin = contractualMin;
        this.unitWorkingDaysInWeek = unitWorkingDaysInWeek;
    }

    public DailyTimeBank(Long unitPositionId, Long staffId, int unitWorkingDaysInWeek, LocalDate date) {
        this.unitPositionId = unitPositionId;
        this.staffId = staffId;
        this.unitWorkingDaysInWeek = unitWorkingDaysInWeek;
        this.date = date;
    }

    public List<TimeBankDistribution> getTimeBankDistributionList() {
        return timeBankDistributionList;
    }

    public void setTimeBankDistributionList(List<TimeBankDistribution> timeBankDistributionList) {
        this.timeBankDistributionList = timeBankDistributionList;
    }

    public int getTotalContractualMinInWeek() {
        return totalContractualMinInWeek;
    }

    public void setTotalContractualMinInWeek(int totalContractualMinInWeek) {
        this.totalContractualMinInWeek = totalContractualMinInWeek;
    }

    public DailyTimeBank() {
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getScheduledMin() {
        return scheduledMin;
    }

    public void setScheduledMin(int scheduledMin) {
        this.scheduledMin = scheduledMin;
    }

    public int getTimeBankMinBeforeCta() {
        return timeBankMinBeforeCta;
    }

    public void setTimeBankMinBeforeCta(int timeBankMinBeforeCta) {
        this.timeBankMinBeforeCta = timeBankMinBeforeCta;
    }

    public int getTimeBankMinAfterCta() {
        return timeBankMinAfterCta;
    }

    public void setTimeBankMinAfterCta(int timeBankMinAfterCta) {
        this.timeBankMinAfterCta = timeBankMinAfterCta;
    }

    public Long getUnitPositionId() {
        return unitPositionId;
    }

    public void setUnitPositionId(Long unitPositionId) {
        this.unitPositionId = unitPositionId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public int getTotalTimeBankMin() {
        return totalTimeBankMin;
    }

    public void setTotalTimeBankMin(int totalTimeBankMin) {
        this.totalTimeBankMin = totalTimeBankMin;
    }

    public int getContractualMin() {
        return contractualMin;
    }

    public void setContractualMin(int contractualMin) {
        this.contractualMin = contractualMin;
    }

    public int getUnitWorkingDaysInWeek() {
        return unitWorkingDaysInWeek;
    }

    public void setUnitWorkingDaysInWeek(int unitWorkingDaysInWeek) {
        this.unitWorkingDaysInWeek = unitWorkingDaysInWeek;
    }

}
