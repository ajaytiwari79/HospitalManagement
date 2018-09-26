package com.kairos.dto.user.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.user.patient.PatientStateLink;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RelativeContacts {
    private String id;

    private String phoneNumber;

    private String name;

    private PatientStateLink _links;

    private String version;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getPhoneNumber ()
    {
        return phoneNumber;
    }

    public void setPhoneNumber (String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public PatientStateLink get_links() {
        return _links;
    }

    public void set_links(PatientStateLink _links) {
        this._links = _links;
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
        return "ClassPojo [id = "+id+", phoneNumber = "+phoneNumber+", name = "+name+", _links = "+_links+", version = "+version+"]";
    }
}