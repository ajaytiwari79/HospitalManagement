package com.kairos.dto.user.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by oodles on 19/4/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientStateType {
    private String id;

    private String name;

   // private String _links;

    private String abbreviation;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }



    public String getAbbreviation ()
    {
        return abbreviation;
    }

    public void setAbbreviation (String abbreviation)
    {
        this.abbreviation = abbreviation;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", name = "+name+", abbreviation = "+abbreviation+"]";
    }
}
