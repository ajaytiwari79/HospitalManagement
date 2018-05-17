package com.kairos.shiftplanning.domain;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

public class EmployeeDifficultyComparator implements  Comparator<EmployeePlanningFact> {
    private static Logger log= LoggerFactory.getLogger(EmployeeDifficultyComparator.class);

    @Override
    public int compare(EmployeePlanningFact o1, EmployeePlanningFact o2) {

        //return o1.getAvailableMinutes().compareTo(o2.getAvailableMinutes());
        //return o1.getAvailableMinutesAfterPlanning().compareTo(o2.getAvailableMinutesAfterPlanning());
    	//return o1.getId().compareTo(o2.getId());
        //log.info("EmployeePlanningFact:{}",new CompareToBuilder().append(o1.getAvialableMinutes(),o2.getAvialableMinutes()).append(o1.getId(),o2.getId()).toComparison());
    	return new CompareToBuilder().append(o1.getId(),o2.getId()).toComparison();//availablemins
    }
}
