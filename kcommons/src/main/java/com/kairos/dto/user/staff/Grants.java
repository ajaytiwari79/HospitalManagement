package com.kairos.dto.user.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by oodles on 25/7/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
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

    @Override
    public String toString()
    {
        return "ClassPojo [plannedStatusDelivery = "+plannedStatusDelivery+", fontColor = "+fontColor+", registeredStatusName = "+registeredStatusName+", unorderedButPlanned = "+unorderedButPlanned+", statusColor = "+statusColor+", registeredDuration = "+registeredDuration+", registeredStatusDelivered = "+registeredStatusDelivered+", version = "+version+", id = "+id+", statusId = "+statusId+", plannedStatusName = "+plannedStatusName+", duration = "+duration+", description = "+description+", ordered = "+ordered+", name = "+name+", background = "+background+"]";
    }
}
