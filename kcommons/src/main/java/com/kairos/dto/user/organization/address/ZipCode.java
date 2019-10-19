package com.kairos.dto.user.organization.address;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ZipCode{
    private String name;
    private int zipCode;
    private String geoFence;

    private boolean isEnable = true;
}
