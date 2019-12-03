package com.kairos.dto.user.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by oodles on 19/4/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class PatientStateType {
    private String id;

    private String name;


    private String abbreviation;


    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", name = "+name+", abbreviation = "+abbreviation+"]";
    }
}
