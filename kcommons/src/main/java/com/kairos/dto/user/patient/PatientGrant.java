package com.kairos.dto.user.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by oodles on 26/4/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class PatientGrant {

    private String basketId;

    private WorkflowState workflowState;

    private String color;

    private String name;

    private List<CurrentElements> currentElements;

    private String version;


    @Override
    public String toString()
    {
        return "ClassPojo [basketId = "+basketId+", workflowState = "+workflowState+", color = "+color+", name = "+name+", currentElements = "+currentElements+", version = "+version+"]";
    }
}