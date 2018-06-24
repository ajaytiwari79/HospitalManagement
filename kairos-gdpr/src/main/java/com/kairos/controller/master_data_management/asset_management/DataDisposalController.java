package com.kairos.controller.master_data_management.asset_management;

import com.kairos.persistance.model.master_data_management.asset_management.DataDisposal;
import com.kairos.service.master_data_management.asset_management.DataDisposalService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.validate_list.ValidateListOfRequestBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;

import static com.kairos.constants.ApiConstant.API_DATA_DISPOSAL_URL;
/*
 *
 *  created by bobby 16/5/2018
 * */


@RestController
@RequestMapping(API_DATA_DISPOSAL_URL)
@Api(API_DATA_DISPOSAL_URL)
public class DataDisposalController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataDisposalController.class);

    @Inject
    private DataDisposalService dataDisposalService;


    @ApiOperation("add DataDisposal")
    @PostMapping("/add")
    public ResponseEntity<Object> createDataDisposal(@PathVariable Long countryId,@PathVariable Long organizationId,@Valid @RequestBody ValidateListOfRequestBody<DataDisposal> dataDisposals) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataDisposalService.createDataDisposal(countryId,organizationId,dataDisposals.getRequestBody()));

    }


    @ApiOperation("get DataDisposal by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getDataDisposal(@PathVariable Long countryId,@PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id is null");

        } else
            return ResponseHandler.generateResponse(HttpStatus.OK, true, dataDisposalService.getDataDisposalById(countryId,organizationId,id));

    }


    @ApiOperation("get all DataDisposal ")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllDataDisposal(@PathVariable Long countryId,@PathVariable Long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataDisposalService.getAllDataDisposal(countryId,organizationId));

    }

    @ApiOperation("get DataDisposal by name")
    @GetMapping("/name")
    public ResponseEntity<Object> getDataDisposalByName(@PathVariable Long countryId,@PathVariable Long organizationId, @RequestParam String name) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataDisposalService.getDataDisposalByName(countryId,organizationId,name));

    }


    @ApiOperation("delete data disposal by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteDataDisposal(@PathVariable Long countryId,@PathVariable Long organizationId,@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataDisposalService.deleteDataDisposalById(countryId,organizationId,id));

    }

    @ApiOperation("update DataDisposal by id")
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateDataDisposal(@PathVariable Long countryId,@PathVariable Long organizationId,@PathVariable BigInteger id, @Valid @RequestBody DataDisposal dataDisposal) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataDisposalService.updateDataDisposal(countryId,organizationId,id, dataDisposal));

    }


}
