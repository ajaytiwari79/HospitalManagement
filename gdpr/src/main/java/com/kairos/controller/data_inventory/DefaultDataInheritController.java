package com.kairos.controller.data_inventory;


import com.kairos.dto.gdpr.OrgTypeSubTypeServiceCategoryVO;
import com.kairos.dto.response.ResponseDTO;
import com.kairos.service.common.DefaultDataInheritService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstant.UNIT_URL;

@RestController
@RequestMapping(API_ORGANIZATION_URL)
class DefaultDataInheritController {


    @Inject
    private DefaultDataInheritService defaultDataInheritService;


    /**
     * @param unitId                  - id of the organization which inherit data from from
     * @param organizationMetaDataDTO - contain meta data about child organization, on the basis of meta data (org type ,sub type ,service category and sub service) unit
     *                                inherit data from parent
     * @return
     */
    @ApiOperation(value = "inherit Data from Parent organization on the basis of Org Type, sub Type,Category and Sub Category")
    @PostMapping(UNIT_URL + "/inherit")
    public ResponseEntity<ResponseDTO<Boolean>> inheritMasterDataFromCountry(@PathVariable Long unitId, @Valid @RequestBody OrgTypeSubTypeServiceCategoryVO organizationMetaDataDTO) throws Exception {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, defaultDataInheritService.copyMasterDataFromCountry( unitId,organizationMetaDataDTO));

    }

    @ApiOperation(value = "create asset for organization on the basis of sub Type, Sub service")
    @PostMapping( UNIT_URL + "/create_default_asset/org_sub_service/{orgSubService}")
    public ResponseEntity<ResponseDTO<Boolean>> createDefaultAsset(@RequestParam Long countryId, @PathVariable Long unitId, @PathVariable Long orgSubService, @Valid @RequestBody List<Long> orgSubTypeIds){
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, defaultDataInheritService.copyMasterAssetToUnitAsset(countryId, unitId,orgSubTypeIds,orgSubService));

    }

}
