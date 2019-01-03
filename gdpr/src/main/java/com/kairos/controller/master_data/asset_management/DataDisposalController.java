package com.kairos.controller.master_data.asset_management;

import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.dto.gdpr.metadata.DataDisposalDTO;
import com.kairos.service.master_data.asset_management.DataDisposalService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.ValidateRequestBodyList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.Optional;
import java.util.Set;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_COUNTRY_URL;

/*
 *
 *  created by bobby 16/5/2018
 * */


@RestController
@RequestMapping(API_ORGANIZATION_COUNTRY_URL)
@Api(API_ORGANIZATION_COUNTRY_URL)
public class DataDisposalController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataDisposalController.class);

    @Inject
    private DataDisposalService dataDisposalService;


    @ApiOperation("add DataDisposal")
    @PostMapping("/data_disposal")
    public ResponseEntity<Object> createDataDisposal(@PathVariable Long countryId, @Valid @RequestBody ValidateRequestBodyList<DataDisposalDTO> dataDisposalDTOs) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataDisposalService.createDataDisposal(countryId, dataDisposalDTOs.getRequestBody()));

    }


    @ApiOperation("get DataDisposal by id")
    @GetMapping("/data_disposal/{dataDisposalId}")
    public ResponseEntity<Object> getDataDisposal(@PathVariable Long countryId, @PathVariable Integer dataDisposalId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataDisposalService.getDataDisposalById(countryId, dataDisposalId));

    }


    @ApiOperation("get all DataDisposal ")
    @GetMapping("/data_disposal")
    public ResponseEntity<Object> getAllDataDisposal(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataDisposalService.getAllDataDisposal(countryId));

    }


    @ApiOperation("delete data disposal by id")
    @DeleteMapping("/data_disposal/{dataDisposalId}")
    public ResponseEntity<Object> deleteDataDisposal(@PathVariable Long countryId, @PathVariable Integer dataDisposalId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataDisposalService.deleteDataDisposalById(countryId, dataDisposalId));

    }

    @ApiOperation("update DataDisposal by id")
    @PutMapping("/data_disposal/{dataDisposalId}")
    public ResponseEntity<Object> updateDataDisposal(@PathVariable Long countryId, @PathVariable Integer dataDisposalId, @Valid @RequestBody DataDisposalDTO dataDisposalDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataDisposalService.updateDataDisposal(countryId, dataDisposalId, dataDisposalDTO));
    }

    @ApiOperation("update Suggested status of Data Disposal")
    @PutMapping("/data_disposal")
    public ResponseEntity<Object> updateSuggestedStatusOfDataDisposals(@PathVariable Long countryId, @RequestBody Set<BigInteger> dataDisposalIds, @RequestParam(required = true) SuggestedDataStatus suggestedDataStatus) {
        if (CollectionUtils.isEmpty(dataDisposalIds)) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Data Disposal is Not Selected");
        } else if (!Optional.ofNullable(suggestedDataStatus).isPresent()) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Suggested Status in Empty");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataDisposalService.updateSuggestedStatusOfDataDisposals(countryId, dataDisposalIds, suggestedDataStatus));
    }


}
