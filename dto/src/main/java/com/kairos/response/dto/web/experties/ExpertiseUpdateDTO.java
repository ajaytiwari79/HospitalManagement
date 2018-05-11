package com.kairos.response.dto.web.experties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.joda.time.DateTime;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by vipul on 30/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExpertiseUpdateDTO {
    private Long id;
    @NotBlank(message="Expertise name can't be empty or null or blank")
    private String name;
    private String description;

    @NotNull(message = "Start date can't be null")
    private Date startDateMillis;

    private Date endDateMillis;

    @NotNull(message = "Level can not be null")
    private Long organizationLevelId;

    @NotNull(message = "services can not be null")
    private Set<Long> organizationServiceIds;

    @NotNull(message = "union can not be null")
    private Long unionId;
    private int fullTimeWeeklyMinutes; // This is equals to 37 hours
    private Integer numberOfWorkingDaysInWeek; // 5 or 7

    @NotNull(message = "Paid Out Frequency can not be null")
    private PaidOutFrequencyEnum paidOutFrequency;

    @Valid
    private SeniorityLevelDTO seniorityLevel;


    private List<Long> tags;
    private Boolean published;

    public ExpertiseUpdateDTO() {
        //default cons
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

    public Set<Long> getOrganizationServiceIds() {
        return organizationServiceIds;
    }

    public void setOrganizationServiceIds(Set<Long> organizationServiceIds) {
        this.organizationServiceIds = organizationServiceIds;
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


    public PaidOutFrequencyEnum getPaidOutFrequency() {
        return paidOutFrequency;
    }

    public void setPaidOutFrequency(PaidOutFrequencyEnum paidOutFrequency) {
        this.paidOutFrequency = paidOutFrequency;
    }

    public SeniorityLevelDTO getSeniorityLevel() {
        return seniorityLevel;
    }

    public void setSeniorityLevel(SeniorityLevelDTO seniorityLevel) {
        this.seniorityLevel = seniorityLevel;
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
