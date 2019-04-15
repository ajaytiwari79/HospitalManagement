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

    private Long employmentId;
    private Long staffId;
    //It is Delta timebank
    private int deltaTimeBankMinutes;
    private int contractualMinutes;
    //It is the scheduled minutes of Ruletemplate which accountType is equal to TIMEBANK_ACCOUNT
    private int scheduledMinutesOfTimeBank;
    // It is the sum of scheduledMinutesOfTimeBank - contractualMinutes
    private int timeBankMinutesWithoutCta;
    //It Includes CTAcompensation of Function and Bonus Ruletemplate which accountType is equal to TIMEBANK_ACCOUNT
    private int ctaBonusMinutesOfTimeBank;
    private LocalDate date;
    //It Includes CTAcompensation of Function and Bonus Ruletemplate which accountType is equal to TIMEBANK_ACCOUNT
    private List<TimeBankCTADistribution> timeBankCTADistributionList;
    private int deltaAccumulatedTimebankMinutes;
    //It is the sum of scheduledMinutesOfTimeBank + ctaBonusMinutesOfTimeBank
    private int plannedMinutesOfTimebank;


    public DailyTimeBankEntry(Long employmentId, Long staffId, LocalDate date) {
        this.employmentId = employmentId;
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

    public Long getEmploymentId() {
        return employmentId;
    }

    public void setEmploymentId(Long employmentId) {
        this.employmentId = employmentId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public int getDeltaTimeBankMinutes() {
        return deltaTimeBankMinutes;
    }

    public void setDeltaTimeBankMinutes(int deltaTimeBankMinutes) {
        this.deltaTimeBankMinutes = deltaTimeBankMinutes;
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

    public int getPlannedMinutesOfTimebank() {
        return plannedMinutesOfTimebank;
    }

    public void setPlannedMinutesOfTimebank(int plannedMinutesOfTimebank) {
        this.plannedMinutesOfTimebank = plannedMinutesOfTimebank;
    }

    @Override
    public String toString() {
        return "DailyTimeBankEntry{" + "unitPositionId=" + employmentId + ", staffId=" + staffId + ", deltaTimeBankMinutes=" + deltaTimeBankMinutes + ", contractualMinutes=" + contractualMinutes + ", scheduledMinutesOfTimeBank=" + scheduledMinutesOfTimeBank + ", timeBankMinutesWithoutCta=" + timeBankMinutesWithoutCta + ", ctaBonusMinutesOfTimeBank=" + ctaBonusMinutesOfTimeBank + ", date=" + date + ", timeBankCTADistributionList=" + timeBankCTADistributionList + ", deltaAccumulatedTimebankMinutes=" + deltaAccumulatedTimebankMinutes + ", plannedMinutesOfTimebank=" + plannedMinutesOfTimebank + '}';
    }
}
