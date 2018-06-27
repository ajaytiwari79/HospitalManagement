package com.kairos.user.patient.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by oodles on 26/4/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParagraphGroup {
    private String id;

    private VisitationType visitationType;

    private String name;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public VisitationType getVisitationType ()
    {
        return visitationType;
    }

    public void setVisitationType (VisitationType visitationType)
    {
        this.visitationType = visitationType;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", visitationType = "+visitationType+", name = "+name+"]";
    }
}
