package com.kairos.response.dto.web;

import java.util.List;

/**
 * Created by oodles on 19/4/17.
 */
public class PatientList {
    private List<PatientWrapper> patientWrappers;

    public List<PatientWrapper> getPatientWrappers() {
        return patientWrappers;
    }

    public void setPatientWrappers(List<PatientWrapper> patientWrappers) {
        this.patientWrappers = patientWrappers;
    }

    public PatientList() {
    }
}
