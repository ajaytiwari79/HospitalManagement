package com.kairos.dto.user.organization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;


/**
 * Created by oodles on 22/12/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class Municipality {

    Long id;
    private String name;
    private String geoFence;
    private String code;

    private float latitude;
    private float longitude;

    private boolean isEnable = true;



}
