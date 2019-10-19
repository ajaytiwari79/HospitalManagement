package com.kairos.dto.user.staff.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by oodles on 9/5/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class CitizenSupplier {
    private String id;

    @JsonIgnoreProperties
    private String organization;

    private String cvrNumber;

    private String name;

    private Boolean active;

    private String type;

    private String version;

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", organization = "+organization+", cvrNumber = "+cvrNumber+", name = "+name+", active = "+active+", type = "+type+", version = "+version+"]";
    }
}
