package com.kairos.dto.user.country.pay_table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.joda.time.DateTime;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Optional;

/**
 * Created by vipul on 15/3/18.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class PayTableDTO {
    private Long id;
    @NotNull(message = "name can't be null")
    private String name;
    private String shortName;
    private String description;

    @NotNull(message = "Start date can't be null")
    @FutureDate
    private Date startDateMillis;

    @FutureDate
    private Date endDateMillis;
    @NotNull(message = "Level can not be null")
    private Long levelId;

    @NotNull(message = "Please provide payment unit type")
    private String paymentUnit;

    public PayTableDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Date getStartDateMillis() {
        return startDateMillis;
    }

    public void setStartDateMillis(Date startDateMillis) {
        this.startDateMillis = startDateMillis;
    }

    public Date getEndDateMillis() {
        return endDateMillis;
    }

    public void setEndDateMillis(Date endDateMillis) {
        this.endDateMillis = endDateMillis;
    }

    public Long getLevelId() {
        return levelId;
    }

    public void setLevelId(Long levelId) {
        this.levelId = levelId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PayTableDTO(String name, String shortName, String description, Date startDateMillis, Date endDateMillis, String paymentUnit, Long levelId) {
        this.name = name;
        this.shortName = shortName;
        this.description = description;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
        this.paymentUnit = paymentUnit;
        this.levelId = levelId;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PayTableDTO{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", shortName='").append(shortName).append('\'');
        sb.append(", startDateMillis=").append(startDateMillis);
        sb.append(", endDateMillis=").append(endDateMillis);
        sb.append(", paymentUnit=").append(paymentUnit);
        sb.append(", levelId=").append(levelId);
        sb.append('}');
        return sb.toString();
    }

    @AssertTrue(message = "'start date' must be less than 'end date'.")
    public boolean isValid() {
        if (!Optional.ofNullable(this.startDateMillis).isPresent()) {
            return false;
        }
        if (Optional.ofNullable(this.endDateMillis).isPresent()) {
            DateTime endDateAsUtc = new DateTime(this.endDateMillis).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
            DateTime startDateAsUtc = new DateTime(this.startDateMillis).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
            boolean dateValue = (endDateAsUtc.isBefore(startDateAsUtc)) ? false : true;
            return dateValue;
        }
        return true;
    }

    public String getPaymentUnit() {
        return paymentUnit;
    }

    public void setPaymentUnit(String paymentUnit) {
        this.paymentUnit = paymentUnit;
    }
}
