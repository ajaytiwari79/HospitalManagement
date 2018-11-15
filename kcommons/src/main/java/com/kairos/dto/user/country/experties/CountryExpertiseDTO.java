package com.kairos.dto.user.country.experties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.user.organization.union.SectorDTO;
import com.kairos.dto.user.organization.union.UnionIDNameDTO;
import com.kairos.enums.shift.BreakPaymentSetting;
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
 * Created by prerna on 14/11/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryExpertiseDTO {

    private Long id;

    @NotBlank(message="Expertise name is required")
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
  /*  private Long unionId;*/
    private UnionIDNameDTO union;
    private Integer fullTimeWeeklyMinutes ; // This is equals to 37 hours
    private Integer numberOfWorkingDaysInWeek; // 5 or 7

    @Valid
    private SeniorityLevelDTO seniorityLevel;

    private List<Long> tags;
    private boolean published;

    @NotNull(message="Please select payment type")
    private BreakPaymentSetting breakPaymentSetting;

    private SectorDTO sector;



    public CountryExpertiseDTO() {
    }

    public CountryExpertiseDTO(Long id, @Valid SeniorityLevelDTO seniorityLevel) {
        this.id = id;
        this.seniorityLevel = seniorityLevel;
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

    public List<Long> getTags() {
        return tags;
    }

    public void setTags(List<Long> tags) {
        this.tags = tags;
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
        this.endDateMillis =endDateMillis==null?null: DateUtils.getEndOfDay(endDateMillis);

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



    public Integer getFullTimeWeeklyMinutes() {
        return fullTimeWeeklyMinutes;
    }

    public void setFullTimeWeeklyMinutes(Integer fullTimeWeeklyMinutes) {
        this.fullTimeWeeklyMinutes = fullTimeWeeklyMinutes;
    }

    public SeniorityLevelDTO getSeniorityLevel() {
        return seniorityLevel;
    }

    public void setSeniorityLevel(SeniorityLevelDTO seniorityLevel) {
        this.seniorityLevel = seniorityLevel;
    }

    public Integer getNumberOfWorkingDaysInWeek() {
        return numberOfWorkingDaysInWeek;
    }

    public void setNumberOfWorkingDaysInWeek(Integer numberOfWorkingDaysInWeek) {
        this.numberOfWorkingDaysInWeek = numberOfWorkingDaysInWeek;
    }



    public CountryExpertiseDTO(@NotNull(message = "error.Expertise.name.notnull") String name, String description, @NotNull(message = "Start date can't be null") Date startDateMillis, Date endDateMillis, @NotNull(message = "Level can not be null") Long organizationLevelId, @NotNull(message = "services can not be null") Set<Long> organizationServiceIds, Integer fullTimeWeeklyMinutes, Integer numberOfWorkingDaysInWeek, @Valid SeniorityLevelDTO seniorityLevel) {
        this.name = name;
        this.description = description;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
        this.organizationLevelId = organizationLevelId;
        this.organizationServiceIds = organizationServiceIds;
        this.fullTimeWeeklyMinutes = fullTimeWeeklyMinutes;
        this.numberOfWorkingDaysInWeek = numberOfWorkingDaysInWeek;
        this.seniorityLevel = seniorityLevel;
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

    public BreakPaymentSetting getBreakPaymentSetting() {
        return breakPaymentSetting;
    }

    public void setBreakPaymentSetting(BreakPaymentSetting breakPaymentSetting) {
        this.breakPaymentSetting = breakPaymentSetting;
    }


    public SectorDTO getSector() {
        return sector;
    }

    public void setSector(SectorDTO sector) {
        this.sector = sector;
    }

    public UnionIDNameDTO getUnion() {
        return union;
    }

    public void setUnion(UnionIDNameDTO union) {
        this.union = union;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }
}
