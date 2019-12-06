package com.kairos.persistence.model.time_bank;

import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.*;

import static com.kairos.commons.utils.ObjectUtils.isNullOrElse;

/*
* Created By Pradeep singh rajawat
*  Date-27/01/2018
*
* */
@Getter
@Setter
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
    private List<TimeBankCTADistribution> timeBankCTADistributionList=new ArrayList<>();
    private int deltaAccumulatedTimebankMinutes;
    private boolean publishedSomeActivities;
    //It is the sum of scheduledMinutesOfTimeBank + ctaBonusMinutesOfTimeBank
    private int plannedMinutesOfTimebank;
    private Map<LocalDate,Integer> publishedBalances;
    private DailyTimeBankEntry draftDailyTimeBankEntry;
    private int timeBankOffMinutes;
    private long protectedDaysOffMinutes;


    public DailyTimeBankEntry(Long employmentId, Long staffId, LocalDate date) {
        this.employmentId = employmentId;
        this.staffId = staffId;
        this.date = date;
        this.publishedBalances = new HashMap<>();
    }

    public DailyTimeBankEntry(Long employmentId, Long staffId, LocalDate date,int contractualMinutes,int deltaTimeBankMinutes) {
        this.employmentId = employmentId;
        this.staffId = staffId;
        this.date = date;
        this.publishedBalances = new HashMap<>();
        this.contractualMinutes = contractualMinutes;
        this.deltaTimeBankMinutes = deltaTimeBankMinutes;
    }


    public DailyTimeBankEntry() {
    }

    public Map<LocalDate, Integer> getPublishedBalances() {
        return publishedBalances= Optional.ofNullable(publishedBalances).orElse(new HashMap<>());
    }

    @Override
    public String toString() {
        return "DailyTimeBankEntry{" + "employmentId=" + employmentId + ", staffId=" + staffId + ", deltaTimeBankMinutes=" + deltaTimeBankMinutes + ", contractualMinutes=" + contractualMinutes + ", scheduledMinutesOfTimeBank=" + scheduledMinutesOfTimeBank + ", timeBankMinutesWithoutCta=" + timeBankMinutesWithoutCta + ", ctaBonusMinutesOfTimeBank=" + ctaBonusMinutesOfTimeBank + ", date=" + date + ", timeBankCTADistributionList=" + timeBankCTADistributionList + ", deltaAccumulatedTimebankMinutes=" + deltaAccumulatedTimebankMinutes + ", plannedMinutesOfTimebank=" + plannedMinutesOfTimebank + '}';
    }
}
