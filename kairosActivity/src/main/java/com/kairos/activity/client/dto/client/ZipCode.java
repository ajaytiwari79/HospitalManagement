package com.kairos.activity.client.dto.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;
import javax.validation.constraints.NotNull;
import java.util.*;
@JsonIgnoreProperties(ignoreUnknown = true)
public class ZipCode{
    private String name;
    private int zipCode;
    private String geoFence;

    private boolean isEnable = true;

    public String getGeoFence() {
        return geoFence;
    }

    public void setGeoFence(String geoFence) {
        this.geoFence = geoFence;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }
}
