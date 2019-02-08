package com.kairos.controller.resources;


import com.kairos.persistence.model.user.resources.VehicleLocation;
import com.kairos.service.resources.VehicleLocationService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_V1;
import static com.kairos.constants.ApiConstants.COUNTRY_URL;

/**
 * Created by oodles on 13/12/17.
 */
@RestController
@RequestMapping(API_V1)
public class VehicleLocationController {

    @Inject
    private VehicleLocationService vehicleLocationService;

    @RequestMapping(value = COUNTRY_URL + "/vehicleLocation", method = RequestMethod.POST)
    @ApiOperation("Create new Vehicel Location")
    public ResponseEntity<Map<String, Object>> createVehicleLocation(@Validated @RequestBody VehicleLocation vehicleLocation, @PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,vehicleLocationService.createVehicleLocation(vehicleLocation));
    }

    @RequestMapping(value = COUNTRY_URL + "/vehicleLocation/{vehicleLocationId}", method = RequestMethod.PUT)
    @ApiOperation("Update Vehicel Location")
    public ResponseEntity<Map<String, Object>> updateVehicleLocation(@Validated @RequestBody VehicleLocation vehicleLocation, @PathVariable long vehicleLocationId, @PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,vehicleLocationService.updateVehicleLocation(vehicleLocation, vehicleLocationId));
    }

    @RequestMapping(value = COUNTRY_URL + "/vehicleLocation/{vehicleLocationId}", method = RequestMethod.DELETE)
    @ApiOperation("Delete Vehicel Location")
    public ResponseEntity<Map<String, Object>> deleteVehicleLocation(@PathVariable Long vehicleLocationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,vehicleLocationService.deleteVehicleLocation(vehicleLocationId));
    }

    @RequestMapping(value = COUNTRY_URL + "/vehicleLocation", method = RequestMethod.GET)
    @ApiOperation("Get All Vehicel Locations")
    public ResponseEntity<Map<String, Object>> getAllVehicleLocations() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,vehicleLocationService.getAllVehicleLocations());
    }

}



