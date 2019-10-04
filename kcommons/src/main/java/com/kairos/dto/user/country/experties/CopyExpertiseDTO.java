package com.kairos.dto.user.country.experties;

import com.kairos.dto.user.organization.union.SectorDTO;
import com.kairos.dto.user.organization.union.UnionIDNameDTO;
import com.kairos.enums.shift.BreakPaymentSetting;
import org.joda.time.DateTime;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CopyExpertiseDTO {
    private Long id;
    @NotBlank(message = "Expertise name is required")
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long organizationLevelId;
    private Set<Long> organizationServiceIds;
    /*private Long unionId;*/
    private UnionIDNameDTO union;
    private Integer fullTimeWeeklyMinutes; // This is equals to 37 hours
    private Integer numberOfWorkingDaysInWeek; // 5 or 7

    private List<SeniorityLevelDTO> seniorityLevels;
    private List<Long> tags;
    private BreakPaymentSetting breakPaymentSetting;
    private Long parentId;
    // TODO REMOVE FOR FE compactibility
    private Long startDateMillis;
    private Long endDateMillis;
    private SectorDTO sector;

    public CopyExpertiseDTO() {
        // DC
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name.trim();
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
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

    public Integer getNumberOfWorkingDaysInWeek() {
        return numberOfWorkingDaysInWeek;
    }

    public void setNumberOfWorkingDaysInWeek(Integer numberOfWorkingDaysInWeek) {
        this.numberOfWorkingDaysInWeek = numberOfWorkingDaysInWeek;
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

    public BreakPaymentSetting getBreakPaymentSetting() {
        return breakPaymentSetting;
    }

    public void setBreakPaymentSetting(BreakPaymentSetting breakPaymentSetting) {
        this.breakPaymentSetting = breakPaymentSetting;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getStartDateMillis() {
        return startDateMillis;
    }

    public void setStartDateMillis(Long startDateMillis) {
        this.startDateMillis = startDateMillis;
    }

    public Long getEndDateMillis() {
        return endDateMillis;
    }

    public void setEndDateMillis(Long endDateMillis) {
        this.endDateMillis = endDateMillis;
    }

    public CopyExpertiseDTO(@NotBlank(message = "Expertise name is required") String name, LocalDate startDate, LocalDate endDate, String description, @NotNull(message = "Level can not be null") Long organizationLevelId, @NotNull(message = "services can not be null") Set<Long> organizationServiceIds, Integer fullTimeWeeklyMinutes, Integer numberOfWorkingDaysInWeek, List<SeniorityLevelDTO> seniorityLevels, @NotNull(message = "Please select payment type") BreakPaymentSetting breakPaymentSetting) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.organizationLevelId = organizationLevelId;
        this.organizationServiceIds = organizationServiceIds;
        this.fullTimeWeeklyMinutes = fullTimeWeeklyMinutes;
        this.numberOfWorkingDaysInWeek = numberOfWorkingDaysInWeek;
        this.seniorityLevels = seniorityLevels;
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

    @AssertTrue(message = "message.start_date.less_than.end_date")
    public boolean isValid() {
        if (!Optional.ofNullable(this.startDateMillis).isPresent() && Optional.ofNullable(this.endDateMillis).isPresent()) {
            return false;
        } else if (Optional.ofNullable(this.startDateMillis).isPresent() && (Optional.ofNullable(this.endDateMillis).isPresent())) {
            DateTime endDateAsUtc = new DateTime(this.endDateMillis).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
            DateTime startDateAsUtc = new DateTime(this.startDateMillis).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
            return !endDateAsUtc.isBefore(startDateAsUtc);
        }
        return true;
    }
}
