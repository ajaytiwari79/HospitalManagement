package com.kairos.persistence.model.user.region;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.country.Country;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;


/**
 * Created by oodles on 12/12/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
public class Region extends UserBaseEntity {

    @NotEmpty(message = "error.Region.name.notEmpty") @NotNull(message = "error.Region.name.notnull")
    private String name;
    @NotEmpty(message = "error.Region.code.notEmpty") @NotNull(message = "error.Region.code.notnull")
    private String code;
    @NotEmpty(message = "error.Region.geoFence.notEmpty") @NotNull(message = "error.Region.geoFence.notnull")
    private String geoFence;

    private float latitude;

    private float longitude;

    @Relationship(type = BELONGS_TO)
    private Country country;

    private boolean isEnable = true;

    public Region() {
    }

    public Region(String name, String code, float latitude, float longitude) {
        this.name = name;
        this.code = code;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }


    public String getGeoFence() {
        return geoFence;
    }

    public void setGeoFence(String geoFence) {
        this.geoFence = geoFence;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
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
