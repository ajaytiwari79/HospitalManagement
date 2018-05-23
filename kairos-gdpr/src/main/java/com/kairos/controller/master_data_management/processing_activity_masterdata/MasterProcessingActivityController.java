package com.kairos.controller.master_data_management.processing_activity_masterdata;



import com.kairos.dto.MasterProcessingActivityDto;
import com.kairos.service.master_data_management.processing_activity_masterdata.MasterProcessingActivityService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import static com.kairos.constant.ApiConstant.API_MASTER_PROCESSING_ACTIVITY;
import javax.inject.Inject;
import java.math.BigInteger;

@RestController
@RequestMapping(API_MASTER_PROCESSING_ACTIVITY)
@Api(API_MASTER_PROCESSING_ACTIVITY)
@CrossOrigin
public class MasterProcessingActivityController {



    @Inject
    private MasterProcessingActivityService masterProcessingActivityService;


    @ApiOperation(value = "add MasterProcessingActivity asset")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<Object> createMasterProcessingActivity(@Validated  @RequestBody MasterProcessingActivityDto processingActivityDto) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.createMasterProcessingActivity(processingActivityDto));
    }

    @ApiOperation(value = "get all MasterProcessingActivity")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllAsset() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.getAllmasterProcessingActivity());
    }

    @ApiOperation(value = "update MasterProcessingActivity")
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateMasterProcessingActivity(@PathVariable BigInteger id, @RequestBody MasterProcessingActivityDto processingActivityDto) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.updateMasterProcessingActivity(id,processingActivityDto));
    }

    @ApiOperation(value = "delete MasterProcessingActivity")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteMasterProcessingActivity(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.deleteMasterProcessingActivity(id));
    }

    @ApiOperation(value = "get MasterProcessingActivity by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getMasterProcessingActivity(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.getMasterProcessingActivityById(id));
    }


}
