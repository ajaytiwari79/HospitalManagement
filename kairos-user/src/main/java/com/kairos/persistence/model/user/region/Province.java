package com.kairos.persistence.model.user.region;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by oodles on 7/1/17.
 */
@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Province extends UserBaseEntity {
    @NotEmpty(message = "error.Province.name.notEmpty") @NotNull(message = "error.Province.name.notnull")
    private String name;
    @NotEmpty(message = "error.Province.geoFence.notEmpty") @NotNull(message = "error.Province.geoFence.notnull")
    private String geoFence;
    @NotEmpty(message = "error.Province.code.notEmpty") @NotNull(message = "error.Province.code.notnull")
    private String code;

    private float latitude;
    private float longitude;

    @Relationship(type = REGION)
    private Region region;

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    private boolean isEnable = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getGeoFence() {
        return geoFence;
    }

    public void setGeoFence(String geoFence) {
        this.geoFence = geoFence;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }


    public Map<String,Object> retrieveDetails() {
        Map<String,Object> response = new HashMap();
        response.put("id",this.id);
        response.put("name",this.name);
        response.put("code",this.code);
        response.put("geoFence",this.geoFence);
        return  response;

    }
}
