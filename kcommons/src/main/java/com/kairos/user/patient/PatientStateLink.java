package com.kairos.user.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * Created by oodles on 19/4/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientStateLink {
    private Self self;

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
        return "ClassPojo [self = "+self+"]";
    }
}
