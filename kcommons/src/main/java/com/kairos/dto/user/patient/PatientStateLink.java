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
public class PatientStateLink {
    private Self self;

    @Override
    public String toString()
    {
        return "ClassPojo [self = "+self+"]";
    }
}
