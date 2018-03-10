package com.kairos.response.dto.web.pay_level;

import com.kairos.persistence.model.user.pay_level.DateRange;
import com.kairos.persistence.model.user.pay_level.FutureDate;
import com.kairos.persistence.model.user.pay_level.ValidSize;
import org.neo4j.ogm.annotation.typeconversion.DateLong;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

/**
 * Created by prabjot on 21/12/17.
 *
 * @MOdified by vipul for additional property
 */

@DateRange
public class PayGroupAreaDTO {

    private Long id;
    @NotNull(message = "Name can not be null")
    private String name;
    private String description;

    @ValidSize(message = "Please select atleast 1 municipality")
    private Set<Long> municipalityId;

    @NotNull(message = "Start date can't be null")
    @DateLong
    @FutureDate
    private Date startDateMillis;

    @FutureDate
    @DateLong
    private Date endDateMillis;


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

    public Set<Long> getMunicipalityId() {
        return municipalityId;
    }

    public void setMunicipalityId(Set<Long> municipalityId) {
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

    public PayGroupAreaDTO(@NotNull(message = "Name can not be null") String name, String description, Set<Long> municipalityId, @NotNull(message = "Start date can't be null") Date startDateMillis, Date endDateMillis) {
        this.name = name;
        this.description = description;
        this.municipalityId = municipalityId;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
    }
}
