package com.kairos.dto.user.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by oodles on 19/7/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
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


    @Override
    public String toString()
    {
        return "ClassPojo [fontColor = "+fontColor+", resourceId = "+resourceId+", primaryIdentifier = "+primaryIdentifier+", professionalJob = "+professionalJob+", visible = "+visible+", backgroundColor = "+backgroundColor+", initials = "+initials+", active = "+active+", fullName = "+fullName+", displayName = "+displayName+"]";
    }

}
