package com.kairos.controller.payroll_system;

import com.kairos.service.payroll_system.PayRollSystemService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_V1;

@RestController
@RequestMapping(API_V1)
public class PayRollSystemController {

    @Inject
    private PayRollSystemService payRollSystemService;
    

    @GetMapping("/default_payrolls")
    @ApiOperation("This Api will get all available Payroll Type List")
    public ResponseEntity<Map<String, Object>> getDefaultAvailablePayRolls() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, payRollSystemService.getDefaultAvailablePayRolls());
    }
}
