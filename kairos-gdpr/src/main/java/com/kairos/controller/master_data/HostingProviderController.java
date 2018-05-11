package com.kairos.controller.master_data;


import com.kairos.service.master_data.HostingProviderService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;

import static com.kairos.constant.ApiConstant.API_HOSTING_PROVIDER_URL;

@RestController
@RequestMapping(API_HOSTING_PROVIDER_URL)
@Api(API_HOSTING_PROVIDER_URL)
public class HostingProviderController {


    @Inject
    private HostingProviderService hostingProviderService;


    @ApiOperation("add HostingProvider")
    @PostMapping("/add")
    public ResponseEntity<Object> createHostingProvider(@RequestParam String hostingProvider) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.createHostingProvider(hostingProvider));

    }


    @ApiOperation("get HostingProvider by id")
    @GetMapping("/id/{id}")
    public ResponseEntity<Object> getHostingProviderById(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.getHostingProviderById(id));

    }


    @ApiOperation("get all HostingProvider ")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllHostingProvider() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.getAllHostingProvider());

    }


    @ApiOperation("delete HostingProvider  by id")
    @DeleteMapping("/delete/id/{id}")
    public ResponseEntity<Object> deleteHostingProviderById(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.deleteHostingProviderById(id));

    }

    @ApiOperation("update HostingProvider by id")
    @PutMapping("/update/id/{id}")
    public ResponseEntity<Object> updateHostingProvider(@PathVariable BigInteger id, @RequestParam String hostingProvider) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingProviderService.updateHostingProvider(id, hostingProvider));

    }


}
