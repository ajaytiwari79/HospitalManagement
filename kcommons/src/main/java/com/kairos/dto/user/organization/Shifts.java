package com.kairos.dto.user.organization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by oodles on 4/5/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class Shifts {
    private String id;

    private String category;

    private String title;

    private String order;

    private String start;

    private String active;

    private String end;

    private String version;

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", category = "+category+", title = "+title+", order = "+order+", start = "+start+", active = "+active+", end = "+end+", version = "+version+"]";
    }
}
