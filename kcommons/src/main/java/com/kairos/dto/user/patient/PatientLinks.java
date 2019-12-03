package com.kairos.dto.user.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.user.staff.client.CitizenOverviewForms;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class PatientLinks {
    private CitizenOverviewForms citizenOverviewForms;

    private PatientOverview patientOverview;

    private Self self;


    @Override
    public String toString()
    {
        return "ClassPojo [citizenOverviewForms = "+citizenOverviewForms+", patientOverview = "+patientOverview+", self = "+self+"]";
    }
}