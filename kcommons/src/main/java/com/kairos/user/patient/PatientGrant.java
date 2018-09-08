package com.kairos.user.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by oodles on 26/4/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientGrant {

    private String basketId;

    private WorkflowState workflowState;

    private String color;

    private String name;

    private List<CurrentElements> currentElements;

    private String version;

    public String getBasketId ()
    {
        return basketId;
    }

    public void setBasketId (String basketId)
    {
        this.basketId = basketId;
    }

    public WorkflowState getWorkflowState ()
    {
        return workflowState;
    }

    public void setWorkflowState (WorkflowState workflowState)
    {
        this.workflowState = workflowState;
    }

    public String getColor ()
    {
        return color;
    }

    public void setColor (String color)
    {
        this.color = color;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public List<CurrentElements> getCurrentElements() {
        return currentElements;
    }

    public void setCurrentElements(List<CurrentElements> currentElements) {
        this.currentElements = currentElements;
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
        return "ClassPojo [basketId = "+basketId+", workflowState = "+workflowState+", color = "+color+", name = "+name+", currentElements = "+currentElements+", version = "+version+"]";
    }
}