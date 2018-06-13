package com.kairos.controller.client;

import com.google.common.collect.Lists;
import com.kairos.persistence.model.user.client.VRPClient;
import com.kairos.service.client.VRPClientService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;
import static com.kairos.constants.ApiConstants.UNIT_URL;

/**
 * @author pradeep
 * @date - 11/6/18
 */
@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
public class VRPClientController {

    @Inject
    private VRPClientService vrpClientService;

    @ApiOperation(value = "import Unit Client Excel File")
    @PostMapping(value = "/importClient")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> importVrpClient(@PathVariable Long unitId, @RequestParam("file") MultipartFile multipartFile) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,
                vrpClientService.importClient(unitId,multipartFile));
    }

    @ApiOperation(value = "get VRPClient by Organization")
    @GetMapping(value = "/getClient")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getClient(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,
                vrpClientService.getAllClient(unitId));
    }



}
