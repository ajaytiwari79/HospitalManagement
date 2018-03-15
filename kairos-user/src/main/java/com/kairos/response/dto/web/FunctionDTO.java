package com.kairos.response.dto.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;
import java.util.List;

/**
 * Created by pavan on 13/3/18.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FunctionDTO {
    private Long id;
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;
    private List<Long> unionIds;
    private List<Long> organizationLevelIds;

    public FunctionDTO() {
        //Default Constructor
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<Long> getUnionIds() {
        return unionIds;
    }

    public void setUnionIds(List<Long> unionIds) {
        this.unionIds = unionIds;
    }

    public List<Long> getOrganizationLevelIds() {
        return organizationLevelIds;
    }

    public void setOrganizationLevelIds(List<Long> organizationLevelIds) {
        this.organizationLevelIds = organizationLevelIds;
    }
}
