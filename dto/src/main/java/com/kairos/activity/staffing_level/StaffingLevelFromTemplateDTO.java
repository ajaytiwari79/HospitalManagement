package com.kairos.activity.staffing_level;/*
 *Created By Pavan on 14/8/18
 *
 */

import com.kairos.util.DateUtils;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class StaffingLevelFromTemplateDTO {
    private BigInteger templateId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Set<LocalDate> excludedDates;
    private Set<Long> selectedDayTypeIds;
    private Set<BigInteger> selectedActivityIds;

    public StaffingLevelFromTemplateDTO() {
        //Default Constructor
    }

    public StaffingLevelFromTemplateDTO(BigInteger templateId, LocalDate fromDate, LocalDate toDate,
                                        Set<LocalDate> excludedDates, Set<Long> selectedDayTypeIds, Set<BigInteger> selectedActivityIds) {
        this.templateId = templateId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.excludedDates = excludedDates;
        this.selectedDayTypeIds = selectedDayTypeIds;
        this.selectedActivityIds = selectedActivityIds;
    }

    public BigInteger getTemplateId() {
        return templateId;
    }

    public void setTemplateId(BigInteger templateId) {
        this.templateId = templateId;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public Set<LocalDate> getExcludedDates() {
        return excludedDates;
    }

    public void setExcludedDates(Set<LocalDate> excludedDates) {
        this.excludedDates = excludedDates;
    }

    public Set<Long> getSelectedDayTypeIds() {
        return selectedDayTypeIds;
    }

    public void setSelectedDayTypeIds(Set<Long> selectedDayTypeIds) {
        this.selectedDayTypeIds = selectedDayTypeIds;
    }

    public Set<BigInteger> getSelectedActivityIds() {
        return selectedActivityIds;
    }

    public void setSelectedActivityIds(Set<BigInteger> selectedActivityIds) {
        this.selectedActivityIds = selectedActivityIds;
    }

    public List<LocalDate> getDatesForCreatingStaffingLevel(){
        List<LocalDate> dates= DateUtils.getDates(this.fromDate,this.toDate);
        dates.removeAll(this.excludedDates);
        return dates;
    }
}
