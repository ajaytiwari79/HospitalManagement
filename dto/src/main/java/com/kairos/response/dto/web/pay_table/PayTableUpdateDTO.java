package com.kairos.response.dto.web.pay_table;

import com.kairos.persistence.model.user.pay_table.FutureDate;
import org.neo4j.ogm.annotation.typeconversion.DateLong;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by vipul on 19/3/18.
 */
public class PayTableUpdateDTO {
    private Long id;
    @NotNull(message = "name can't be null")
    private String name;
    @NotNull(message = "short name  can't be null")
    private String shortName;
    private String description;
    @NotNull(message = "Start date can't be null")
    //@DateLong
    private Date startDateMillis;
    //@DateLong
    private Date endDateMillis;
    @NotNull(message = "Level can not be null")
    private Long levelId;

    public PayTableUpdateDTO() {
        //Default cons
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

    public PayTableUpdateDTO(String name, String shortName, String description, Date startDateMillis, Date endDateMillis, Long levelId) {
        this.name = name;
        this.shortName = shortName;
        this.description = description;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
        this.levelId=levelId;
    }
}
