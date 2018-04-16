package com.kairos.shiftplanning.domain.move.helper;

import com.kairos.shiftplanning.domain.ActivityLineInterval;
import com.kairos.shiftplanning.domain.ShiftRequestPhase;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import org.apache.commons.collections4.CollectionUtils;
import org.optaplanner.core.impl.domain.variable.listener.support.VariableListenerNotifiable;
import org.optaplanner.core.impl.domain.variable.listener.support.VariableListenerSupport;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ActivityLineIntervalChangeMoveHelper {
    private static Logger log= LoggerFactory.getLogger(ActivityLineIntervalChangeMoveHelper.class);

    /**
     *
     * @param scoreDirector
     * @param activityLineInterval gets assigned
     * @param shiftRequestPhase  to be assigned to
     */
    public static void assignActivityIntervalToShift(ScoreDirector<ShiftRequestPhasePlanningSolution> scoreDirector, ActivityLineInterval activityLineInterval,
                                                     ShiftRequestPhase shiftRequestPhase,List<ActivityLineInterval> exActivityLineIntervalsForThisShift,ShiftRequestPhase exActivityLineIntervalShift){
        //checkSanity(scoreDirector);
        //(existingActivityLineIntervals= ShiftPlanningUtility.getOverlappingActivityLineIntervals
        //(shiftRequestPhase.getActivityLineIntervalsList(),activityLineInterval)
        log.debug("**********************STARTS********************");
        if(CollectionUtils.isNotEmpty(exActivityLineIntervalsForThisShift)){
            for(ActivityLineInterval existingActivityLineInterval: exActivityLineIntervalsForThisShift){
                if(Objects.equals(existingActivityLineInterval.getShift(),exActivityLineIntervalShift)){
                    continue;
                }
                scoreDirector.beforeVariableChanged(existingActivityLineInterval, "shift");
                log.debug("X {} {{}} -> {{}}",existingActivityLineInterval,existingActivityLineInterval.getShift(),exActivityLineIntervalShift);
                existingActivityLineInterval.setShift(exActivityLineIntervalShift);
                scoreDirector.afterVariableChanged(existingActivityLineInterval, "shift");
            }
        }
        if(Objects.equals(activityLineInterval.getShift(),shiftRequestPhase)){
            return;
        }
        scoreDirector.beforeVariableChanged(activityLineInterval, "shift");
        log.debug("{} {{}} -> {{}}",activityLineInterval,activityLineInterval.getShift(),shiftRequestPhase);
        activityLineInterval.setShift(shiftRequestPhase);
        scoreDirector.afterVariableChanged(activityLineInterval, "shift");
        log.debug("**********************END********************");
        /*if(shiftRequestPhase!=null && shiftRequestPhase.getActivityLineIntervalsList().size()==1 && shiftRequestPhase.getInterval().toDuration().getStandardMinutes()!=15l){
            log.info("+++++++++++++++++++++++++"+ShiftPlanningUtility.getIntervalAsString(shiftRequestPhase.getInterval()));
        }*/
        boolean b=false;
        if(b) {
            try {
                Field variableListenerSupport = ((DroolsScoreDirector) scoreDirector).getClass().getSuperclass().getDeclaredField("variableListenerSupport");
                variableListenerSupport.setAccessible(true);
                VariableListenerSupport vls = (VariableListenerSupport) variableListenerSupport.get(scoreDirector);
                Field notifiableList = vls.getClass().getDeclaredField("notifiableList");
                notifiableList.setAccessible(true);
                List<VariableListenerNotifiable> list = (List<VariableListenerNotifiable>) notifiableList.get(vls);
                for (VariableListenerNotifiable vn : list) {
                    log.info(vn.getVariableListener() + "--" + vn.getNotificationQueue().size());
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        //IRSV is triggered in beforeVariableChanged() so if exiting shift is null IRSV wont be triggered because shift would be null for it. it wont know what collection to add to
        //now, there is no need to worry as after the move is applied it'll trigger all listeners and then IRSV would also be trigger
        //but it IRSV would be triggered then then wwhy not here with scoreDirector.afterVariableChanged(activityLineInterval, "shift");??????????

    }

    public static void assignActivityIntervalToShift(ScoreDirector<ShiftRequestPhasePlanningSolution> scoreDirector, ActivityLineInterval activityLineInterval,
                                                     ShiftRequestPhase shiftRequestPhase,ActivityLineInterval activityLineInterval2,ShiftRequestPhase shiftRequestPhase2){
        //checkSanity(scoreDirector);
        scoreDirector.beforeVariableChanged(activityLineInterval2, "shift");
        log.debug("{} {{}} -> {{}}",activityLineInterval2,activityLineInterval2.getShift(),shiftRequestPhase2);
        activityLineInterval2.setShift(shiftRequestPhase2);

        scoreDirector.afterVariableChanged(activityLineInterval2, "shift");


        /*if(Objects.equals(activityLineInterval.getShift(),shiftRequestPhase)){
            return;
        }*/
        if(Objects.equals(activityLineInterval.getShift(),shiftRequestPhase)){
            log.info("HOW");
        }
        scoreDirector.beforeVariableChanged(activityLineInterval, "shift");
        log.debug("{} {{}} -> {{}}",activityLineInterval,activityLineInterval.getShift(),shiftRequestPhase);
        activityLineInterval.setShift(shiftRequestPhase);
        scoreDirector.afterVariableChanged(activityLineInterval, "shift");
        log.debug("**********************END********************");
    }
    public static void assignActivityIntervalToShift(ScoreDirector<ShiftRequestPhasePlanningSolution> scoreDirector, ActivityLineInterval activityLineInterval,
                                                     ShiftRequestPhase shiftRequestPhase) {
        //(existingActivityLineIntervals= ShiftPlanningUtility.getOverlappingActivityLineIntervals
        //(shiftRequestPhase.getActivityLineIntervalsList(),activityLineInterval)
        //log.info("*P*********************STARTS********************");
        //checkSanity(scoreDirector);
        if(Objects.equals(activityLineInterval.getShift(),shiftRequestPhase)){
            return;
        }
        scoreDirector.beforeVariableChanged(activityLineInterval, "shift");
        log.debug("{} {{}} -> {{}}", activityLineInterval,activityLineInterval.getShift(), shiftRequestPhase);
        activityLineInterval.setShift(shiftRequestPhase);
        scoreDirector.afterVariableChanged(activityLineInterval, "shift");
        //log.info("*P*********************END********************");
    }
    public static void checkSanity(ScoreDirector<ShiftRequestPhasePlanningSolution> scoreDirector){
        scoreDirector.getWorkingSolution().getShifts().forEach(s->{
            if(new HashSet<>(s.getActivityLineIntervals().stream().map(a->a.getStart()).collect(Collectors.toList())).size()!=s.getActivityLineIntervals().size()){
                log.info(s.toString());
                s.getActivityLineIntervals().stream().sorted().forEach(a->{
                    log.info(a.getLabel());
                });
                log.info("messed up");
                throw new IllegalStateException();
            }
        });
    }
}
