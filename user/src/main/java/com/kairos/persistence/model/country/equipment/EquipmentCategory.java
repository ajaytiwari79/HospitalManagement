package com.kairos.persistence.model.country.equipment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by prerna on 12/12/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
public class EquipmentCategory extends UserBaseEntity{
    private String name;
    private String description;
    private Float weightInKg;
    private Float lengthInCm;
    private Float heightInCm;
    private Float widthInCm;
    private Float volumeInCm;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getWeightInKg() {
        return weightInKg;
    }

    public void setWeightInKg(Float weightInKg) {
        this.weightInKg = weightInKg;
    }

    public Float getLengthInCm() {
        return lengthInCm;
    }

    public void setLengthInCm(Float lengthInCm) {
        this.lengthInCm = lengthInCm;
    }

    public Float getHeightInCm() {
        return heightInCm;
    }

    public void setHeightInCm(Float heightInCm) {
        this.heightInCm = heightInCm;
    }

    public Float getWidthInCm() {
        return widthInCm;
    }

    public void setWidthInCm(Float widthInCm) {
        this.widthInCm = widthInCm;
    }

    public Float getVolumeInCm() {
        return volumeInCm;
    }

    public void setVolumeInCm(Float volumeInCm) {
        this.volumeInCm = volumeInCm;
    }
}
