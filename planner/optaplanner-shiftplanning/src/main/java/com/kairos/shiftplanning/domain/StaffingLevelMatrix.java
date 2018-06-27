package com.kairos.shiftplanning.domain;

import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
@XStreamAlias("StaffingLevelMatrix")
public class StaffingLevelMatrix {
    public StaffingLevelMatrix() {
    }

    private static Logger log = LoggerFactory.getLogger(StaffingLevelMatrix.class);

    private Map<LocalDate, Object[]> staffingLevelMatrix;
    private int[] activitiesRank;
    public StaffingLevelMatrix(Map<LocalDate, Object[]> staffingLevelMatrix, int[] activitiesRank) {
        this.staffingLevelMatrix = staffingLevelMatrix;
        this.activitiesRank = activitiesRank;
    }
    public Map<LocalDate, Object[]> getStaffingLevelMatrix() {
        return staffingLevelMatrix;
    }
    public int[] getMissingMinAndMax(List<ShiftRequestPhase> shifts){
        long start=System.currentTimeMillis();
        Map<LocalDate, Object[]> reducedStaffingLevelMatrix =ShiftPlanningUtility.reduceStaffingLevelMatrix(staffingLevelMatrix,shifts,null,null,15);

        int[] minMax=ShiftPlanningUtility.getTotalMissingMinAndMaxStaffingLevels(reducedStaffingLevelMatrix,activitiesRank);
        if(log.isDebugEnabled())
            log.debug("SL matrix deduction took:"+(System.currentTimeMillis()-start)/1000.0);

        return minMax;
    }
    public int[] getMissingMinAndMax(List<ShiftRequestPhase> shifts,List<ShiftBreak> shiftBreaks,List<IndirectActivity> indirectActivities){
        long start=System.currentTimeMillis();
        Map<LocalDate, Object[]> reducedStaffingLevelMatrix =ShiftPlanningUtility.reduceStaffingLevelMatrix(staffingLevelMatrix,shifts,shiftBreaks,indirectActivities,15);

        int[] minMax=ShiftPlanningUtility.getTotalMissingMinAndMaxStaffingLevels(reducedStaffingLevelMatrix,activitiesRank);
        if(log.isDebugEnabled())
            log.debug("SL matrix deduction took:"+(System.currentTimeMillis()-start)/1000.0);

        return minMax;
    }
    @Deprecated
    public int[] getMissingMinAndMaxFromALIs(List<ActivityLineInterval> alis){
        long start=System.currentTimeMillis();
        //Map<LocalDate, Object[]> reducedStaffingLevelMatrix =ShiftPlanningUtility.reduceStaffingLevelMatrix(staffingLevelMatrix,shifts,15);
        Map<LocalDate, Object[]> reducedStaffingLevelMatrix =ShiftPlanningUtility.reduceALIsFromStaffingLevelMatrix(staffingLevelMatrix,alis,15);

        int[] minMax=ShiftPlanningUtility.getTotalMissingMinAndMaxStaffingLevels(reducedStaffingLevelMatrix,activitiesRank);
        if(log.isDebugEnabled())
            log.debug("SL matrix deduction took:"+(System.currentTimeMillis()-start)/1000.0);

        return minMax;
    }

    public int[] getActivitiesRank() {
        return activitiesRank;
    }
}
