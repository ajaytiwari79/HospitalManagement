package com.kairos.shiftplanning.domain.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.shift.Shift;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.LocalDate;

import java.util.Collections;
import java.util.List;

/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE3
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaximumConsecutiveWorkingDaysWTATemplate implements ConstraintHandler {

    private List<String> balanceType;//multiple check boxes
    private boolean checkAgainstTimeRules;
    private int daysLimit;//no of days
    private int weight;
    private ScoreLevel level;
    private String templateType;

    public MaximumConsecutiveWorkingDaysWTATemplate(int daysLimit, int weight, ScoreLevel level) {
        this.daysLimit = daysLimit;
        this.weight = weight;
        this.level = level;
    }

    //TODO Test case
    public int getConsecutiveDays(List<LocalDate> localDates) {
        if(localDates.size()<2) return 0;
        Collections.sort(localDates);
        int count = 1;
        int max = 0;
        int l=1;
        while(l<localDates.size()){
            if(localDates.get(l-1).equals(localDates.get(l).minusDays(1))){
                count++;
            }else{
                count=0;
            }
            if(count>max){
                max=count;
            }
            l++;
        }
        return max;
    }

    public int checkConstraints(List<Shift> shifts) {
        int consecutiveDays = getConsecutiveDays(ShiftPlanningUtility.getSortedAndUniqueDates(shifts));
        return consecutiveDays > daysLimit?(consecutiveDays-(int) daysLimit):0;
    }

}
