package com.kairos.shiftplanning.domain.activityConstraint;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.enums.Day;
import com.kairos.shiftplanning.domain.Shift;
import com.kairos.shiftplanning.domain.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.wta.ConstraintHandler;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import org.joda.time.DateTime;


import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.shiftplanning.utils.ShiftPlanningUtility.isValidForDayType;

/**
 * @author pradeep
 * @date - 18/12/18
 */

public class ActivityDayType implements ConstraintHandler {

    private List<DayType> dayTypes;
    private ScoreLevel level;
    private int weight;


    public ActivityDayType(List<DayType> dayTypes, ScoreLevel level, int weight) {
        this.dayTypes = dayTypes;
        this.level = level;
        this.weight = weight;
    }

    public ActivityDayType() {
    }

    public List<DayType> getDayTypes() {
        return dayTypes;
    }

    public void setDayTypes(List<DayType> dayTypes) {
        this.dayTypes = dayTypes;
    }

    public ScoreLevel getLevel() {
        return level;
    }

    public void setLevel(ScoreLevel level) {
        this.level = level;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }


    public int checkConstraints(Shift shift){
        return isValidForDayType(shift,this.dayTypes) ? 0 : 1;
    }





}
