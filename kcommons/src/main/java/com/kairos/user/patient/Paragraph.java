package com.kairos.user.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Paragraph {
    private String id;

    private String description;

    private String name;

    private String active;

    private ParagraphGroup group;

    private String section;

    private String version;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }


    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
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

    public ParagraphGroup getGroup() {
        return group;
    }

    public void setGroup(ParagraphGroup group) {
        this.group = group;
    }

    public String getSection ()
    {
        return section;
    }

    public void setSection (String section)
    {
        this.section = section;
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
        return "ClassPojo [id = "+id+", description = "+description+", name = "+name+", active = "+active+", group = "+group+", section = "+section+", version = "+version+"]";
    }
}