package com.kairos.activity.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by oodles on 12/5/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AvailableContacts {
    private String id;

    private List<RelativeContacts> relativeContacts;

    private String version;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public List<RelativeContacts> getRelativeContacts() {
        return relativeContacts;
    }

    public void setRelativeContacts(List<RelativeContacts> relativeContacts) {
        this.relativeContacts = relativeContacts;
    }

    public String getVersion ()
    {
        return version;
    }

    public void setVersion (String version)
    {
        this.version = version;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", relativeContacts = "+relativeContacts+", version = "+version+"]";
    }
}
