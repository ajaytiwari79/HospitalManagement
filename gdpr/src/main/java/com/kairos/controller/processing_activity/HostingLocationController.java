package com.kairos.controller.processing_activity;


import com.kairos.service.processing_activity.HostingLocationService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;

import static com.kairos.constants.ApiConstant.API_HOSTING_LOCATION_URL;


@RestController
@RequestMapping(API_HOSTING_LOCATION_URL)
@Api(API_HOSTING_LOCATION_URL)
public class HostingLocationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostingLocationController.class);

    @Inject
    private HostingLocationService hostingLocationService;


    @ApiOperation("add HostingLocation")
    @PostMapping("/add")
    public ResponseEntity<Object> createHostingLocation(@RequestParam String hostingLocation) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingLocationService.createHostingLocation(hostingLocation));

    }


    @ApiOperation("get HostingLocation by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getHostingLocationById(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingLocationService.getHostingLocationById(id));

    }


    @ApiOperation("get all HostingLocation ")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllHostingLocation() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingLocationService.getAllHostingLocation());

    }


    @ApiOperation("delete HostingLocation  by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteHostingLocationById(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingLocationService.deleteHostingLocationById(id));

    }

    @ApiOperation("update HostingLocation by id")
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateHostingLocation(@PathVariable BigInteger id, @RequestParam String hostingLocation) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingLocationService.updateHostingLocation(id, hostingLocation));

    }


}
