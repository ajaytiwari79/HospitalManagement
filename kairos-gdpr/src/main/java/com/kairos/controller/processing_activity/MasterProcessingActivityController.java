package com.kairos.controller.processing_activity;


import com.kairos.persistance.model.processing_activity.MasterProcessingActivity;
import com.kairos.service.processing_activity.MasterProcessingActivityService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Object> createMasterProcessingActivity(@RequestBody MasterProcessingActivity processingActivity) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.createMasterProcessingActivity(processingActivity));
    }

    @ApiOperation(value = "get all MasterProcessingActivity")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllAsset() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.getAllmasterProcessingActivity());
    }

    @ApiOperation(value = "update MasterProcessingActivity")
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateMasterProcessingActivity(@PathVariable BigInteger id, @RequestBody MasterProcessingActivity processingActivity) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.updateMasterProcessingActivity(id, processingActivity));
    }

    @ApiOperation(value = "delete MasterProcessingActivity")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteMasterProcessingActivity(@PathVariable BigInteger id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.deleteMasterProcessingActivity(id));
    }

    @ApiOperation(value = "get MasterProcessingActivity by id")
    @GetMapping("/id/{id}")
    public ResponseEntity<Object> getMasterProcessingActivityById(@PathVariable BigInteger id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterProcessingActivityService.getMasterProcessingActivityById(id));
    }


}
