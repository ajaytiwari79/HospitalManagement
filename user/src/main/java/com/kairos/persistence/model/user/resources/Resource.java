package com.kairos.persistence.model.user.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.equipment.Equipment;
import com.kairos.persistence.model.country.feature.Feature;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.RESOURCE_HAS_EQUIPMENT;
import static com.kairos.persistence.model.constants.RelationshipConstants.RESOURCE_HAS_FEATURE;

/**
 * Created by arvind on 6/10/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
@Getter
@Setter
@NoArgsConstructor
public class Resource extends UserBaseEntity {


    private Vehicle vehicleType;
    private String registrationNumber;
    private String number;
    private String modelDescription;
    private float costPerKM;
    private FuelType fuelType;
    private boolean enabled ;
    private Long decommissionDate;

    @Relationship(type = RESOURCE_HAS_FEATURE)
    private List<Feature> features = new ArrayList<>();

    @Relationship(type = RESOURCE_HAS_EQUIPMENT)
    private List<Equipment> equipments = new ArrayList<>();

    public Resource(Vehicle vehicleType, String registrationNumber, String number, String modelDescription,
                    float costPerKM,FuelType fuelType) {
        this.vehicleType = vehicleType;
        this.registrationNumber = registrationNumber;
        this.number = number;
        this.modelDescription = modelDescription;
        this.costPerKM = costPerKM;
        this.fuelType = fuelType;
    }

}



