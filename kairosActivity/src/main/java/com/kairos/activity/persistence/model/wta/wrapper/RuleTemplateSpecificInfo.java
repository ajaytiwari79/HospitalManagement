package com.kairos.activity.persistence.model.wta.wrapper;

import com.kairos.activity.client.dto.TimeSlotWrapper;
import com.kairos.activity.persistence.model.period.PlanningPeriod;
import com.kairos.activity.persistence.model.phase.Phase;
import com.kairos.activity.persistence.model.wta.StaffWTACounter;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;
import com.kairos.activity.util.DateTimeInterval;
import com.kairos.response.dto.web.cta.DayTypeDTO;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * @author pradeep
 * @date - 23/5/18
 */

public class RuleTemplateSpecificInfo {

    private List<ShiftQueryResultWithActivity> shifts;
    private ShiftQueryResultWithActivity shift;
    private List<TimeSlotWrapper> timeSlotWrappers;
    private String phase;
    private DateTimeInterval planningPeriod;
    private Map<BigInteger,Integer> counterMap;
    private List<DayTypeDTO> dayTypes;
    private String userPostion;


    public RuleTemplateSpecificInfo(List<ShiftQueryResultWithActivity> shifts, ShiftQueryResultWithActivity shift, List<TimeSlotWrapper> timeSlotWrappers, String phase, DateTimeInterval planningPeriod,Map<BigInteger,Integer> counterMap,List<DayTypeDTO> dayTypes) {
        this.shifts = shifts;
        this.shift = shift;
        this.timeSlotWrappers = timeSlotWrappers;
        this.phase = phase;
        this.planningPeriod = planningPeriod;
        this.counterMap = counterMap;
        this.dayTypes = dayTypes;
    }

    public String getUserPostion() {
        return userPostion;
    }

    public void setUserPostion(String userPostion) {
        this.userPostion = userPostion;
    }

    public List<DayTypeDTO> getDayTypes() {
        return dayTypes;
    }

    public void setDayTypes(List<DayTypeDTO> dayTypes) {
        this.dayTypes = dayTypes;
    }

    public Map<BigInteger, Integer> getCounterMap() {
        return counterMap;
    }

    public void setCounterMap(Map<BigInteger, Integer> counterMap) {
        this.counterMap = counterMap;
    }

    public List<ShiftQueryResultWithActivity> getShifts() {
        return shifts;
    }

    public void setShifts(List<ShiftQueryResultWithActivity> shifts) {
        this.shifts = shifts;
    }

    public ShiftQueryResultWithActivity getShift() {
        return shift;
    }

    public void setShift(ShiftQueryResultWithActivity shift) {
        this.shift = shift;
    }

    public List<TimeSlotWrapper> getTimeSlotWrappers() {
        return timeSlotWrappers;
    }

    public void setTimeSlotWrappers(List<TimeSlotWrapper> timeSlotWrappers) {
        this.timeSlotWrappers = timeSlotWrappers;
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
