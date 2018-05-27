package com.kairos.controller.master_data_management.processing_activity_masterdata;

import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.Destination;
import com.kairos.service.master_data_management.processing_activity_masterdata.DestinationService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

import static com.kairos.constant.ApiConstant.API_DESTINATION;
/*
 *
 *  created by bobby 20/5/2018
 * */


@RestController
@RequestMapping(API_DESTINATION)
@Api(API_DESTINATION)
public class DestinationController {


    @Inject
    private DestinationService destinationService;


    @ApiOperation("add Destination")
    @PostMapping("/add")
    public ResponseEntity<Object> createDestination(@PathVariable Long countryId, @RequestBody List<Destination> destinations) {

        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, destinationService.createDestination(countryId, destinations));

    }


    @ApiOperation("get Destination by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getDestination(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id is null");

        } else
            return ResponseHandler.generateResponse(HttpStatus.OK, true, destinationService.getDestination(countryId, id));

    }


    @ApiOperation("get all Destination")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllDestination() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, destinationService.getAllDestination());

    }

    @ApiOperation("get Destination by name")
    @GetMapping("/")
    public ResponseEntity<Object> getDestinationByName(@PathVariable Long countryId, @RequestParam String name) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, destinationService.getDestinationByName(countryId, name));

    }


    @ApiOperation("delete Destination by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteDestination(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, destinationService.deleteDestination(id));

    }

    @ApiOperation("update Destination by id")
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateDestination(@PathVariable BigInteger id, @RequestBody Destination destination) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, destinationService.updateDestination(id, destination));

    }


}
