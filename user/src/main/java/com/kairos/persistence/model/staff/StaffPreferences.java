package com.kairos.persistence.model.staff;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;

import java.math.BigInteger;
import java.util.*;


@NodeEntity
public class StaffPreferences extends UserBaseEntity {
    private Set<BigInteger> activityId;
    private Set<Long> dateForDay;
    private Set<Long> dateForWeek;


    public StaffPreferences() {
        //Default Constructor
    }

    public Set<BigInteger> getActivityId() {
        return activityId =Optional.ofNullable(activityId).orElse(new HashSet<>());

    }

    public void setActivityId(Set<BigInteger> activityId) {
        this.activityId = activityId;
    }

    public Set<Long> getDateForDay() {
        return dateForDay=Optional.ofNullable(dateForDay).orElse(new HashSet<>());
    }

    public void setDateForDay(Set<Long> dateForDay) {
        this.dateForDay = dateForDay;
    }

    public Set<Long> getDateForWeek() {
        return dateForWeek=Optional.ofNullable(dateForWeek).orElse(new HashSet<>());
    }

    public void setDateForWeek(Set<Long> dateForWeek) {
        this.dateForWeek = dateForWeek;
    }
}
