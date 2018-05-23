package com.kairos.controller.master_data_management.asset_management;


import com.kairos.persistance.model.master_data_management.asset_management.HostingType;
import com.kairos.service.master_data_management.asset_management.HostingTypeService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import static com.kairos.constant.ApiConstant.API_HOSTING_TYPE_URL;

@RestController
@RequestMapping(API_HOSTING_TYPE_URL)
@Api(API_HOSTING_TYPE_URL)
@CrossOrigin
public class HostingTypeController {




    @Inject
    private HostingTypeService hostingTypeService;


    @ApiOperation("add HostingType")
    @PostMapping("/add")
    public ResponseEntity<Object> createHostingType(@RequestBody List<HostingType> hostingTypes) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.createHostingType(hostingTypes));

    }


    @ApiOperation("get HostingType by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getHostingType(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.getHostingType(id));

    }


    @ApiOperation("get all HostingType ")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllHostingType() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.getAllHostingType());

    }


    @ApiOperation("get HostingType by name")
    @GetMapping("/")
    public ResponseEntity<Object> getHostingTypeByName(@RequestParam String name) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.getHostingTypeByName(name));

    }


    @ApiOperation("delete HostingType  by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteHostingType(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.deleteHostingType(id));

    }

    @ApiOperation("update HostingType by id")
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateHostingType(@PathVariable BigInteger id, @RequestBody HostingType hostingtype) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.updateHostingType(id, hostingtype));

    }


}
