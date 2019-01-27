package com.kairos.controller.data_category_element;


import com.kairos.dto.gdpr.master_data.DataCategoryDTO;
import com.kairos.dto.response.ResponseDTO;
import com.kairos.response.dto.master_data.data_mapping.DataCategoryResponseDTO;
import com.kairos.service.data_subject_management.DataCategoryService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import java.util.List;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstant.UNIT_URL;
import static com.kairos.constants.ApiConstant.COUNTRY_URL;


@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class DataCategoryController {


    @Inject
    private DataCategoryService dataCategoryService;


    @ApiOperation("add data category ")
    @PostMapping(COUNTRY_URL + "/data_category")
    public ResponseEntity<ResponseDTO<DataCategoryDTO>> saveDataCategoryAndDataElement(@PathVariable Long countryId, @Valid @RequestBody DataCategoryDTO dataCategoryDto) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, dataCategoryService.saveDataCategoryAndDataElement(countryId, false, dataCategoryDto));
    }

    @ApiOperation("get data category by id with data Elements ")
    @GetMapping(COUNTRY_URL + "/data_category/{dataCategoryId}")
    public ResponseEntity<ResponseDTO<DataCategoryResponseDTO>> getDataCategoryWithDataElementsById(@PathVariable Long countryId, @PathVariable Long dataCategoryId) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, dataCategoryService.getDataCategoryWithDataElementByCountryIdAndId(countryId, dataCategoryId));

    }

    @ApiOperation("get all data category ")
    @GetMapping(COUNTRY_URL + "/data_category")
    public ResponseEntity<ResponseDTO<List<DataCategoryResponseDTO>>> getAllDataCategoryWithDataElements(@PathVariable Long countryId) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, dataCategoryService.getAllDataCategoryWithDataElementByCountryId(countryId));

    }

    @ApiOperation("delete data category by id ")
    @DeleteMapping(COUNTRY_URL + "/data_category/{dataCategoryId}")
    public ResponseEntity<ResponseDTO<Boolean>> deleteDataCategory(@PathVariable Long countryId, @PathVariable Long dataCategoryId) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, dataCategoryService.deleteDataCategoryById(countryId,false,dataCategoryId));

    }

    @ApiOperation("update data category by id ")
    @PutMapping(COUNTRY_URL + "/data_category/{dataCategoryId}")
    public ResponseEntity<ResponseDTO<DataCategoryDTO>> updateDataCategoryAndDataElement(@PathVariable Long countryId, @PathVariable Long dataCategoryId, @Valid @RequestBody DataCategoryDTO dataCategoryDto) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, dataCategoryService.updateDataCategoryAndDataElement(countryId, false, dataCategoryId, dataCategoryDto));

    }


    @ApiOperation("organization save data category ")
    @PostMapping(UNIT_URL + "/data_category")
    public ResponseEntity<ResponseDTO<DataCategoryDTO>> addDataCategoryAndDataElement(@PathVariable Long unitId, @Valid @RequestBody DataCategoryDTO dataCategoryDto) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, dataCategoryService.saveDataCategoryAndDataElement(unitId, true, dataCategoryDto));
    }

    @ApiOperation("organization update data category by id ")
    @PutMapping(UNIT_URL + "/data_category/{dataCategoryId}")
    public ResponseEntity<ResponseDTO<DataCategoryDTO>> updateOrganizationDataCategoryAndDataElement(@PathVariable Long unitId, @PathVariable Long dataCategoryId, @Valid @RequestBody DataCategoryDTO dataCategoryDto) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, dataCategoryService.updateDataCategoryAndDataElement(unitId, true, dataCategoryId, dataCategoryDto));

    }

    @ApiOperation("organization, get data category by id with data Elements ")
    @GetMapping(UNIT_URL + "/data_category/{dataCategoryId}")
    public ResponseEntity<ResponseDTO<DataCategoryResponseDTO>> getOrganizationDataCategoryWithDataElements(@PathVariable Long unitId, @PathVariable Long dataCategoryId) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, dataCategoryService.getDataCategoryWithDataElementByUnitIdAndId(unitId, dataCategoryId));

    }

    @ApiOperation("organization get all data category ")
    @GetMapping(UNIT_URL + "/data_category")
    public ResponseEntity<ResponseDTO<List<DataCategoryResponseDTO>>> getAllOrganizationDataCategoryWithDataElements(@PathVariable Long unitId) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, dataCategoryService.getAllDataCategoryWithDataElementByUnitId(unitId));

    }

    @ApiOperation("organization ,delete data category by id ")
    @DeleteMapping(UNIT_URL + "/data_category/{dataCategoryId}")
    public ResponseEntity<ResponseDTO<Boolean>> deleteOrganizationDataCategory(@PathVariable Long unitId, @PathVariable Long dataCategoryId) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, dataCategoryService.deleteDataCategoryById(unitId,true,dataCategoryId));

    }


}
