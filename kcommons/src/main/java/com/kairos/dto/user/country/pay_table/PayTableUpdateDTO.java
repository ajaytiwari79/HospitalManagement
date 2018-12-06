package com.kairos.dto.user.country.pay_table;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by vipul on 19/3/18.
 */
public class PayTableUpdateDTO {
    private Long id;
    @NotNull(message = "name can't be null")
    private String name;
    private String shortName;
    private String description;
    @NotNull(message = "Start date can't be null")
    private Date startDateMillis;

    private Date endDateMillis;
    @NotNull(message = "Level can not be null")
    private Long levelId;
    @NotNull(message = "Please provide payment unit type")
    private String paymentUnit;

    public PayTableUpdateDTO() {
        //Default cons
    }

    public PayTableUpdateDTO(Long id, @NotNull(message = "name can't be null") String name) {
        this.id = id;
        this.name = name;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public PayTableUpdateDTO(String name, String shortName, String description, Date startDateMillis, Date endDateMillis, String paymentUnit, Long levelId) {
        this.name = name;
        this.shortName = shortName;
        this.description = description;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
        this.paymentUnit = paymentUnit;
        this.levelId=levelId;
    }

    public String getPaymentUnit() {
        return paymentUnit;
    }

    public void setPaymentUnit(String paymentUnit) {
        this.paymentUnit = paymentUnit;
    }
}
