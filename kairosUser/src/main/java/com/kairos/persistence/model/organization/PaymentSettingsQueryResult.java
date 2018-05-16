package com.kairos.persistence.model.organization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.response.dto.web.experties.PaidOutFrequencyEnum;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;

/**
 * Created by vipul on 17/4/18.
 */
@QueryResult
@JsonInclude(JsonInclude.Include.NON_EMPTY)   // this annotation is used if the field is empty then it will not be in your response
public class PaymentSettingsQueryResult {
    private Long id;
    private DayOfWeek weeklyPayDay;
    private DayOfWeek fornightlyPayDay;
    @Range(min = 1l, max = 31L)
    private Long monthlyPayDay;
    private Long lastFornightlyPayDate;

    public PaymentSettingsQueryResult() {
        //
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getMonthlyPayDay() {
        return monthlyPayDay;
    }

    public void setMonthlyPayDay(Long monthlyPayDay) {
        this.monthlyPayDay = monthlyPayDay;
    }

    public Long getLastFornightlyPayDate() {
        return lastFornightlyPayDate;
    }

    public void setLastFornightlyPayDate(Long lastFornightlyPayDate) {
        this.lastFornightlyPayDate = lastFornightlyPayDate;
    }
}
