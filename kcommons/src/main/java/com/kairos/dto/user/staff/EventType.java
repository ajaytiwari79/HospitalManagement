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
public class EventType {
    private String color;

    private String name;

    @Override
    public String toString()
    {
        return "ClassPojo [color = "+color+", name = "+name+"]";
    }
}
