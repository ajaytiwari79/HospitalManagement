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

import static com.kairos.constants.ApiConstant.*;

@RestController
@RequestMapping(API_ORGANIZATION_URL)
public class DefaultDataInheritController {


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


}
