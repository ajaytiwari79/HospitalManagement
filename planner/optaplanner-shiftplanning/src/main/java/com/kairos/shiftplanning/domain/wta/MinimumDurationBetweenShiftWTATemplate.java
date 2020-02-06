package com.kairos.shiftplanning.domain.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.shift.Shift;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE16
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MinimumDurationBetweenShiftWTATemplate implements ConstraintHandler {

    private List<String> balanceType;
    private long minimumDurationBetweenShifts;
    private int weight;
    private ScoreLevel level;
    private String templateType;


    public MinimumDurationBetweenShiftWTATemplate(long minimumDurationBetweenShifts, int weight, ScoreLevel level) {
        this.minimumDurationBetweenShifts = minimumDurationBetweenShifts;
        this.weight = weight;
        this.level = level;
    }

    public boolean checkConsTraints(List<Shift> shifts, Shift shift) {
        boolean isValid = false;
        int timefromPrevShift = 0;
        shifts = (List<Shift>) shifts.stream().filter(shift1 -> shift1.getStart() != null && shift1.getEnd() != null).filter(shift1 -> shift1.getEnd().isBefore(shift.getStart())).sorted(ShiftPlanningUtility.getShiftStartTimeComparator()).collect(Collectors.toList());
        if (shifts.size() > 0) {
            DateTime prevShiftEnd = shifts.size() > 1 ? shifts.get(shifts.size() - 1).getEnd() : shifts.get(0).getEnd();
            timefromPrevShift = new Period(prevShiftEnd, shift.getStart()).getMinutes();
            if(timefromPrevShift==0 && shift.getStart().getDayOfWeek()==1){
                timefromPrevShift = new Period(shift.getEmployee().getPrevShiftEnd(), shift.getStart()).getMinutes();
            }
        }
        if (timefromPrevShift < minimumDurationBetweenShifts) {
            isValid = true;
        }
        return isValid;
    }

}