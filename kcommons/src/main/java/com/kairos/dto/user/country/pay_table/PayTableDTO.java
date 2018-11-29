package com.kairos.dto.user.country.pay_table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.joda.time.DateTime;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.math.BigDecimal;
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
    private LocalDate startDateMillis;


    private LocalDate endDateMillis;
    @NotNull(message = "Level can not be null")
    private Long levelId;

    @NotNull(message = "Please provide payment unit type")
    private String paymentUnit;

    private BigDecimal percentageValue; // this value is being used to update paygrade and functional amount

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

    public LocalDate getStartDateMillis() {
        return startDateMillis;
    }

    public void setStartDateMillis(LocalDate startDateMillis) {
        this.startDateMillis = startDateMillis;
    }

    public LocalDate getEndDateMillis() {
        return endDateMillis;
    }

    public void setEndDateMillis(LocalDate endDateMillis) {
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

    public BigDecimal getPercentageValue() {
        return percentageValue;
    }

    public void setPercentageValue(BigDecimal percentageValue) {
        this.percentageValue = percentageValue;
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
            return endDateMillis.isAfter(startDateMillis.minusDays(1));
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
