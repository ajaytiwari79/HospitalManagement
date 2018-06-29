package com.kairos.user.patient;

public class PatientState {
    private String id;

    private String defaultObject;

    private String color;

    private String name;

    private PatientStateLink _links;

    private String active;

    private PatientStateType type;

    private String version;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getDefaultObject ()
    {
        return defaultObject;
    }

    public void setDefaultObject (String defaultObject)
    {
        this.defaultObject = defaultObject;
    }

    public String getColor ()
    {
        return color;
    }

    public void setColor (String color)
    {
        this.color = color;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }





    public String getActive ()
    {
        return active;
    }

    public void setActive (String active)
    {
        this.active = active;
    }

    public PatientStateLink get_links() {
        return _links;
    }

    public void set_links(PatientStateLink _links) {
        this._links = _links;
    }

    public PatientStateType getType() {
        return type;
    }

    public void setType(PatientStateType type) {
        this.type = type;
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
        return "ClassPojo [id = "+id+", defaultObject = "+defaultObject+", color = "+color+", name = "+name+", _links = "+_links+", active = "+active+", type = "+type+", version = "+version+"]";
    }
}