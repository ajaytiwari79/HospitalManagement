package com.kairos.persistence.model.user.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.io.Serializable;
import java.util.List;

/**
 * Created by prabjot on 16/10/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@QueryResult
@Getter
@Setter
@NoArgsConstructor
public class ResourceWrapper implements Serializable {
    private Long id;
    private String registrationNumber;
    private String number;
    private String modelDescription;
    private float costPerKM;
    private FuelType fuelType;
    private Vehicle vehicleType;
    private Long creationDate;
    private Long decommissionDate;
    private boolean isDecommision;
    private List<ResourceUnAvailability> resourceUnAvailabilities;

    @JsonProperty(value = "isDecommision")
    public boolean isDecommision() {
        return isDecommision;
    }

}
