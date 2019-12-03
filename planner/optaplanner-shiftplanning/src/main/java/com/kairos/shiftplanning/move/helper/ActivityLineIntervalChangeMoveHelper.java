package com.kairos.shiftplanning.move.helper;

import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import org.apache.commons.collections4.CollectionUtils;
import org.optaplanner.core.impl.domain.variable.listener.support.VariableListenerNotifiable;
import org.optaplanner.core.impl.domain.variable.listener.support.VariableListenerSupport;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ActivityLineIntervalChangeMoveHelper {
    public static final String SHIFT = "shift";
    public static final String FORMAT = "{} {{}} -> {{}}";
    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityLineIntervalChangeMoveHelper.class);

    /**
     *
     * @param scoreDirector
     * @param activityLineInterval gets assigned
     * @param shiftImp  to be assigned to
     */
    public static void assignActivityIntervalToShift(ScoreDirector<ShiftRequestPhasePlanningSolution> scoreDirector, ActivityLineInterval activityLineInterval,
                                                     ShiftImp shiftImp, List<ActivityLineInterval> exActivityLineIntervalsForThisShift, ShiftImp exActivityLineIntervalShift){
        //checkSanity(scoreDirector);
        //(existingActivityLineIntervals= ShiftPlanningUtility.getOverlappingActivityLineIntervals
        //(shiftImp.getActivityLineIntervalsList(),activityLineInterval)
        LOGGER.debug("**********************STARTS********************");
        if(CollectionUtils.isNotEmpty(exActivityLineIntervalsForThisShift)){
            for(ActivityLineInterval existingActivityLineInterval: exActivityLineIntervalsForThisShift){
                if(Objects.equals(existingActivityLineInterval.getShift(),exActivityLineIntervalShift)){
                    continue;
                }
                scoreDirector.beforeVariableChanged(existingActivityLineInterval, SHIFT);
                LOGGER.debug("X {} {{}} -> {{}}",existingActivityLineInterval,existingActivityLineInterval.getShift(),exActivityLineIntervalShift);
                existingActivityLineInterval.setShift(exActivityLineIntervalShift);
                scoreDirector.afterVariableChanged(existingActivityLineInterval, SHIFT);
            }
        }
        if(Objects.equals(activityLineInterval.getShift(), shiftImp)){
            return;
        }
        scoreDirector.beforeVariableChanged(activityLineInterval, SHIFT);
        LOGGER.debug(FORMAT,activityLineInterval,activityLineInterval.getShift(), shiftImp);
        activityLineInterval.setShift(shiftImp);
        scoreDirector.afterVariableChanged(activityLineInterval, SHIFT);
        LOGGER.debug("**********************END********************");
        /*if(shiftImp!=null && shiftImp.getActivityLineIntervalsList().size()==1 && shiftImp.getInterval().toDuration().getStandardMinutes()!=15l){
            log.info("+++++++++++++++++++++++++"+ShiftPlanningUtility.getIntervalAsString(shiftImp.getInterval()));
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
                    LOGGER.info(vn.getVariableListener() + "--" + vn.getNotificationQueue().size());
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                LOGGER.error(e.getMessage());
            }
        }
        //IRSV is triggered in beforeVariableChanged() so if exiting shift is null IRSV wont be triggered because shift would be null for it. it wont know what collection to add to
        //now, there is no need to worry as after the move is applied it'll trigger all listeners and then IRSV would also be trigger
        //but it IRSV would be triggered then then wwhy not here with scoreDirector.afterVariableChanged(activityLineInterval, "shift");??????????

    }

    public static void assignActivityIntervalToShift(ScoreDirector<ShiftRequestPhasePlanningSolution> scoreDirector, ActivityLineInterval activityLineInterval,
                                                     ShiftImp shiftImp, ActivityLineInterval activityLineInterval2, ShiftImp shiftImp2){
        //checkSanity(scoreDirector);
        scoreDirector.beforeVariableChanged(activityLineInterval2, SHIFT);
        LOGGER.debug(FORMAT,activityLineInterval2,activityLineInterval2.getShift(), shiftImp2);
        activityLineInterval2.setShift(shiftImp2);

        scoreDirector.afterVariableChanged(activityLineInterval2, SHIFT);


        /*if(Objects.equals(activityLineInterval.getShift(),shiftImp)){
            return;
        }*/
        if(Objects.equals(activityLineInterval.getShift(), shiftImp)){
            LOGGER.info("HOW");
        }
        scoreDirector.beforeVariableChanged(activityLineInterval, SHIFT);
        LOGGER.debug(FORMAT,activityLineInterval,activityLineInterval.getShift(), shiftImp);
        activityLineInterval.setShift(shiftImp);
        scoreDirector.afterVariableChanged(activityLineInterval, SHIFT);
        LOGGER.debug("**********************END********************");
    }
    public static void assignActivityIntervalToShift(ScoreDirector<ShiftRequestPhasePlanningSolution> scoreDirector, ActivityLineInterval activityLineInterval,
                                                     ShiftImp shiftImp) {
        //(existingActivityLineIntervals= ShiftPlanningUtility.getOverlappingActivityLineIntervals
        //(shiftImp.getActivityLineIntervalsList(),activityLineInterval)
        //log.info("*P*********************STARTS********************");
        //checkSanity(scoreDirector);
        if(Objects.equals(activityLineInterval.getShift(), shiftImp)){
            return;
        }
        scoreDirector.beforeVariableChanged(activityLineInterval, SHIFT);
        LOGGER.debug(FORMAT, activityLineInterval,activityLineInterval.getShift(), shiftImp);
        activityLineInterval.setShift(shiftImp);
        scoreDirector.afterVariableChanged(activityLineInterval, SHIFT);
        //log.info("*P*********************END********************");
    }
    public static void checkSanity(ScoreDirector<ShiftRequestPhasePlanningSolution> scoreDirector){
        scoreDirector.getWorkingSolution().getShifts().forEach(s->{
            if(new HashSet<>(s.getActivityLineIntervals().stream().map(ActivityLineInterval::getStart).collect(Collectors.toList())).size()!=s.getActivityLineIntervals().size()){
                LOGGER.info(s.toString());
                s.getActivityLineIntervals().stream().sorted().forEach(a-> LOGGER.info(a.getLabel()));
                LOGGER.info("messed up");
                throw new IllegalStateException();
            }
        });
    }
}
