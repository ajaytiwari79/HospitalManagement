package com.kairos.dto.user.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.user.staff.client.CitizenOverviewForms;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientLinks {
    private CitizenOverviewForms citizenOverviewForms;

    private PatientOverview patientOverview;

    private Self self;

    public CitizenOverviewForms getCitizenOverviewForms ()
    {
        return citizenOverviewForms;
    }

    public void setCitizenOverviewForms (CitizenOverviewForms citizenOverviewForms)
    {
        this.citizenOverviewForms = citizenOverviewForms;
    }

    public PatientOverview getPatientOverview ()
    {
        return patientOverview;
    }

    public void setPatientOverview (PatientOverview patientOverview)
    {
        this.patientOverview = patientOverview;
    }

    public Self getSelf ()
    {
        return self;
    }

    public void setSelf (Self self)
    {
        this.self = self;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [citizenOverviewForms = "+citizenOverviewForms+", patientOverview = "+patientOverview+", self = "+self+"]";
    }
}