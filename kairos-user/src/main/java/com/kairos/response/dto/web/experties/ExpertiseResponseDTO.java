package com.kairos.response.dto.web.experties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.user.pay_table.FutureDate;
import org.neo4j.ogm.annotation.typeconversion.DateLong;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vipul on 29/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExpertiseResponseDTO {
    private Long id;
    private String name;
    private String description;
    @DateLong
    private Date startDateMillis;
    @DateLong
    private Date endDateMillis;
    private Long organizationLevelId;
    private Long serviceId;
    private Long unionId;
    private int fullTimeWeeklyMinutes;
    private Integer numberOfWorkingDaysInWeek;
    private Long payTableId;
    private PaidOutFrequencyEnum paidOutFrequency;
    private List<SeniorityLevelDTO> seniorityLevels = new ArrayList<>();
    private List<Long> tags;
    private Boolean published;

    public ExpertiseResponseDTO() {
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

    public Long getOrganizationLevelId() {
        return organizationLevelId;
    }

    public void setOrganizationLevelId(Long organizationLevelId) {
        this.organizationLevelId = organizationLevelId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long getUnionId() {
        return unionId;
    }

    public void setUnionId(Long unionId) {
        this.unionId = unionId;
    }

    public int getFullTimeWeeklyMinutes() {
        return fullTimeWeeklyMinutes;
    }

    public void setFullTimeWeeklyMinutes(int fullTimeWeeklyMinutes) {
        this.fullTimeWeeklyMinutes = fullTimeWeeklyMinutes;
    }

    public Integer getNumberOfWorkingDaysInWeek() {
        return numberOfWorkingDaysInWeek;
    }

    public void setNumberOfWorkingDaysInWeek(Integer numberOfWorkingDaysInWeek) {
        this.numberOfWorkingDaysInWeek = numberOfWorkingDaysInWeek;
    }

    public Long getPayTableId() {
        return payTableId;
    }

    public void setPayTableId(Long payTableId) {
        this.payTableId = payTableId;
    }

    public PaidOutFrequencyEnum getPaidOutFrequency() {
        return paidOutFrequency;
    }

    public void setPaidOutFrequency(PaidOutFrequencyEnum paidOutFrequency) {
        this.paidOutFrequency = paidOutFrequency;
    }

    public List<SeniorityLevelDTO> getSeniorityLevels() {
        return seniorityLevels;
    }

    public void setSeniorityLevels(List<SeniorityLevelDTO> seniorityLevels) {
        this.seniorityLevels = seniorityLevels;
    }

    public List<Long> getTags() {
        return tags;
    }

    public void setTags(List<Long> tags) {
        this.tags = tags;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }
}
