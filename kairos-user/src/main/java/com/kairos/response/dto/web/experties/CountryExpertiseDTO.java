package com.kairos.response.dto.web.experties;

import com.kairos.persistence.model.user.pay_table.FutureDate;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.typeconversion.DateLong;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * Created by prerna on 14/11/17.
 */
public class CountryExpertiseDTO {

    private long id;

    @NotEmpty(message = "error.Expertise.name.notEmpty")
    @NotNull(message = "error.Expertise.name.notnull")
    private String name;

    //@NotEmpty(message = "error.Expertise.description.notEmpty") @NotNull(message = "error.Expertise.description.notnull")
    private String description;
    @NotNull(message = "Start date can't be null")
    @DateLong
    @FutureDate
    private Date startDateMillis;

    @FutureDate
    @DateLong
    private Date endDateMillis;
    @NotNull(message = "Level can not be null")
    private Long levelId;

    @NotNull(message = "services can not be null")
    private Long serviceId;

    @NotNull(message = "union can not be null")
    private Long unionId;

    private int fullTimeWeeklyHours;

    private List<Long> tags;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public List<Long> getTags() {
        return tags;
    }

    public void setTags(List<Long> tags) {
        this.tags = tags;
    }
}
