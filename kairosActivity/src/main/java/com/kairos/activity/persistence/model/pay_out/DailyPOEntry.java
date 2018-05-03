package com.kairos.activity.persistence.model.pay_out;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class DailyPOEntry extends MongoBaseEntity {
    private Long unitPositionId;
    private int totalPOMinutes;
    private int contractualMinutes;
    private int scheduledMinutes;
    private int poMinutesWithoutCta;
    private int poMinutesWithCta;
    private LocalDate date;
    private List<PayOutCTADistribution> poCtaDistibList;

    public DailyPOEntry(){ }

    public DailyPOEntry(Long unitPositionId, int unitWorkingDaysInWeek, LocalDate date){
        this.unitPositionId = unitPositionId;
        this.date = date;
    }

    public Long getUnitPositionId() {
        return unitPositionId;
    }

    public void setUnitPositionId(Long unitPositionId) {
        this.unitPositionId = unitPositionId;
    }

    public int getTotalPOMinutes() {
        return totalPOMinutes;
    }

    public void setTotalPOMinutes(int totalPOMinutes) {
        this.totalPOMinutes = totalPOMinutes;
    }

    public int getContractualMinutes() {
        return contractualMinutes;
    }

    public void setContractualMinutes(int contractualMinutes) {
        this.contractualMinutes = contractualMinutes;
    }

    public int getScheduledMinutes() {
        return scheduledMinutes;
    }

    public void setScheduledMinutes(int scheduledMinutes) {
        this.scheduledMinutes = scheduledMinutes;
    }

    public int getPoMinutesWithoutCta() {
        return poMinutesWithoutCta;
    }

    public void setPoMinutesWithoutCta(int poMinutesWithoutCta) {
        this.poMinutesWithoutCta = poMinutesWithoutCta;
    }

    public int getPoMinutesWithCta() {
        return poMinutesWithCta;
    }

    public void setPoMinutesWithCta(int poMinutesWithCta) {
        this.poMinutesWithCta = poMinutesWithCta;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<PayOutCTADistribution> getPoCtaDistibList() {
        return poCtaDistibList;
    }

    public void setPoCtaDistibList(List<PayOutCTADistribution> poCtaDistibList) {
        this.poCtaDistibList = poCtaDistibList;
    }
}
