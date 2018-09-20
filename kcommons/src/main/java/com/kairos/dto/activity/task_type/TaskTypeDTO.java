package com.kairos.dto.activity.task_type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by prabjot on 22/2/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskTypeDTO {

    @NotNull(message = "error.Tasktype.title.notnull") @NotEmpty(message = "error.Tasktype.title.notnull")
    private String title;

    private Date expiresOn;


    private String description;

    private BigInteger id;

    private Boolean status;
    private int duration;
    private Long serviceId;
    private String parentTaskTypeId;
    private String colorForGantt;


    public String getColorForGantt() {
        return colorForGantt;
    }

    public void setColorForGantt(String colorForGantt) {
        this.colorForGantt = colorForGantt;
    }

    private List<BigInteger> tags = new ArrayList<>();

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public TaskTypeDTO() {
        //default constructor
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getParentTaskTypeId() {
        return parentTaskTypeId;
    }

    public void setParentTaskTypeId(String parentTaskTypeId) {
        this.parentTaskTypeId = parentTaskTypeId;
    }

    public TaskTypeDTO(String title, Date expiresOn, String description, BigInteger id, boolean status) {
        this.title = title;
        this.expiresOn = expiresOn;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }


    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public Date getExpiresOn() {
        return expiresOn;
    }

    public void setExpiresOn(Date expiresOn) {
        this.expiresOn = expiresOn;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<BigInteger> getTags() {
        return tags;
    }

    public void setTags(List<BigInteger> tags) {
        this.tags = tags;
    }
}
