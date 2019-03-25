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
@Document(collection = "dailyTimeBankEntries")
public class DailyTimeBankEntry extends MongoBaseEntity{

    private Long unitPositionId;
    private Long staffId;
    //In minutes
    private int totalTimeBankMinutes; //It is Delta timebank
    private int contractualMinutes;
    private int scheduledMinutesOfTimeBank;
    private int timeBankMinutesWithoutCta;// It is the sum of scheduledMinutesOfTimeBank - contractualMinutes
    private int ctaBonusMinutesOfTimeBank;      //It is the sum of timeBankCTADistributionList minutes
    private long accumultedTimeBankMinutes;
    private LocalDate date;
    private List<TimeBankCTADistribution> timeBankCTADistributionList;
    private int deltaAccumulatedTimebankMinutes;


    public DailyTimeBankEntry(Long unitPositionId, Long staffId, LocalDate date) {
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


    public long getAccumultedTimeBankMinutes() {
        return accumultedTimeBankMinutes;
    }

    public void setAccumultedTimeBankMinutes(long accumultedTimeBankMinutes) {
        this.accumultedTimeBankMinutes = accumultedTimeBankMinutes;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getScheduledMinutesOfTimeBank() {
        return scheduledMinutesOfTimeBank;
    }

    public void setScheduledMinutesOfTimeBank(int scheduledMinutesOfTimeBank) {
        this.scheduledMinutesOfTimeBank = scheduledMinutesOfTimeBank;
    }

    public int getTimeBankMinutesWithoutCta() {
        return timeBankMinutesWithoutCta;
    }

    public void setTimeBankMinutesWithoutCta(int timeBankMinutesWithoutCta) {
        this.timeBankMinutesWithoutCta = timeBankMinutesWithoutCta;
    }

    public int getCtaBonusMinutesOfTimeBank() {
        return ctaBonusMinutesOfTimeBank;
    }

    public void setCtaBonusMinutesOfTimeBank(int ctaBonusMinutesOfTimeBank) {
        this.ctaBonusMinutesOfTimeBank = ctaBonusMinutesOfTimeBank;
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

    public int getTotalTimeBankMinutes() {
        return totalTimeBankMinutes;
    }

    public void setTotalTimeBankMinutes(int totalTimeBankMinutes) {
        this.totalTimeBankMinutes = totalTimeBankMinutes;
    }

    public int getContractualMinutes() {
        return contractualMinutes;
    }

    public void setContractualMinutes(int contractualMinutes) {
        this.contractualMinutes = contractualMinutes;
    }

    public int getDeltaAccumulatedTimebankMinutes() {
        return deltaAccumulatedTimebankMinutes;
    }

    public void setDeltaAccumulatedTimebankMinutes(int deltaAccumulatedTimebankMinutes) {
        this.deltaAccumulatedTimebankMinutes = deltaAccumulatedTimebankMinutes;
    }
}
