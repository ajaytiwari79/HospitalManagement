package com.kairos.dto.user.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.user.staff.CurrentAddress;
import com.kairos.dto.user.staff.Grants;

import java.util.List;

/**
 * Created by oodles on 25/7/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientResourceList {
    private String id;

    private String fontColor;

    private CurrentAddress currentAddress;

    private String name;

    private List<Grants> grants;

    private String version;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getFontColor ()
    {
        return fontColor;
    }

    public void setFontColor (String fontColor)
    {
        this.fontColor = fontColor;
    }

    public CurrentAddress getCurrentAddress ()
    {
        return currentAddress;
    }

    public void setCurrentAddress (CurrentAddress currentAddress)
    {
        this.currentAddress = currentAddress;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public List<Grants> getGrants() {
        return grants;
    }

    public void setGrants(List<Grants> grants) {
        this.grants = grants;
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
        return "ClassPojo [id = "+id+", fontColor = "+fontColor+", currentAddress = "+currentAddress+", name = "+name+", grants = "+grants+", version = "+version+"]";
    }
}
