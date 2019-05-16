package com.kairos.persistence.model.user.region;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.hibernate.validator.constraints.Range;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotBlank;
import java.util.*;

import static com.kairos.constants.UserMessagesConstants.*;
import static com.kairos.persistence.model.constants.RelationshipConstants.MUNICIPALITY;


/**
 * Created by oodles on 28/12/16.
 */
@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
public class ZipCode extends UserBaseEntity {

    @NotBlank(message = ERROR_ZIPCODE_NAME_NOTEMPTY)
    private String name;
    @Range(min=1,message = ERROR_ZIPCODE_ZIPCODE_NOTNULL)
    private int zipCode;
    @NotBlank(message = ERROR_ZIPCODE_GEOFENCE_NOTEMPTY)
    private String geoFence;

    private boolean isEnable = true;

    @Relationship(type = MUNICIPALITY)
    private List<Municipality> municipalities;


    public String getGeoFence() {
        return geoFence;
    }

    public void setGeoFence(String geoFence) {
        this.geoFence = geoFence;
    }

    public List<Municipality> getMunicipalities() {
        return Optional.ofNullable(municipalities).orElse(new ArrayList<>());
    }

    public void setMunicipalities(List<Municipality> municipalities) {
        this.municipalities = municipalities;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
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
        response.put("zipCode",this.zipCode);
        response.put("geoFence",this.geoFence);
        return  response;

    }
}
