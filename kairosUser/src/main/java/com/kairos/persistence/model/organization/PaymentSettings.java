package com.kairos.persistence.model.organization;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.response.dto.web.experties.PaidOutFrequencyEnum;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Range;
import org.neo4j.ogm.annotation.Labels;
import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.data.annotation.Version;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;

/**
 * Created by vipul on 12/4/18.
 */
@NodeEntity
public class PaymentSettings extends UserBaseEntity {

    private DayOfWeek weeklyPayDay;
    private DayOfWeek fornightlyPayDay;
    private Long lastFornigthtlyPayDate;
    @Range(min = 1l, max = 31L)
    private Long monthlyPayDay;


    public PaymentSettings() {
        // default cons
    }

    public PaymentSettings(DayOfWeek weeklyPayDay, DayOfWeek fornightlyPayDay, Long monthlyPayDay){
        this.weeklyPayDay = weeklyPayDay;
        this.monthlyPayDay = monthlyPayDay;
        this.fornightlyPayDay = fornightlyPayDay;
    }

    public DayOfWeek getWeeklyPayDay() {
        return weeklyPayDay;
    }

    public void setWeeklyPayDay(DayOfWeek weeklyPayDay) {
        this.weeklyPayDay = weeklyPayDay;
    }

    public DayOfWeek getFornightlyPayDay() {
        return fornightlyPayDay;
    }

    public void setFornightlyPayDay(DayOfWeek fornightlyPayDay) {
        this.fornightlyPayDay = fornightlyPayDay;
    }

    public Long getLastFornigthtlyPayDate() {
        return lastFornigthtlyPayDate;
    }

    public void setLastFornigthtlyPayDate(Long lastFornigthtlyPayDate) {
        this.lastFornigthtlyPayDate = lastFornigthtlyPayDate;
    }

    public Long getMonthlyPayDay() {
        return monthlyPayDay;
    }

    public void setMonthlyPayDay(Long monthlyPayDay) {
        this.monthlyPayDay = monthlyPayDay;
    }
}
