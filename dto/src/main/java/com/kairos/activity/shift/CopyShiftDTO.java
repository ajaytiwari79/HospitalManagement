package com.kairos.activity.shift;

import com.kairos.persistence.model.user.country.Day;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

public class CopyShiftDTO {
    List<BigInteger> shiftIds;
    List<Long> staffIds;
    private Long expertiseId;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Day> selectedDays;
    private Integer tillWeeks;
    private Boolean copyAttachedJobs;
    private Boolean copyAttachedNotes;
    private Boolean copyChatConversation;
    private Boolean includeStopBrick;
    private Boolean includeVetoDays;
    private Boolean includeAvailability;

    public CopyShiftDTO() {
        // dc
    }

    public List<BigInteger> getShiftIds() {
        return shiftIds;
    }

    public void setShiftIds(List<BigInteger> shiftIds) {
        this.shiftIds = shiftIds;
    }

    public List<Long> getStaffIds() {
        return staffIds;
    }

    public void setStaffIds(List<Long> staffIds) {
        this.staffIds = staffIds;
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

    public List<Day> getSelectedDays() {
        return selectedDays;
    }

    public void setSelectedDays(List<Day> selectedDays) {
        this.selectedDays = selectedDays;
    }

    public Integer getTillWeeks() {
        return tillWeeks;
    }

    public void setTillWeeks(Integer tillWeeks) {
        this.tillWeeks = tillWeeks;
    }

    public Boolean getCopyAttachedJobs() {
        return copyAttachedJobs;
    }

    public void setCopyAttachedJobs(Boolean copyAttachedJobs) {
        this.copyAttachedJobs = copyAttachedJobs;
    }

    public Boolean getCopyAttachedNotes() {
        return copyAttachedNotes;
    }

    public void setCopyAttachedNotes(Boolean copyAttachedNotes) {
        this.copyAttachedNotes = copyAttachedNotes;
    }

    public Boolean getCopyChatConversation() {
        return copyChatConversation;
    }

    public void setCopyChatConversation(Boolean copyChatConversation) {
        this.copyChatConversation = copyChatConversation;
    }

    public Boolean getIncludeStopBrick() {
        return includeStopBrick;
    }

    public void setIncludeStopBrick(Boolean includeStopBrick) {
        this.includeStopBrick = includeStopBrick;
    }

    public Boolean getIncludeVetoDays() {
        return includeVetoDays;
    }

    public void setIncludeVetoDays(Boolean includeVetoDays) {
        this.includeVetoDays = includeVetoDays;
    }

    public Boolean getIncludeAvailability() {
        return includeAvailability;
    }

    public void setIncludeAvailability(Boolean includeAvailability) {
        this.includeAvailability = includeAvailability;
    }

    public Long getExpertiseId() {
        return expertiseId;
    }

    public void setExpertiseId(Long expertiseId) {
        this.expertiseId = expertiseId;
    }
}
