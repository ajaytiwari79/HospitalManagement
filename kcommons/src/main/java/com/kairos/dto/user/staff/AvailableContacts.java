package com.kairos.dto.user.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class AvailableContacts {
    private String id;

    private List<RelativeContacts> relativeContacts;

    private String version;


    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", relativeContacts = "+relativeContacts+", version = "+version+"]";
    }
}