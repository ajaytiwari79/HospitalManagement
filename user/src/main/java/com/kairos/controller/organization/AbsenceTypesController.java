package com.kairos.controller.organization;

import com.kairos.persistence.model.organization.AbsenceTypes;
import com.kairos.service.organization.AbsenceTypesService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_V1;


/**
 * Created by oodles on 16/12/16.
 */
@RestController
@RequestMapping(API_V1 + "/organization/absenceTypes")
public class AbsenceTypesController {

    @Inject
    AbsenceTypesService absenceTypesService;

    // Organization
    @ApiOperation(value = "Get All Absence Types")
    @RequestMapping(value = "/all",method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getAllAbsenceTypes(){
        return  ResponseHandler.generateResponse(HttpStatus.OK,true,
                absenceTypesService.getAllAbsenceTypes());

    }

    // Organization
    @ApiOperation(value = "Save Absence Types")
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> createAbsenceTypes(@RequestBody Map<String, Object> data){
        String ATVTID = (String) data.get("ATVTID");
        String organisationId = (String) data.get("organisationId");
        String name = (String) data.get("name");
       AbsenceTypes absenceTypes = absenceTypesService.createAbsenceTypes(Long.parseLong(ATVTID), name, Long.parseLong(organisationId));
       // return RequestHandler.generateResponse(HttpStatus.OK,true, "success");
        return  ResponseHandler.generateResponse(HttpStatus.OK,true, absenceTypes.getATVTID());

    }

    /**
     *  @auther anil maurya
     *  this endpoint is called from task micro service
     * @param name
     * @return
     */
    @ApiOperation(value = "Get All Absence Types by name ")
    @RequestMapping(value = "/{name}",method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getAllAbsenceTypeByName(@PathVariable String name){
        return  ResponseHandler.generateResponse(HttpStatus.OK,true,
                absenceTypesService.getAbsenceTypeMapByName(name));

    }
}
