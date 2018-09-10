package com.kairos.dto.user.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by oodles on 19/7/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventResource {

    private String fontColor;

    private String resourceId;

    private String primaryIdentifier;

    private String professionalJob;

    private String visible;

    private String backgroundColor;

    private String initials;

    private String active;

    private String fullName;

    private String displayName;

    public String getFontColor ()
    {
        return fontColor;
    }

    public void setFontColor (String fontColor)
    {
        this.fontColor = fontColor;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getPrimaryIdentifier() {
        return primaryIdentifier;
    }

    public void setPrimaryIdentifier(String primaryIdentifier) {
        this.primaryIdentifier = primaryIdentifier;
    }

    public String getProfessionalJob() {
        return professionalJob;
    }

    public void setProfessionalJob(String professionalJob) {
        this.professionalJob = professionalJob;
    }

    public String getVisible ()
    {
        return visible;
    }

    public void setVisible (String visible)
    {
        this.visible = visible;
    }

    public String getBackgroundColor ()
    {
        return backgroundColor;
    }

    public void setBackgroundColor (String backgroundColor)
    {
        this.backgroundColor = backgroundColor;
    }

    public String getInitials ()
    {
        return initials;
    }

    public void setInitials (String initials)
    {
        this.initials = initials;
    }


    public String getActive ()
    {
        return active;
    }

    public void setActive (String active)
    {
        this.active = active;
    }

    public String getFullName ()
    {
        return fullName;
    }

    public void setFullName (String fullName)
    {
        this.fullName = fullName;
    }

    public String getDisplayName ()
    {
        return displayName;
    }

    public void setDisplayName (String displayName)
    {
        this.displayName = displayName;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [fontColor = "+fontColor+", resourceId = "+resourceId+", primaryIdentifier = "+primaryIdentifier+", professionalJob = "+professionalJob+", visible = "+visible+", backgroundColor = "+backgroundColor+", initials = "+initials+", active = "+active+", fullName = "+fullName+", displayName = "+displayName+"]";
    }

}
