package com.kairos.controller.country.default_data;

import com.kairos.service.country.default_data.UnitTypeService;
import com.kairos.user.country.system_setting.UnitTypeDTO;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_UNIT_TYPE;

//  Created By Vipul   On 9/8/18
@RestController
@RequestMapping(API_UNIT_TYPE)
@Api(API_UNIT_TYPE)
public class UnitTypeController {
    @Inject
    private UnitTypeService unitTypeService;

    @PostMapping
    @ApiOperation("This method will create unit type in country")
    public ResponseEntity<Map<String, Object>> addUnitTypeInCountry(@PathVariable Long countryId, @RequestBody @Valid UnitTypeDTO unitTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, unitTypeService.addUnitTypeInCountry(countryId, unitTypeDTO));
    }

    @GetMapping
    @ApiOperation("this  is used to fetch all unit type of country")
    public ResponseEntity<Map<String, Object>> getAllUnitTypeOfCountry(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitTypeService.getAllUnitTypeOfCountry(countryId));
    }

    /**
     * @param unit type id
     */
    @PutMapping(value = "/{unitTypeId}")
    @ApiOperation("This method will update a particular unit type")
    public ResponseEntity<Map<String, Object>> updateUnitTypeOfCountry(@PathVariable Long countryId, @RequestBody @Valid UnitTypeDTO unitTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitTypeService.updateUnitTypeOfCountry(countryId, unitTypeDTO));
    }
}
