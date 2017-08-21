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
 * Created by oodles on 22/12/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
public class Municipality extends UserBaseEntity{
    @NotEmpty(message = "error.Municipality.name.notEmpty") @NotNull(message = "error.Municipality.name.notnull")
    private String name;

    @NotEmpty(message = "error.Municipality.geoFence.notEmpty") @NotNull(message = "error.Municipality.geoFence.notnull")
    private String geoFence;

    @NotEmpty(message = "error.Municipality.code.notEmpty") @NotNull(message = "error.Municipality.code.notnull")
    private String code;

    private float latitude;
    private float longitude;

    @Relationship(type = PROVINCE)
    private Province province;
    private boolean isEnable = true;


    public String getGeoFence() {
        return geoFence;
    }

    public void setGeoFence(String geoFence) {
        this.geoFence = geoFence;
    }

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
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



    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Map<String,Object> retrieveDetails() {
        Map<String,Object> response = new HashMap();
        response.put("id",this.id);
        response.put("name",this.name);
        response.put("code",this.code);
        response.put("geoFence",this.geoFence);
        return  response;

    }

    @Override
    public Municipality clone() throws CloneNotSupportedException {
        Municipality municipality = (Municipality) super.clone();
        municipality.setId(null);
        return municipality;
    }
}
