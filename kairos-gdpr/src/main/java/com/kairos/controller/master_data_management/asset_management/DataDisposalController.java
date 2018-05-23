package com.kairos.controller.master_data_management.asset_management;

import com.kairos.persistance.model.master_data_management.asset_management.DataDisposal;
import com.kairos.service.master_data_management.asset_management.DataDisposalService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import static com.kairos.constant.ApiConstant.API_DATA_DISPOSAL_URL;


@RestController
@RequestMapping(API_DATA_DISPOSAL_URL)
@Api(API_DATA_DISPOSAL_URL)
@CrossOrigin
public class DataDisposalController {


    @Inject
    private DataDisposalService dataDisposalService;


    @ApiOperation("add DataDisposal")
    @PostMapping("/add")
    public ResponseEntity<Object> createDataDisposal(@RequestBody List<DataDisposal> dataDisposals) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataDisposalService.createDataDisposal(dataDisposals));

    }


    @ApiOperation("get DataDisposal by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getDataDisposal(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataDisposalService.getDataDisposalById(id));

    }


    @ApiOperation("get all DataDisposal ")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllDataDisposal() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataDisposalService.getAllDataDisposal());

    }

    @ApiOperation("get DataDisposal by name")
    @GetMapping("")
    public ResponseEntity<Object> getDataDisposalByName(@RequestParam String name) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataDisposalService.getDataDisposalByName(name));

    }




    @ApiOperation("delete HostingProvider  by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteDataDisposal(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataDisposalService.deleteDataDisposalById(id));

    }

    @ApiOperation("update DataDisposal by id")
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateDataDisposal(@PathVariable BigInteger id, @RequestBody DataDisposal dataDisposal) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, dataDisposalService.updateDataDisposal(id, dataDisposal));

    }


}
