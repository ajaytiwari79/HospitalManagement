package com.kairos.user.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CitizenSupplier {
    private String id;

    @JsonIgnoreProperties
    private String organization;

    private String cvrNumber;

    private String name;

    private Boolean active;

    private String type;

    private String version;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getOrganization ()
    {
        return organization;
    }

    public void setOrganization (String organization)
    {
        this.organization = organization;
    }

    public String getCvrNumber() {
        return cvrNumber;
    }

    public void setCvrNumber(String cvrNumber) {
        this.cvrNumber = cvrNumber;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
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
        return "ClassPojo [id = "+id+", organization = "+organization+", cvrNumber = "+cvrNumber+", name = "+name+", active = "+active+", type = "+type+", version = "+version+"]";
    }
}