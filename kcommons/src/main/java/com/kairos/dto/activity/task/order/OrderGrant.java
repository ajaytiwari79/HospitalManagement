package com.kairos.dto.activity.task.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class OrderGrant {

    private String id;

    private String model;

    private String name;

    private String packageId;

    private Integer originatorId;

    private String version;

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", model = "+model+", name = "+name+", packageId = "+packageId+", originatorId = "+originatorId+", version = "+version+"]";
    }
}