package com.kairos.persistence.model.user.resources;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

import static com.kairos.constants.UserMessagesConstants.ERROR_DESCRIPTION_NOTNULL;

/**
 * Created by prabjot on 13/10/17.
 */
@Getter
@Setter
public class ResourceDTO {

    @NotNull(message = "Registration number can't be empty")
    private String registrationNumber;
    private String number;
    @NotNull(message = ERROR_DESCRIPTION_NOTNULL)
    private String modelDescription;
    @NotNull(message = "Cost per km can not be null")
    private float costPerKM;
    @NotNull(message = "Fuel type can not be null")
    private FuelType fuelType;
    private Long vehicleTypeId;
    private String decommissionDate;


    @Override
    public String toString() {
        return "ResourceDTO{" +
                "registrationNumber='" + registrationNumber + '\'' +
                ", number='" + number + '\'' +
                ", modelDescription='" + modelDescription + '\'' +
                ", costPerKM=" + costPerKM +
                ", vehicleTypeId=" + vehicleTypeId +
                '}';
    }
}
