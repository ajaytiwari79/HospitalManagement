package com.kairos.activity.persistence.model.wta.wrapper;

import com.kairos.activity.persistence.model.wta.templates.template_types.ShortestAndAverageDailyRestWTATemplate;

/**
 * @author pradeep
 * @date - 22/5/18
 */

public class ShortestAndAverageDailyRestWrapper implements RuleTemplateWrapper{


    private ShortestAndAverageDailyRestWTATemplate wtaTemplate;
    private RuleTemplateSpecificInfo infoWrapper;

    @Override
    public String isSatisfied() {
        return "";
    }

  /*  public static int checkConstraints(List<ShiftQueryResultWithActivity> shifts,ShortestAndAverageDailyRestWTATemplate ruleTemplate){
        if(shifts.size()<2) return 0;
        List<DateTimeInterval> intervals= getSortedIntervals(shifts);
        int restingTimeUnder=0;
        int totalRestAllShifts=0;
        for(int i=1;i<intervals.size();i++){
            ZonedDateTime lastEnd=intervals.get(i-1).getEnd();
            ZonedDateTime thisStart=intervals.get(i).getStart();
            long totalRest=(thisStart.getMillisOfDay()-lastEnd.toInstant().toEpochMilli())/60000;
            restingTimeUnder=(int)(continuousDayRestingTime >totalRest? continuousDayRestingTime -totalRest:0);
            totalRestAllShifts+=totalRest;
        }
        float averageRestingTime=totalRestAllShifts/shifts.size();
        return  (restingTimeUnder + (int)(averageRest>averageRestingTime?averageRest-averageRestingTime:0));
    }*/

    public ShortestAndAverageDailyRestWrapper(ShortestAndAverageDailyRestWTATemplate wtaTemplate, RuleTemplateSpecificInfo infoWrapper) {
        this.wtaTemplate = wtaTemplate;
        this.infoWrapper = infoWrapper;
    }
}
