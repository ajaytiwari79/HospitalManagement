package com.kairos.dto.user.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by oodles on 25/7/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Grants {
    private String plannedStatusDelivery;

    private String fontColor;

    private String registeredStatusName;

    private String unorderedButPlanned;

    private String statusColor;

    private String registeredDuration;

    private String registeredStatusDelivered;

    private String version;

    private String id;

    private String statusId;

    private String plannedStatusName;

    private String duration;

    private String description;

    private String ordered;

    private String name;

    private String background;

    public String getPlannedStatusDelivery ()
    {
        return plannedStatusDelivery;
    }

    public void setPlannedStatusDelivery (String plannedStatusDelivery)
    {
        this.plannedStatusDelivery = plannedStatusDelivery;
    }

    public String getFontColor ()
    {
        return fontColor;
    }

    public void setFontColor (String fontColor)
    {
        this.fontColor = fontColor;
    }





    public String getUnorderedButPlanned ()
    {
        return unorderedButPlanned;
    }

    public void setUnorderedButPlanned (String unorderedButPlanned)
    {
        this.unorderedButPlanned = unorderedButPlanned;
    }

    public String getStatusColor ()
    {
        return statusColor;
    }

    public void setStatusColor (String statusColor)
    {
        this.statusColor = statusColor;
    }





    public String getRegisteredStatusDelivered ()
    {
        return registeredStatusDelivered;
    }

    public void setRegisteredStatusDelivered (String registeredStatusDelivered)
    {
        this.registeredStatusDelivered = registeredStatusDelivered;
    }

    public String getVersion ()
    {
        return version;
    }

    public void setVersion (String version)
    {
        this.version = version;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getStatusId ()
    {
        return statusId;
    }

    public void setStatusId (String statusId)
    {
        this.statusId = statusId;
    }

    public String getPlannedStatusName ()
    {
        return plannedStatusName;
    }

    public void setPlannedStatusName (String plannedStatusName)
    {
        this.plannedStatusName = plannedStatusName;
    }

    public String getDuration ()
    {
        return duration;
    }

    public void setDuration (String duration)
    {
        this.duration = duration;
    }

    public String getRegisteredStatusName() {
        return registeredStatusName;
    }

    public void setRegisteredStatusName(String registeredStatusName) {
        this.registeredStatusName = registeredStatusName;
    }

    public String getRegisteredDuration() {
        return registeredDuration;
    }

    public void setRegisteredDuration(String registeredDuration) {
        this.registeredDuration = registeredDuration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrdered ()
    {
        return ordered;
    }

    public void setOrdered (String ordered)
    {
        this.ordered = ordered;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getBackground ()
    {
        return background;
    }

    public void setBackground (String background)
    {
        this.background = background;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [plannedStatusDelivery = "+plannedStatusDelivery+", fontColor = "+fontColor+", registeredStatusName = "+registeredStatusName+", unorderedButPlanned = "+unorderedButPlanned+", statusColor = "+statusColor+", registeredDuration = "+registeredDuration+", registeredStatusDelivered = "+registeredStatusDelivered+", version = "+version+", id = "+id+", statusId = "+statusId+", plannedStatusName = "+plannedStatusName+", duration = "+duration+", description = "+description+", ordered = "+ordered+", name = "+name+", background = "+background+"]";
    }
}
