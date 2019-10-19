package com.kairos.dto.user.patient;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientState {
    private String id;

    private String defaultObject;

    private String color;

    private String name;

    private PatientStateLink _links;

    private String active;

    private PatientStateType type;

    private String version;

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", defaultObject = "+defaultObject+", color = "+color+", name = "+name+", _links = "+_links+", active = "+active+", type = "+type+", version = "+version+"]";
    }
}