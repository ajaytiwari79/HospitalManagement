package com.kairos.controller.clause;


import com.kairos.dto.gdpr.master_data.ClauseDTO;
import com.kairos.dto.gdpr.master_data.MasterClauseDTO;
import com.kairos.dto.response.ResponseDTO;
import com.kairos.response.dto.clause.ClauseBasicResponseDTO;
import com.kairos.response.dto.clause.ClauseResponseDTO;
import com.kairos.response.dto.clause.UnitLevelClauseResponseDTO;
import com.kairos.service.clause.ClauseService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

import static com.kairos.constants.ApiConstant.*;


/*
 *
 *  created by bobby 04/5/2018
 * */


@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class ClauseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClauseController.class);

    @Inject
    private ClauseService clauseService;


    @ApiOperation("save  master clause")
    @PostMapping(COUNTRY_URL + "/clause")
    public ResponseEntity<ResponseDTO<MasterClauseDTO>> saveMasterClause(@PathVariable Long countryId, @Validated @RequestBody MasterClauseDTO clauseDto) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, clauseService.createClause(countryId, false, clauseDto));

    }

    @ApiOperation("get master clause by id")
    @GetMapping(COUNTRY_URL + "/clause/{id}")
    public ResponseEntity<Object> getClause(@PathVariable Long countryId, @PathVariable BigInteger id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseService.getClauseById(countryId, id));
    }


    @ApiOperation("delete master  clause by id")
    @DeleteMapping(COUNTRY_URL + "/clause/{clauseId}")
    public ResponseEntity<ResponseDTO<Boolean>> deleteMasterClause(@PathVariable Long countryId, @PathVariable BigInteger clauseId) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, clauseService.deleteClauseById(countryId, false, clauseId));
    }

    @ApiOperation("update master clause ")
    @PutMapping(COUNTRY_URL + "/clause/{clauseId}")
    public ResponseEntity<ResponseDTO<MasterClauseDTO>> updateMasterClause(@PathVariable Long countryId, @PathVariable BigInteger clauseId, @Validated @RequestBody MasterClauseDTO clauseDto) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, clauseService.updateClause(countryId, false, clauseId, clauseDto));

    }


    @ApiOperation("get all master clause")
    @GetMapping(COUNTRY_URL + "/clause")
    public ResponseEntity<ResponseDTO<List<ClauseResponseDTO>>> getAllMasterClause(@PathVariable Long countryId) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, clauseService.getAllClauseByCountryId(countryId));
    }


    @ApiOperation("save clause at unit level")
    @PostMapping(UNIT_URL + "/clause")
    public ResponseEntity<ResponseDTO<ClauseDTO>> saveClause(@PathVariable Long unitId, @Validated @RequestBody ClauseDTO clauseDto) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, clauseService.createClause(unitId, true, clauseDto));
    }


    @ApiOperation("update clause at  unit level")
    @PutMapping(UNIT_URL + "/clause/{clauseId}")
    public ResponseEntity<ResponseDTO<ClauseDTO>> updateClause(@PathVariable Long unitId, @PathVariable BigInteger clauseId, @Validated @RequestBody ClauseDTO clauseDto) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, clauseService.updateClause(unitId, true, clauseId, clauseDto));
    }

    @ApiOperation("get all clause of unit")
    @GetMapping(UNIT_URL + "/clause")
    public ResponseEntity<ResponseDTO<List<UnitLevelClauseResponseDTO>>> getAllClause(@PathVariable Long unitId) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, clauseService.getAllClauseByUnitId(unitId));
    }

    @ApiOperation("delete  clause by id")
    @DeleteMapping(UNIT_URL + "/clause/{clauseId}")
    public ResponseEntity<ResponseDTO<Boolean>> deleteClause(@PathVariable Long unitId, @PathVariable BigInteger clauseId) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, clauseService.deleteClauseById(unitId, true, clauseId));
    }

    @ApiOperation("Get Clause MetaData")
    @GetMapping(COUNTRY_URL + "/clause/meta_data")
    public ResponseEntity<Object> getClauseMetaDataByCountryId(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseService.getClauseMetaDataByCountryId(countryId));

    }

    @ApiOperation("get  clause metadata of unit")
    @GetMapping(UNIT_URL + "/clause/meta_data")
    public ResponseEntity<Object> getClauseMetadataByOrganizationId(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseService.getClauseMetadataByOrganizationId(unitId));
    }




}