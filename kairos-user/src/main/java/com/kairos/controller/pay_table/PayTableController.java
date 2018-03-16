package com.kairos.controller.pay_table;

import com.kairos.persistence.model.user.pay_table.PayLevelDTO;
import com.kairos.response.dto.web.pay_table.PayGradeDTO;
import com.kairos.response.dto.web.pay_table.PayGradeMatrixDTO;
import com.kairos.response.dto.web.pay_table.PayTableDTO;
import com.kairos.service.pay_table.PayTableService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_COUNTRY_URL;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * Created by prabjot on 26/12/17.
 *
 * @Modified by vipul for KP-2635
 */
@Api(value = API_ORGANIZATION_COUNTRY_URL)
@RestController
@RequestMapping(API_ORGANIZATION_COUNTRY_URL)
public class PayTableController {

    @Inject
    private PayTableService payTableService;

    @RequestMapping(value = "/pay_table_data", method = GET)
    public ResponseEntity<Map<String, Object>> getPayTablesByOrganizationLevel(@PathVariable Long countryId,
                                                                               @RequestParam Long organizationLevel) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, payTableService.getPayTablesByOrganizationLevel(countryId, organizationLevel));
    }

    @RequestMapping(value = "/organization_level_pay_table", method = GET)
    public ResponseEntity<Map<String, Object>> getOrganizationLevelWisePayTables(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, payTableService.getOrganizationLevelWisePayTables(countryId));
    }

    @RequestMapping(value = "/pay_table", method = POST)
    public ResponseEntity<Map<String, Object>> createPayLevel(@PathVariable Long countryId, @Validated @RequestBody PayTableDTO payTableDTO) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, payTableService.createPayLevel(countryId, payTableDTO));
    }

    @RequestMapping(value = "/pay_table/{payTableId}/pay_grade", method = PUT)
    public ResponseEntity<Map<String, Object>> addPayGradeInPayTable(@PathVariable Long payTableId, @Validated @RequestBody PayGradeDTO payGradeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, payTableService.addPayGradeInPayTable(payTableId, payGradeDTO));
    }


    @RequestMapping(value = "/pay_table/{payTableId}", method = PUT)
    public ResponseEntity<Map<String, Object>> updatePayLevel(@PathVariable Long payTableId, @Validated @RequestBody PayTableDTO payTableDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, payTableService.updatePayLevel(payTableId, payTableDTO));
    }


}
