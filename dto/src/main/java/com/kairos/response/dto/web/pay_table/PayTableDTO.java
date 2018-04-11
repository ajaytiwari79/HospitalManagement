package com.kairos.response.dto.web.pay_table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.user.pay_table.DateRange;
import com.kairos.persistence.model.user.pay_table.FutureDate;
import com.kairos.response.dto.web.pay_group_area.PayGroupAreaDTO;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.joda.time.DateTime;

import javax.validation.ConstraintValidatorContext;
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
    @NotNull(message = "short name  can't be null")
    private String shortName;
    private String description;

    @NotNull(message = "Start date can't be null")
    //@DateLong
    @FutureDate
    private Date startDateMillis;

    @FutureDate
    //@DateLong
    private Date endDateMillis;
    @NotNull(message = "Level can not be null")
    private Long levelId;

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

    public PayTableDTO(String name, String shortName, String description, Date startDateMillis, Date endDateMillis, Long levelId) {
        this.name = name;
        this.shortName = shortName;
        this.description = description;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
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
}
