package com.kairos.controller.master_data.asset_management;

import com.kairos.persistance.model.master_data.default_asset_setting.DataDisposal;
import com.kairos.service.master_data.asset_management.DataDisposalService;
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

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstant.UNIT_URL;

/*
 *
 *  created by bobby 16/5/2018
 * */


@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class DataDisposalController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataDisposalController.class);

    @Inject
    private DataDisposalService dataDisposalService;


    @ApiOperation("add DataDisposal")
    @PostMapping("/data_disposal/add")
    public ResponseEntity<Object> createDataDisposal(@PathVariable Long countryId, @Valid @RequestBody ValidateListOfRequestBody<DataDisposal> dataDisposals) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataDisposalService.createDataDisposal(countryId, dataDisposals.getRequestBody()));

    }


    @ApiOperation("get DataDisposal by id")
    @GetMapping("/data_disposal/{id}")
    public ResponseEntity<Object> getDataDisposal(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataDisposalService.getDataDisposalById(countryId, id));

    }


    @ApiOperation("get all DataDisposal ")
    @GetMapping("/data_disposal/all")
    public ResponseEntity<Object> getAllDataDisposal(@PathVariable Long countryId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataDisposalService.getAllDataDisposal(countryId));

    }

    @ApiOperation("get DataDisposal by name")
    @GetMapping("/data_disposal/name")
    public ResponseEntity<Object> getDataDisposalByName(@PathVariable Long countryId, @RequestParam String name) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataDisposalService.getDataDisposalByName(countryId, name));

    }


    @ApiOperation("delete data disposal by id")
    @DeleteMapping("/data_disposal/delete/{id}")
    public ResponseEntity<Object> deleteDataDisposal(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataDisposalService.deleteDataDisposalById(countryId, id));

    }

    @ApiOperation("update DataDisposal by id")
    @PutMapping("/data_disposal/update/{id}")
    public ResponseEntity<Object> updateDataDisposal(@PathVariable Long countryId, @PathVariable BigInteger id, @Valid @RequestBody DataDisposal dataDisposal) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataDisposalService.updateDataDisposal(countryId, id, dataDisposal));

    }

}
