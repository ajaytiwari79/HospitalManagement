package com.kairos.persistence.model.time_bank;

import com.kairos.persistence.model.activity.Shift;
import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

/*
* Created By Pradeep singh rajawat
*  Date-27/01/2018
*
* */
@Document(collection = "dailyTimeBankEntries")
public class DailyTimeBankEntry extends MongoBaseEntity{

    private Long unitPositionId;
    private Long staffId;
    //In minutes
    private int totalTimeBankMin;
    private int contractualMin;
    private int scheduledMin;
    private int timeBankMinWithoutCta;
    private int timeBankMinWithCta;
    private long accumultedTimeBankMin;
    private LocalDate date;
    private List<TimeBankCTADistribution> timeBankCTADistributionList;


    public DailyTimeBankEntry(Long unitPositionId, Long staffId, int unitWorkingDaysInWeek, LocalDate date) {
        this.unitPositionId = unitPositionId;
        this.staffId = staffId;
        this.date = date;
    }

    public List<TimeBankCTADistribution> getTimeBankCTADistributionList() {
        return timeBankCTADistributionList;
    }

    public void setTimeBankCTADistributionList(List<TimeBankCTADistribution> timeBankCTADistributionList) {
        this.timeBankCTADistributionList = timeBankCTADistributionList;
    }


    public DailyTimeBankEntry() {
    }


    public long getAccumultedTimeBankMin() {
        return accumultedTimeBankMin;
    }

    public void setAccumultedTimeBankMin(long accumultedTimeBankMin) {
        this.accumultedTimeBankMin = accumultedTimeBankMin;
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

    public int getTimeBankMinWithoutCta() {
        return timeBankMinWithoutCta;
    }

    public void setTimeBankMinWithoutCta(int timeBankMinWithoutCta) {
        this.timeBankMinWithoutCta = timeBankMinWithoutCta;
    }

    public int getTimeBankMinWithCta() {
        return timeBankMinWithCta;
    }

    public void setTimeBankMinWithCta(int timeBankMinWithCta) {
        this.timeBankMinWithCta = timeBankMinWithCta;
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


}
