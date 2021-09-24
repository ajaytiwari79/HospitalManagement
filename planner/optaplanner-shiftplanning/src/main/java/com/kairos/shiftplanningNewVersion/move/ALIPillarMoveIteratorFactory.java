package com.kairos.shiftplanningNewVersion.move;

import com.kairos.commons.utils.DateTimeInterval;


import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import com.kairos.shiftplanningNewVersion.entity.ALI;
import com.kairos.shiftplanningNewVersion.entity.Shift;
import com.kairos.shiftplanningNewVersion.move.helper.ALIWrapper;
import com.kairos.shiftplanningNewVersion.solution.StaffingLevelSolution;
import com.kairos.shiftplanningNewVersion.utils.StaffingLevelPlanningUtility;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveIteratorFactory;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates pillar for a random date with all activities/shifts for that day.
 */
public class ALIPillarMoveIteratorFactory implements MoveIteratorFactory<StaffingLevelSolution> {
    private static Logger log= LoggerFactory.getLogger(ALIPillarMoveIteratorFactory.class);

    @Override
    public long getSize(ScoreDirector<StaffingLevelSolution> scoreDirector) {
        StaffingLevelSolution solution = scoreDirector.getWorkingSolution();
        return (long) solution.getWeekDates().size() * solution.getActivities().size();
    }
    @Override
    public Iterator<? extends Move<StaffingLevelSolution>> createOriginalMoveIterator(ScoreDirector<StaffingLevelSolution> scoreDirector) {
        return (Iterator<? extends Move<StaffingLevelSolution>>) new UnsupportedOperationException();
    }

    /**
     * It generates moves for a random day.
     * @param scoreDirector
     * @param workingRandom
     * @return
     */
    @Override
    public ALIPillarMoveIterator<? extends Move<StaffingLevelSolution>> createRandomMoveIterator(ScoreDirector<StaffingLevelSolution> scoreDirector, Random workingRandom) {
        StaffingLevelSolution solution = scoreDirector.getWorkingSolution();
        List<List<ALIWrapper>> possibleActivityLineIntervalWrappersList= new ArrayList<>();
        LocalDate date=solution.getWeekDates().get(workingRandom.nextInt(solution.getWeekDates().size()));
        List<Shift> shifts= solution.getShifts().stream().filter(s->s.getStartDate().equals(date)).collect(Collectors.toList());
        //Todo pradeep make it for multiple days
        /*Map<String,List<ALI>> groupedAlis= solution.getActivityLineIntervals();
        List<List<ALI>> activityLineIntervalsPerDay= new ArrayList<>();
        for(Activity activity :solution.getActivitiesPerDay().get(date)){
            for(Map.Entry<String,List<ALI>> entry:groupedAlis.entrySet()){
                if(!entry.getKey().startsWith(date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))+"_"+ activity.getId())) continue;
                List<ALI> alis=entry.getValue();
                activityLineIntervalsPerDay.add(alis);
            }
        }*/
        List<List<ALI>> activityLineIntervalsPerDay= new ArrayList<>();
        solution.getAliPerActivities().forEach(alis -> activityLineIntervalsPerDay.add(new ArrayList(alis)));
        //TODO we need to merge multiple activitities here to make them a move but they could have different shifts but it's fine.
        mergeMultipleActivityLinesForSingleShift(activityLineIntervalsPerDay,workingRandom);
        log.debug("potential number of moves in pillar:{}",activityLineIntervalsPerDay.size());
        for(Shift shift:shifts){
            for(List<ALI> alis: activityLineIntervalsPerDay){
                int[] randomRange= ShiftPlanningUtility.getRandomRange(alis.size(),workingRandom);
                alis=alis.subList(randomRange[0],randomRange[1]);
                List<ALIWrapper> ALIWrappers = StaffingLevelPlanningUtility.toActivityWrapper(alis,shift);
                if(ALIWrappers.isEmpty())
                    continue;
                possibleActivityLineIntervalWrappersList.add(ALIWrappers);
            }
        }
        Collections.shuffle(possibleActivityLineIntervalWrappersList);
        return new ALIPillarMoveIterator(possibleActivityLineIntervalWrappersList,workingRandom);
    }

    private void mergeMultipleActivityLinesForSingleShift(List<List<ALI>> activityLineIntervalsPerDay,Random workingRandom) {
        List<List<ALI>> mergedActivityLines=new ArrayList<>();
        for(List<ALI> activityLineIntervalsOuter:activityLineIntervalsPerDay){
            if(activityLineIntervalsOuter.get(0).getActivity().isTypeAbsence())continue;
            int outerPartitionIndex=workingRandom.nextInt(activityLineIntervalsOuter.size());
            ZonedDateTime outersPartitionTime=activityLineIntervalsOuter.get(outerPartitionIndex).getStart();
            for(List<ALI> activityLineIntervalsInner:activityLineIntervalsPerDay){
                updateActivityLineInterval(mergedActivityLines, activityLineIntervalsOuter, outerPartitionIndex, outersPartitionTime, activityLineIntervalsInner);
            }
        }
        activityLineIntervalsPerDay.addAll(mergedActivityLines);
    }

    private void updateActivityLineInterval(List<List<ALI>> mergedActivityLines, List<ALI> activityLineIntervalsOuter, int outerPartitionIndex, ZonedDateTime outersPartitionTime, List<ALI> activityLineIntervalsInner) {
        if(activityLineIntervalsInner.get(0).getActivity().isTypeAbsence()) return;
        if(activityLineIntervalsInner==activityLineIntervalsOuter ||
                activityLineIntervalsInner.get(0).getActivity().getId().equals(activityLineIntervalsOuter.get(0).getActivity().getId()))
            return;
        if(overlaps(activityLineIntervalsOuter,activityLineIntervalsInner)){
            List<ALI> mergedAlis=new ArrayList<>();
            int innerPartitionStart=-1;
            for(int i=0;i<activityLineIntervalsInner.size();i++){
                ALI ali=activityLineIntervalsInner.get(i);
                if(ali.getStart().isEqual(outersPartitionTime)){
                    innerPartitionStart=i;
                    break;
                }
            }
            if(innerPartitionStart<0) return;
            mergedAlis.addAll(activityLineIntervalsOuter.subList(0,outerPartitionIndex));
            mergedAlis.addAll(activityLineIntervalsInner.subList(innerPartitionStart,activityLineIntervalsInner.size()));
            mergedActivityLines.add(mergedAlis);
        }
    }

    private boolean overlaps(List<ALI> activityLineIntervalsOuter, List<ALI> activityLineIntervalsInner) {
        return getInterval(activityLineIntervalsInner).overlaps(getInterval(activityLineIntervalsOuter));
    }

    private DateTimeInterval getInterval(List<ALI> activityLineIntervalsOuter) {
        return new DateTimeInterval(activityLineIntervalsOuter.get(0).getStart(),activityLineIntervalsOuter.get(activityLineIntervalsOuter.size()-1).getEnd());
    }
}
