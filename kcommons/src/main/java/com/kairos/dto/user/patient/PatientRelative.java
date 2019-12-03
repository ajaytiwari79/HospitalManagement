package com.kairos.dto.user.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class PatientRelative {
    private String id;

    private String patientId;

    private RelatedPatient relatedPatient;

    private RelativeContactDetails contact;

    private String importance;

    private String displayName;

    private String type;

    private String version;


    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", patientId = "+patientId+", relatedPatient = "+relatedPatient+", importance = "+importance+", displayName = "+displayName+", type = "+type+", version = "+version+"]";
    }
}