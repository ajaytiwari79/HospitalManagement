package com.kairos.controller.master_data;


import com.kairos.service.master_data.HostingLocationService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;

import static com.kairos.constant.ApiConstant.API_HOSTING_LOCATION_URL;


@RestController
@RequestMapping(API_HOSTING_LOCATION_URL)
@Api(API_HOSTING_LOCATION_URL)
public class HostingLocationController {


    @Inject
    private HostingLocationService hostingLocationService;


    @ApiOperation("add HostingLocation")
    @PostMapping("/add")
    public ResponseEntity<Object> createHostingLocation(@RequestParam String hostingLocation) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingLocationService.createHostingLocation(hostingLocation));

    }


    @ApiOperation("get HostingLocation by id")
    @GetMapping("/id/{id}")
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
    @DeleteMapping("/delete/id/{id}")
    public ResponseEntity<Object> deleteHostingLocationById(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingLocationService.deleteHostingLocationById(id));

    }

    @ApiOperation("update HostingLocation by id")
    @PutMapping("/update/id/{id}")
    public ResponseEntity<Object> updateHostingLocation(@PathVariable BigInteger id, @RequestParam String hostingLocation) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingLocationService.updateHostingLocation(id, hostingLocation));

    }


}
