package com.kairos.shiftplanning.domain;

import org.apache.commons.lang3.builder.CompareToBuilder;

import java.util.Comparator;

public class ActivityLineIntervalComparator implements Comparator<ActivityLineInterval>  {

    @Override
    public int compare(ActivityLineInterval a1, ActivityLineInterval a2) {
        return new CompareToBuilder()
                .append(a1.getActivityPlannerEntity().getId(), a2.getActivityPlannerEntity().getId())
                .append(a1.getStaffNo(), a2.getStaffNo())
                .append(a1.getStart(), a2.getStart())
                .toComparison();
    }
}
