package com.kairos.shiftplanning.move;

import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.move.helper.ActivityLineIntervalWrapper;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveIteratorFactory;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates pillar for a random date with all activities/shifts for that day.
 */
public class ActivityLineIntervalPillarMoveIteratorFactory implements MoveIteratorFactory<ShiftRequestPhasePlanningSolution> {
    private static Logger log= LoggerFactory.getLogger(ActivityLineIntervalPillarMoveIteratorFactory.class);

    @Override
    public long getSize(ScoreDirector<ShiftRequestPhasePlanningSolution> scoreDirector) {
        ShiftRequestPhasePlanningSolution solution = scoreDirector.getWorkingSolution();
        return (long) solution.getWeekDates().size() * solution.getActivities().size();
    }
    @Override
    public Iterator<? extends Move<ShiftRequestPhasePlanningSolution>> createOriginalMoveIterator(ScoreDirector<ShiftRequestPhasePlanningSolution> scoreDirector) {
        return (Iterator<? extends Move<ShiftRequestPhasePlanningSolution>>) new UnsupportedOperationException();
    }

    /**
     * It generates moves for a random day.
     * @param scoreDirector
     * @param workingRandom
     * @return
     */
    @Override
    public ActivityLineIntervalPillarMoveIterator<? extends Move<ShiftRequestPhasePlanningSolution>> createRandomMoveIterator(ScoreDirector<ShiftRequestPhasePlanningSolution> scoreDirector, Random workingRandom) {
        ShiftRequestPhasePlanningSolution solution = scoreDirector.getWorkingSolution();
        List<List<ActivityLineIntervalWrapper>> possibleActivityLineIntervalWrappersList= new ArrayList<>();
        LocalDate date=solution.getWeekDates().get(workingRandom.nextInt(solution.getWeekDates().size()));
        List<ShiftImp> shifts= solution.getShifts().stream().filter(s->s.getDate().equals(date)).collect(Collectors.toList());
        Map<String,List<ActivityLineInterval>> groupedAlis= solution.getActivitiesIntervalsGroupedPerDay();
        List<List<ActivityLineInterval>> activityLineIntervalsPerDay= new ArrayList<>();
        for(Activity activity :solution.getActivitiesPerDay().get(date)){
            for(Map.Entry<String,List<ActivityLineInterval>> entry:groupedAlis.entrySet()){
                if(!entry.getKey().startsWith(date.toString("MM/dd/yyyy")+"_"+ activity.getId())) continue;
                List<ActivityLineInterval> alis=entry.getValue();
                activityLineIntervalsPerDay.add(alis);
            }
        }
        //TODO we need to merge multiple activitities here to make them a move but they could have different shifts but it's fine.
        mergeMultipleActivityLinesForSingleShift(activityLineIntervalsPerDay,workingRandom);
        log.debug("potential number of moves in pillar:{}",activityLineIntervalsPerDay.size());
        for(ShiftImp shift:shifts){
            for(List<ActivityLineInterval> alis: activityLineIntervalsPerDay){
                int[] randomRange= ShiftPlanningUtility.getRandomRange(alis.size(),workingRandom);
                alis=alis.subList(randomRange[0],randomRange[1]);
                List<ActivityLineIntervalWrapper> activityLineIntervalWrappers=ShiftPlanningUtility.toActivityWrapper(alis,shift);
                if(activityLineIntervalWrappers.isEmpty())
                    continue;
                possibleActivityLineIntervalWrappersList.add(activityLineIntervalWrappers);
            }
        }
        Collections.shuffle(possibleActivityLineIntervalWrappersList);
        return new ActivityLineIntervalPillarMoveIterator(possibleActivityLineIntervalWrappersList,workingRandom);
    }

    private void mergeMultipleActivityLinesForSingleShift(List<List<ActivityLineInterval>> activityLineIntervalsPerDay,Random workingRandom) {
        List<List<ActivityLineInterval>> mergedActivityLines=new ArrayList<>();
        for(List<ActivityLineInterval> activityLineIntervalsOuter:activityLineIntervalsPerDay){
            if(activityLineIntervalsOuter.get(0).getActivity().isTypeAbsence())continue;
            int outerPartitionIndex=workingRandom.nextInt(activityLineIntervalsOuter.size());
            DateTime outersPartitionTime=activityLineIntervalsOuter.get(outerPartitionIndex).getStart();
            for(List<ActivityLineInterval> activityLineIntervalsInner:activityLineIntervalsPerDay){
                updateActivityLineInterval(mergedActivityLines, activityLineIntervalsOuter, outerPartitionIndex, outersPartitionTime, activityLineIntervalsInner);
            }
        }
        activityLineIntervalsPerDay.addAll(mergedActivityLines);
    }

    private void updateActivityLineInterval(List<List<ActivityLineInterval>> mergedActivityLines, List<ActivityLineInterval> activityLineIntervalsOuter, int outerPartitionIndex, DateTime outersPartitionTime, List<ActivityLineInterval> activityLineIntervalsInner) {
        if(activityLineIntervalsInner.get(0).getActivity().isTypeAbsence()) return;
        if(activityLineIntervalsInner==activityLineIntervalsOuter ||
                activityLineIntervalsInner.get(0).getActivity().getId().equals(activityLineIntervalsOuter.get(0).getActivity().getId()))
            return;
        if(overlaps(activityLineIntervalsOuter,activityLineIntervalsInner)){
            List<ActivityLineInterval> mergedAlis=new ArrayList<>();
            int innerPartitionStart=-1;
            for(int i=0;i<activityLineIntervalsInner.size();i++){
                ActivityLineInterval ali=activityLineIntervalsInner.get(i);
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

    private boolean overlaps(List<ActivityLineInterval> activityLineIntervalsOuter, List<ActivityLineInterval> activityLineIntervalsInner) {
        return getInterval(activityLineIntervalsInner).overlaps(getInterval(activityLineIntervalsOuter));
    }

    private Interval getInterval(List<ActivityLineInterval> activityLineIntervalsOuter) {
        return new Interval(activityLineIntervalsOuter.get(0).getStart(),activityLineIntervalsOuter.get(activityLineIntervalsOuter.size()-1).getEnd());
    }
}
