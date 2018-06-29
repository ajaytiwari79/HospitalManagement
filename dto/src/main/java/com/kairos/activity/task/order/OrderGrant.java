package com.kairos.activity.task.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderGrant {

    private String id;

    private String model;

    private String name;

    private String packageId;

    private Integer originatorId;

    private String version;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getModel ()
    {
        return model;
    }

    public void setModel (String model)
    {
        this.model = model;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getPackageId ()
    {
        return packageId;
    }

    public void setPackageId (String packageId)
    {
        this.packageId = packageId;
    }

    public Integer getOriginatorId() {
        return originatorId;
    }

    public void setOriginatorId(Integer originatorId) {
        this.originatorId = originatorId;
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
        return "ClassPojo [id = "+id+", model = "+model+", name = "+name+", packageId = "+packageId+", originatorId = "+originatorId+", version = "+version+"]";
    }
}