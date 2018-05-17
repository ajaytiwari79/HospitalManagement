package com.kairos.shiftplanning.domain;

import org.apache.commons.lang3.builder.CompareToBuilder;

import java.util.Comparator;

public class EmployeeStrengthComparator implements  Comparator<EmployeePlanningFact> {
    @Override
    public int compare(EmployeePlanningFact a, EmployeePlanningFact b) {
        return new CompareToBuilder()
                .append(a.getId(), b.getId())
                .toComparison();
    }
}
