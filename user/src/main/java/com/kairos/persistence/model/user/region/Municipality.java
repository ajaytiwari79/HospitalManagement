package com.kairos.persistence.model.user.region;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.PROVINCE;


/**
 * Created by oodles on 22/12/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
public class Municipality extends UserBaseEntity {
    @NotBlank(message = "error.Municipality.name.notEmpty")
    private String name;

    @NotBlank(message = "error.Municipality.geoFence.notEmpty")
    private String geoFence;

    @NotBlank(message = "error.Municipality.code.notEmpty")
    private String code;

    private float latitude;
    private float longitude;

    @Relationship(type = PROVINCE)
    private Province province;
    private boolean isEnable = true;

    public Municipality() {
    }

    public Municipality(Long id,String name) {
        this.name = name;
        this.id=id;
    }

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

    public Municipality(Long id) {
        this.id = id;
    }

    public Map<String, Object> retrieveDetails() {
        Map<String, Object> response = new HashMap();
        response.put("id", this.id);
        response.put("name", this.name);
        response.put("code", this.code);
        response.put("geoFence", this.geoFence);
        return response;
    }

    public Municipality retrieveBasicDetails() {
        return new Municipality(this.id, this.name);
    }
}
