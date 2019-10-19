package com.kairos.dto.user.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by oodles on 19/7/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ImportShiftDTO {
    private Date startTime;

    private String id;

    private EventResource eventResource;

    private String supplierId;

    private String status;

    private Date endTime;

    private String transportType;

    private String version;


    @Override
    public String toString()
    {
        return "ClassPojo [startTime = "+startTime+", id = "+id+", eventResource = "+eventResource+", supplierId = "+supplierId+", status = "+status+", endTime = "+endTime+", transportType = "+transportType+", version = "+version+"]";
    }
}
