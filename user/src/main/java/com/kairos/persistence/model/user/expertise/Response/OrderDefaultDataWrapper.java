package com.kairos.persistence.model.user.expertise.Response;

import com.kairos.dto.ActivityDTO;
import com.kairos.user.country.FunctionDTO;
import com.kairos.user.country.ReasonCodeResponseDTO;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.user.staff.StaffPersonalDetailDTO;
import com.kairos.activity.open_shift.OrderResponseDTO;
import com.kairos.activity.presence_type.PresenceTypeDTO;

import java.util.List;

public class OrderDefaultDataWrapper {
    private List<OrderResponseDTO> orders;
    private List<ActivityDTO> activities;
    private List<Skill> skills;
    private List<Expertise> expertise;
   // private List<TimeType> timeTypes;
    private List<StaffPersonalDetailDTO> staffList;
    private List<PresenceTypeDTO> plannedTime;
    private List<FunctionDTO> functions;
    private List<ReasonCodeResponseDTO> reasonCodes;
    private List<com.kairos.user.country.DayType> dayTypes;
    private Integer minOpenShiftHours;

    public OrderDefaultDataWrapper() {
        //Default Constructor
    }

    public OrderDefaultDataWrapper(List<OrderResponseDTO> orders, List<ActivityDTO> activities, List<Skill> skills, List<Expertise> expertise, List<StaffPersonalDetailDTO> staffList,
                                   List<PresenceTypeDTO> plannedTime, List<FunctionDTO> functions, List<ReasonCodeResponseDTO> reasonCodes, List<com.kairos.user.country.DayType> dayTypes, Integer minOpenShiftHours) {
        this.orders = orders;
        this.activities = activities;
        this.skills = skills;
        this.expertise = expertise;
       // this.timeTypes = timeTypes;
        this.staffList = staffList;
        this.plannedTime = plannedTime;
        this.functions = functions;
        this.reasonCodes = reasonCodes;
        this.dayTypes = dayTypes;
        this.minOpenShiftHours=minOpenShiftHours;
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

  /*  public List<TimeType> getTimeTypes() {
        return timeTypes;
    }

    public void setTimeTypes(List<TimeType> timeTypes) {
        this.timeTypes = timeTypes;
    }*/

    public List<StaffPersonalDetailDTO> getStaffList() {
        return staffList;
    }

    public void setStaffList(List<StaffPersonalDetailDTO> staffList) {
        this.staffList = staffList;
    }

    public List<PresenceTypeDTO> getPlannedTime() {
        return plannedTime;
    }

    public void setPlannedTime(List<PresenceTypeDTO> plannedTime) {
        this.plannedTime = plannedTime;
    }

    public List<FunctionDTO> getFunctions() {
        return functions;
    }

    public void setFunctions(List<FunctionDTO> functions) {
        this.functions = functions;
    }

    public List<ReasonCodeResponseDTO> getReasonCodes() {
        return reasonCodes;
    }

    public void setReasonCodes(List<ReasonCodeResponseDTO> reasonCodes) {
        this.reasonCodes = reasonCodes;
    }

    public List<com.kairos.user.country.DayType> getDayTypes() {
        return dayTypes;
    }

    public void setDayTypes(List<com.kairos.user.country.DayType> dayTypes) {
        this.dayTypes = dayTypes;
    }

    public Integer getMinOpenShiftHours() {
        return minOpenShiftHours;
    }

    public void setMinOpenShiftHours(Integer minOpenShiftHours) {
        this.minOpenShiftHours = minOpenShiftHours;
    }
}
