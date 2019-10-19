package com.kairos.dto.user.visitation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class RepetitionNext {
    private RepetitionType next;

    private String weekenddays;

    private String weekdays;

    @Override
    public String toString()
    {
        return "ClassPojo [next = "+next+"]";
    }
}