package com.kairos.wrapper.wta;

import com.kairos.dto.activity.shift.ViolatedRulesDTO;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotWrapper;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author pradeep
 * @date - 23/5/18
 */

public class RuleTemplateSpecificInfo {

    private List<ShiftWithActivityDTO> shifts = new ArrayList<>();
    private ShiftWithActivityDTO shift;
    private Map<String,TimeSlotWrapper> timeSlotWrapperMap;
    private String phase;
    private DateTimeInterval planningPeriod;
    private Map<String,Integer> counterMap;
    private Map<Long, DayTypeDTO> dayTypeMap;
    private UserAccessRoleDTO user;
    private int totalTimeBank;
    private ViolatedRulesDTO violatedRules;
    private int staffAge;
    private Map<BigInteger,ActivityWrapper> activityWrapperMap;


    public RuleTemplateSpecificInfo(List<ShiftWithActivityDTO> shifts, ShiftWithActivityDTO shift, Map<String,TimeSlotWrapper> timeSlotWrapperMap, String phase, DateTimeInterval planningPeriod, Map<String,Integer> counterMap, Map<Long, DayTypeDTO> dayTypeMap, UserAccessRoleDTO user, int totalTimeBank,Map<BigInteger, ActivityWrapper> activityWrapperMap,int staffAge) {
        this.shifts = shifts;
        this.shift = shift;
        this.timeSlotWrapperMap = timeSlotWrapperMap;
        this.phase = phase;
        this.planningPeriod = planningPeriod;
        this.counterMap = counterMap;
        this.dayTypeMap = dayTypeMap;
        this.user = user;
        this.totalTimeBank = totalTimeBank;
        this.violatedRules = new ViolatedRulesDTO();
        this.activityWrapperMap = activityWrapperMap;
        this.staffAge = staffAge;
    }

    public int getStaffAge() {
        return staffAge;
    }

    public void setStaffAge(int staffAge) {
        this.staffAge = staffAge;
    }

    public Map<BigInteger, ActivityWrapper> getActivityWrapperMap() {
        return activityWrapperMap;
    }

    public void setActivityWrapperMap(Map<BigInteger, ActivityWrapper> activityWrapperMap) {
        this.activityWrapperMap = activityWrapperMap;
    }

    public ViolatedRulesDTO getViolatedRules() {
        return violatedRules;
    }

    public void setViolatedRules(ViolatedRulesDTO violatedRules) {
        this.violatedRules = violatedRules;
    }

    public int getTotalTimeBank() {
        return totalTimeBank;
    }

    public void setTotalTimeBank(int totalTimeBank) {
        this.totalTimeBank = totalTimeBank;
    }

    public UserAccessRoleDTO getUser() {
        return user;
    }

    public void setUser(UserAccessRoleDTO user) {
        this.user = user;
    }

    public Map<Long, DayTypeDTO> getDayTypeMap() {
        return dayTypeMap;
    }

    public void setDayTypeMap(Map<Long, DayTypeDTO> dayTypeMap) {
        this.dayTypeMap = dayTypeMap;
    }

    public Map<String, Integer> getCounterMap() {
        return counterMap;
    }

    public void setCounterMap(Map<String, Integer> counterMap) {
        this.counterMap = counterMap;
    }

    public List<ShiftWithActivityDTO> getShifts() {
        return shifts;
    }

    public void setShifts(List<ShiftWithActivityDTO> shifts) {
        this.shifts = shifts;
    }

    public ShiftWithActivityDTO getShift() {
        return shift;
    }

    public void setShift(ShiftWithActivityDTO shift) {
        this.shift = shift;
    }

    public Map<String, TimeSlotWrapper> getTimeSlotWrapperMap() {
        return timeSlotWrapperMap;
    }

    public void setTimeSlotWrapperMap(Map<String, TimeSlotWrapper> timeSlotWrapperMap) {
        this.timeSlotWrapperMap = timeSlotWrapperMap;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public DateTimeInterval getPlanningPeriod() {
        return planningPeriod;
    }

    public void setPlanningPeriod(DateTimeInterval planningPeriod) {
        this.planningPeriod = planningPeriod;
    }
}
