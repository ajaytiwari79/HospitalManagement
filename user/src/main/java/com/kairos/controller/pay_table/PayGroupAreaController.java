package com.kairos.controller.pay_table;

import com.kairos.dto.user.country.pay_group_area.PayGroupAreaDTO;
import com.kairos.service.pay_group_area.PayGroupAreaService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.constants.ApiConstants.*;

/**
 * Created by prabjot on 20/12/17.
 */
@Api
@RestController
@RequestMapping
public class PayGroupAreaController {

    @Inject
    private PayGroupAreaService payGroupAreaService;

    @PostMapping(API_ORGANIZATION_COUNTRY_URL+"/pay_group_area")
    public ResponseEntity<Map<String, Object>> savePayGroupArea(@PathVariable Long countryId,
                                                                @Validated @RequestBody List<PayGroupAreaDTO> payGroupAreaDTO) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, payGroupAreaService.savePayGroupArea(countryId, payGroupAreaDTO));
    }

    @PutMapping(API_ORGANIZATION_COUNTRY_URL+"/pay_group_area/{payGroupAreaId}")
    public ResponseEntity<Map<String, Object>> updatePayGroupArea(@PathVariable Long payGroupAreaId, @RequestBody PayGroupAreaDTO payGroupAreaDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, payGroupAreaService.updatePayGroupArea(payGroupAreaId, payGroupAreaDTO));
    }

    @DeleteMapping(API_ORGANIZATION_COUNTRY_URL+"/pay_group_area/{payGroupAreaId}")
    public ResponseEntity<Map<String, Object>> deletePayGroup(@PathVariable Long payGroupAreaId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, payGroupAreaService.deletePayGroupArea(payGroupAreaId));
    }

    @GetMapping(API_ORGANIZATION_COUNTRY_URL+"/pay_group_area")
    public ResponseEntity<Map<String, Object>> getPayGroup(@PathVariable Long countryId, @RequestParam Long organizationLevel)
     {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, payGroupAreaService.getPayGroupArea(countryId, organizationLevel));
    }

    @DeleteMapping(API_ORGANIZATION_COUNTRY_URL+"/remove_pay_group_area/{payGroupAreaId}")
    public ResponseEntity<Map<String, Object>> deletePayGroupFromMunicipality(@PathVariable Long payGroupAreaId,
                                                                              @RequestParam Long municipalityId,
                                                                              @RequestParam Long relationshipId)
    {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, payGroupAreaService.deletePayGroupFromMunicipality(payGroupAreaId, municipalityId,relationshipId));
    }
    @GetMapping(API_ORGANIZATION_COUNTRY_URL+"/municipality_organization_level")
    public ResponseEntity<Map<String, Object>> getMunicipalityAndOrganizationLevel(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, payGroupAreaService.getMunicipalityAndOrganizationLevel(countryId));
    }

    @GetMapping(API_V1+UNIT_URL+"/pay_group_area")
    public ResponseEntity<Map<String, Object>> getPayGroupAreaByLevel(@RequestParam Long organizationLevel)
    {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, payGroupAreaService.getPayGroupAreaByLevel( organizationLevel));
    }


}
