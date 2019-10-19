package com.kairos.dto.user.patient;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientIdentifier {
    private String managedExternally;
    private String type;

    private String identifier;


    @Override
    public String toString() {
        return "ClassPojo [managedExternally = " + managedExternally + ", type = " + type + ", identifier = " + identifier + "]";
    }
}