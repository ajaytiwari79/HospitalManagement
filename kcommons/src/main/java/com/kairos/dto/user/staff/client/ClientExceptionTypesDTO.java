package com.kairos.dto.user.staff.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

/**
 * Created by oodles on 25/10/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ClientExceptionTypesDTO {

    private boolean isEnabled= true;

    private String name;
    private String value;
    private String description;
    private BigInteger id;
}
