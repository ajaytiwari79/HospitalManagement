package com.kairos.controller.client_exception;

import com.kairos.persistence.model.client_exception.ClientExceptionDTO;
import com.kairos.persistence.model.client_exception.ClientExceptionType;
import com.kairos.service.client_exception.ClientExceptionService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;

/**
 * Created by oodles on 7/2/17.
 */
//TODO configure oauth 2 and uncomment @PreAuthorize
@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@Api(value = API_ORGANIZATION_UNIT_URL)
public class ClientExceptionController {

    @Inject
    private ClientExceptionService clientExceptionService;

    // Get Client Exceptions
    @RequestMapping(method = RequestMethod.GET,value = "/client_exception/{clientExceptionId}")
    @ApiOperation("get task exception")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getClientExceptionById( @PathVariable BigInteger clientExceptionId) throws ParseException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                clientExceptionService.getClientExceptionById(clientExceptionId));
    }


    @RequestMapping(method = RequestMethod.POST,value = "/client/{clientId}/client_exception")
    @ApiOperation("Create Client Exception")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createClientException(@RequestBody @Validated ClientExceptionDTO reqData, @PathVariable long unitId,@PathVariable long clientId) throws ParseException {
        return ResponseHandler.generateResponse(HttpStatus.OK, false, clientExceptionService.createClientException(reqData ,unitId,clientId));
    }


    @RequestMapping(method = RequestMethod.PUT,value = "/client_exception/{clientExceptionId}")
    @ApiOperation("update client exception")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateClientExceptionById( @PathVariable BigInteger clientExceptionId, @PathVariable long unitId, @RequestBody @Validated ClientExceptionDTO exceptionDTO) throws ParseException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                clientExceptionService.updateClientExceptionById(clientExceptionId, exceptionDTO,unitId));
    }

    @RequestMapping(method = RequestMethod.DELETE,value = "/client_exception/{clientExceptionId}")
    @ApiOperation("Delete client exception")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteClientException( @PathVariable BigInteger clientExceptionId,@PathVariable long unitId) throws ParseException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                clientExceptionService.deleteClientException(clientExceptionId,unitId));
    }


    @RequestMapping(method = RequestMethod.POST, value = "/client_exception/bulkDelete")
    @ApiOperation("Bulk Delete task exception")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> bulkDeleteClientException(@PathVariable long unitId,@Validated @RequestBody ClientExceptionDTO exceptionDTO) throws ParseException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                clientExceptionService.bulkDeleteClientException(exceptionDTO,unitId));
    }

    @RequestMapping(method = RequestMethod.POST,value = "/client_exception/dates")
    @ApiOperation("Get task exception from dates")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getClientExceptionBetweenDates(@PathVariable long unitId,@RequestBody ClientExceptionDTO exceptionDTO) throws ParseException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                clientExceptionService.getClientExceptionOnDates(exceptionDTO,unitId));
    }

    // ClientExceptionType
    @RequestMapping(method = RequestMethod.GET,value = "/client_exception_type")
    @ApiOperation("Get task exception type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTaskExceptionType() throws ParseException {
        List<ClientExceptionType> response = clientExceptionService.getTaskExceptionType();
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, response);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }
    @RequestMapping(method = RequestMethod.POST ,value = "/client_exception_type")
    @ApiOperation("Create task exception type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createTaskExceptionType(@RequestBody ClientExceptionType reqData) throws ParseException {
        ClientExceptionType response = clientExceptionService.createTaskExceptionType(reqData);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, response);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }
    @RequestMapping(method = RequestMethod.PUT ,value = "/client_exception_type")
    @ApiOperation("Update task exception type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateTaskExceptionType(@RequestBody ClientExceptionType reqData) throws ParseException {
        ClientExceptionType response = clientExceptionService.updateTaskExceptionType(reqData);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, response);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }


    @RequestMapping(method = RequestMethod.DELETE,value = "/client_exception_type")
    @ApiOperation("Delete task exception type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteTaskExceptionType(@PathVariable String taskExceptionTypeId) throws ParseException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                clientExceptionService.deleteTaskExceptionType(taskExceptionTypeId));
    }

    /**
     * this method will return clients only which are having exceptions for current week
     * @param unitId
     * @return
     */
    @RequestMapping(value = "/client_exception/clients",method = RequestMethod.GET)
    public ResponseEntity<Map<String,Object>> getClientsByIds(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK,true,clientExceptionService.getExceptionClients(unitId));
    }

}