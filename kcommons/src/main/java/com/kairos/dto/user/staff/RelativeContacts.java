package com.kairos.dto.user.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.user.patient.PatientStateLink;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class RelativeContacts {
    private String id;

    private String phoneNumber;

    private String name;

    private PatientStateLink _links;

    private String version;

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", phoneNumber = "+phoneNumber+", name = "+name+", _links = "+_links+", version = "+version+"]";
    }
}