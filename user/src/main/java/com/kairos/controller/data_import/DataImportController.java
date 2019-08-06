package com.kairos.controller.data_import;

import com.kairos.service.data_import.DataImportService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_V1;
import static com.kairos.constants.ApiConstants.UNIT_URL;

/**
 * Created by prabjot on 7/2/17.
 */
@RestController
@RequestMapping(API_V1 +UNIT_URL)
public class DataImportController {

    @Inject
    private DataImportService dataImportService;

    @ApiOperation("Import data by excel sheet")
    @RequestMapping(value = "/data_import", method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> importDataFromExcel(@RequestParam("file") MultipartFile multipartFile){

       // dataImportService.importDataFromExcel(multipartFile);
        return ResponseHandler.generateResponse(HttpStatus.CREATED,true,true);
    }

    @ApiOperation("update preferred time")
    @RequestMapping(value = "/data_import/time",method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> updateTimeOfVisitationDemand(@RequestParam("file") MultipartFile multipartFile){

      //  dataImportService.updatePreferredTimeOfDemand(multipartFile);
        return ResponseHandler.generateResponse(HttpStatus.CREATED,true,true);
    }
}
