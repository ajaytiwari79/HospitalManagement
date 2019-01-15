package com.kairos.controller.master_data.processing_activity_masterdata;


import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.dto.gdpr.metadata.AccessorPartyDTO;
import com.kairos.service.master_data.processing_activity_masterdata.AccessorPartyService;
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
 *  created by bobby 17/5/2018
 * */


@RestController
@RequestMapping(API_ORGANIZATION_COUNTRY_URL)
@Api(API_ORGANIZATION_COUNTRY_URL)
public class AccessorPartyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessorPartyController.class);

    @Inject
    private AccessorPartyService accessorPartyService;


    @ApiOperation("add AccessorParty")
    @PostMapping("/accessor_party")
    public ResponseEntity<Object> createAccessorParty(@PathVariable Long countryId, @Valid @RequestBody ValidateRequestBodyList<AccessorPartyDTO> accessorParties) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.createAccessorParty(countryId, accessorParties.getRequestBody(),false));

    }


    @ApiOperation("get AccessorParty by id")
    @GetMapping("/accessor_party/{accessorPartyId}")
    public ResponseEntity<Object> getAccessorParty(@PathVariable Long countryId, @PathVariable Long accessorPartyId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.getAccessorParty(countryId, accessorPartyId));
    }


    @ApiOperation("get all AccessorParty ")
    @GetMapping("/accessor_party")
    public ResponseEntity<Object> getAllAccessorParty(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.getAllAccessorParty(countryId));
    }

    @ApiOperation("delete AccessorParty  by id")
    @DeleteMapping("/accessor_party/{accessorPartyId}")
    public ResponseEntity<Object> deleteAccessorParty(@PathVariable Long countryId, @PathVariable Long accessorPartyId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.deleteAccessorParty(countryId, accessorPartyId));

    }

    @ApiOperation("update AccessorParty by id")
    @PutMapping("/accessor_party/{accessorPartyId}")
    public ResponseEntity<Object> updateAccessorParty(@PathVariable Long countryId, @PathVariable Long accessorPartyId, @Valid @RequestBody AccessorPartyDTO accessorParty) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.updateAccessorParty(countryId, accessorPartyId, accessorParty));
    }

    @ApiOperation("update Suggested status of Accessor Party")
    @PutMapping("/accessor_party")
    public ResponseEntity<Object> updateSuggestedStatusOfAccessorParties(@PathVariable Long countryId, @RequestBody Set<Long> accessorPartyIds, @RequestParam(required = true) SuggestedDataStatus suggestedDataStatus) {
        if (CollectionUtils.isEmpty(accessorPartyIds)) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Accessor Party is Not Selected");
        } else if (!Optional.ofNullable(suggestedDataStatus).isPresent()) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Suggested Status in Empty");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessorPartyService.updateSuggestedStatusOfAccessorPartyList(countryId, accessorPartyIds, suggestedDataStatus));
    }


}
