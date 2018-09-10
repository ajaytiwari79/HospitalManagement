package com.kairos.dto.user.country.pay_group_area;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.user.country.pay_table.DateRange;
import com.kairos.dto.user.country.pay_table.FutureDate;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by prabjot on 21/12/17.
 *
 * @MOdified by vipul for additional property
 */

@DateRange
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayGroupAreaDTO {

    private Long id;
    private Long payGroupAreaId;
    @NotNull(message = "Name can not be null")
    private String name;
    private String description;

    @NotNull(message = "Please select municipality")
    private Long municipalityId;

    @NotNull(message = "Start date can't be null")
    //@DateLong
    @FutureDate
    private Date startDateMillis;

    @FutureDate
    //@DateLong
    private Date endDateMillis;

    @NotNull(message = "Level can not be null")
    private Long levelId;

    public PayGroupAreaDTO() {
        //default constructor
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getMunicipalityId() {
        return municipalityId;
    }

    public void setMunicipalityId(Long municipalityId) {
        this.municipalityId = municipalityId;
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

    public Long getPayGroupAreaId() {
        return payGroupAreaId;
    }

    public void setPayGroupAreaId(Long payGroupAreaId) {
        this.payGroupAreaId = payGroupAreaId;
    }

    public PayGroupAreaDTO(@NotNull(message = "Name can not be null") String name, String description, Long municipalityId, @NotNull(message = "Start date can't be null") Date startDateMillis, Date endDateMillis, @NotNull Long levelId) {
        this.name = name;
        this.description = description;
        this.municipalityId = municipalityId;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
        this.levelId = levelId;
    }
}
