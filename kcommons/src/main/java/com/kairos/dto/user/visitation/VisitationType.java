package com.kairos.dto.user.visitation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by oodles on 26/4/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class VisitationType {
    private String id;

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+"]";
    }
}