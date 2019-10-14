package com.kairos.persistence.model.user.resources;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created by prabjot on 13/10/17.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResourceTypeWrapper {

    private List<Vehicle> vehicleTypeList;
    private List<FuelType> fuelTypeList;

}
