package com.kairos.dto.user.patient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.user.staff.CurrentAddress;
import com.kairos.dto.user.staff.Grants;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by oodles on 25/7/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class PatientResourceList {
    private String id;

    private String fontColor;

    private CurrentAddress currentAddress;

    private String name;

    private List<Grants> grants;

    private String version;

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", fontColor = "+fontColor+", currentAddress = "+currentAddress+", name = "+name+", grants = "+grants+", version = "+version+"]";
    }
}
