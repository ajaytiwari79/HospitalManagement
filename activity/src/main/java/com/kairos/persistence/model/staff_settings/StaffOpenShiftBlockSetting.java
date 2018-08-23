package com.kairos.persistence.model.staff_settings;
/*
 * Created By Pavan on 17/8/18
 */

import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class StaffOpenShiftBlockSetting extends MongoBaseEntity {
    private Long staffId;
    private Set<BigInteger> activityIds;
    private Set<LocalDate> dateForDay;
    private Set<LocalDate> dateForWeek;

    public StaffOpenShiftBlockSetting() {
        //Default Constructor
    }

    public StaffOpenShiftBlockSetting(Long staffId) {
        this.staffId = staffId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public Set<BigInteger> getActivityIds() {
        return activityIds=Optional.ofNullable(activityIds).orElse(new HashSet<>());
    }

    public void setActivityIds(Set<BigInteger> activityIds) {
        this.activityIds = activityIds;
    }

    public Set<LocalDate> getDateForDay() {
        return dateForDay=Optional.ofNullable(dateForDay).orElse(new HashSet<>());
    }

    public void setDateForDay(Set<LocalDate> dateForDay) {
        this.dateForDay = dateForDay;
    }

    public Set<LocalDate> getDateForWeek() {
        return dateForWeek=Optional.ofNullable(dateForWeek).orElse(new HashSet<>());
    }

    public void setDateForWeek(Set<LocalDate> dateForWeek) {
        this.dateForWeek = dateForWeek;
    }
}
