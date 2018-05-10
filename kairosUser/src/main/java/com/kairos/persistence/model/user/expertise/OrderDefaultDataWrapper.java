package com.kairos.persistence.model.user.expertise;

import com.kairos.activity.response.dto.ActivityDTO;
import com.kairos.persistence.model.organization.DayType;
import com.kairos.persistence.model.user.agreement.cta.PlannedTimeWithFactor;
import com.kairos.persistence.model.user.country.Function;
import com.kairos.persistence.model.user.country.ReasonCode;
import com.kairos.persistence.model.user.country.TimeType;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.model.user.staff.Staff;
import com.kairos.response.dto.web.open_shift.OrderResponseDTO;

import java.util.List;

public class OrderDefaultDataWrapper {
    private List<OrderResponseDTO> orders;
    private List<ActivityDTO> activities;
    private List<Skill> skills;
    private List<Expertise> expertise;
    private List<TimeType> timeTypes;
    private List<Staff> staffList;
    private List<PlannedTimeWithFactor> plannedTimeWithFactors;
    private List<Function> functions;
    private List<ReasonCode> reasonCodes;
    private List<DayType> dayTypes;

    public OrderDefaultDataWrapper() {
        //Default Constructor
    }

    public List<OrderResponseDTO> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderResponseDTO> orders) {
        this.orders = orders;
    }

    public List<ActivityDTO> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityDTO> activities) {
        this.activities = activities;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    public List<Expertise> getExpertise() {
        return expertise;
    }

    public void setExpertise(List<Expertise> expertise) {
        this.expertise = expertise;
    }

    public List<TimeType> getTimeTypes() {
        return timeTypes;
    }

    public void setTimeTypes(List<TimeType> timeTypes) {
        this.timeTypes = timeTypes;
    }

    public List<Staff> getStaffList() {
        return staffList;
    }

    public void setStaffList(List<Staff> staffList) {
        this.staffList = staffList;
    }

    public List<PlannedTimeWithFactor> getPlannedTimeWithFactors() {
        return plannedTimeWithFactors;
    }

    public void setPlannedTimeWithFactors(List<PlannedTimeWithFactor> plannedTimeWithFactors) {
        this.plannedTimeWithFactors = plannedTimeWithFactors;
    }

    public List<Function> getFunctions() {
        return functions;
    }

    public void setFunctions(List<Function> functions) {
        this.functions = functions;
    }

    public List<ReasonCode> getReasonCodes() {
        return reasonCodes;
    }

    public void setReasonCodes(List<ReasonCode> reasonCodes) {
        this.reasonCodes = reasonCodes;
    }

    public List<DayType> getDayTypes() {
        return dayTypes;
    }

    public void setDayTypes(List<DayType> dayTypes) {
        this.dayTypes = dayTypes;
    }
}
