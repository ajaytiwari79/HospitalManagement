package com.kairos.controller.payroll_system;

import com.kairos.dto.activity.payroll_system.PayRollSystemDTO;
import com.kairos.service.payroll_system.PayRollSystemService;
import com.kairos.service.payroll_system.country.CountryPayRollSystemService;
import com.kairos.service.payroll_system.organization.OrganizationPayRollSystemService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_URL;

@RestController
@RequestMapping(API_ORGANIZATION_URL)
public class PayRollSystemController {

    @Inject
    private PayRollSystemService payRollSystemService;
    

    //====================Default Available PayRolls at system level==========================
    @GetMapping("/default_payrolls")
    @ApiOperation("This Api will get all available Payroll Type List")
    public ResponseEntity<Map<String, Object>> getDefaultAvailablePayRolls() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, payRollSystemService.getDefaultAvailablePayRolls());
    }
}
