package com.kairos.planning.domain;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

public class VehicleComparator implements Comparator<Vehicle> {
    private static Logger log= LoggerFactory.getLogger(VehicleComparator.class);


    @Override
    public int compare(Vehicle v1, Vehicle v2) {
       // return v1.getSpeedKmpm().compareTo(v2.getSpeedKmpm());
        return new CompareToBuilder().append(v1.getRequiredSkillList().size(),v2.getRequiredSkillList().size())
                .append(v1.getSpeedKmpm(),v2.getSpeedKmpm()).append(v1.getType(),v2.getType())
                .append(v1.getId(),v2.getId()).toComparison();
    }
}
