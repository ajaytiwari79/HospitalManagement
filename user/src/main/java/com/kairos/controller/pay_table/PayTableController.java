package com.kairos.controller.pay_table;

import com.kairos.persistence.model.country.pay_table.PayGradeDTO;
import com.kairos.dto.user.country.pay_table.PayTableUpdateDTO;
import com.kairos.dto.user.country.pay_table.PayTableDTO;
import com.kairos.service.pay_table.PayTableService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_COUNTRY_URL;
import static org.springframework.web.bind.annotation.RequestMethod.*;

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
    public ResponseEntity<Map<String, Object>> getPayTablesByOrganizationLevel(@PathVariable Long countryId,@RequestParam LocalDate startDate,
                                                                               @RequestParam Long organizationLevel) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, payTableService.getPayTablesByOrganizationLevel(countryId, organizationLevel,startDate));
    }

    @RequestMapping(value = "/organization_level/{organizationLevel}/pay_table", method = GET)
    public ResponseEntity<Map<String, Object>> getPayTablesByOrganizationLevel(@PathVariable Long organizationLevel) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, payTableService.getPayTablesByOrganizationLevel(organizationLevel));
    }

    @RequestMapping(value = "/organization_level_pay_group_area", method = GET)
    public ResponseEntity<Map<String, Object>> getOrganizationLevelWisePayGroupAreas(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, payTableService.getOrganizationLevelWisePayGroupAreas(countryId));
    }


    // to create a new pay table in country
    @RequestMapping(value = "/pay_table", method = POST)
    public ResponseEntity<Map<String, Object>> createPayTable(@PathVariable Long countryId, @Validated @RequestBody PayTableDTO payTableDTO) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, payTableService.createPayTable(countryId, payTableDTO));
    }

    // to update a the above created pay table in country
    @RequestMapping(value = "/pay_table/{payTableId}", method = PUT)
    public ResponseEntity<Map<String, Object>> updatePayTable(@PathVariable Long countryId, @PathVariable Long payTableId,
                                                              @Validated @RequestBody PayTableUpdateDTO payTableDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                payTableService.updatePayTable(countryId, payTableId, payTableDTO));
    }


    // remove a pay Table
    @RequestMapping(value = "/pay_table/{payTableId}", method = DELETE)
    public ResponseEntity<Map<String, Object>> removePayTable(@PathVariable Long countryId, @PathVariable Long payTableId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                payTableService.removePayTable(payTableId));
    }


    // add a new pay grade in payTable
    @RequestMapping(value = "/pay_table/{payTableId}/pay_grade", method = POST)
    public ResponseEntity<Map<String, Object>> addPayGradeInPayTable(@PathVariable Long payTableId, @Validated @RequestBody PayGradeDTO payGradeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, payTableService.addPayGradeInPayTable(payTableId, payGradeDTO));
    }

    @RequestMapping(value = "/pay_table/{payTableId}/pay_grade", method = GET)
    public ResponseEntity<Map<String, Object>> getPayGridsByPayTableId(@PathVariable Long payTableId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, payTableService.getPayGradesByPayTableId(payTableId));
    }

    // THIS API is used to delete a particular  payGrade from a payTable
    @RequestMapping(value = "/pay_table/{payTableId}/pay_grade/{payGradeId}", method = DELETE)
    public ResponseEntity<Map<String, Object>> removePayGradeInPayTable(@PathVariable Long payTableId, @PathVariable Long payGradeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, payTableService.removePayGradeInPayTable(payTableId, payGradeId));
    }

    // THIS API is used to update a row of payGrade matrix of a payTable.
    @RequestMapping(value = "/pay_table/{payTableId}/pay_grade/{payGradeId}", method = PUT)
    public ResponseEntity<Map<String, Object>> updatePayGradeInPayTable(@PathVariable Long payTableId,@PathVariable Long payGradeId, @Validated @RequestBody PayGradeDTO payGradeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, payTableService.updatePayGradeInPayTable(payTableId,payGradeId, payGradeDTO));
    }

    @RequestMapping(value = "/pay_table/{payTableId}/publish", method = POST)
    public ResponseEntity<Map<String, Object>> publishPayTable(@PathVariable Long payTableId,@RequestParam LocalDate publishedDate) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, payTableService.publishPayTable(payTableId,publishedDate));
    }

    @PutMapping(value = "/pay_table/{payTableId}/amount")
    public ResponseEntity<Map<String,Object>> updatePayTableAmount(@PathVariable @NotNull Long payTableId,@RequestBody PayTableDTO payTableDTO){
       return ResponseHandler.generateResponse(HttpStatus.OK,true,payTableService.updatePayTableAmountByPercentage(payTableId,payTableDTO));
    }

}
