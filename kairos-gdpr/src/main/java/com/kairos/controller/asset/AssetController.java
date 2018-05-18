package com.kairos.controller.asset;



import com.kairos.dto.MasterAssetDto;
import com.kairos.service.asset.MasterAssetService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.inject.Inject;
import java.math.BigInteger;
import static com.kairos.constant.ApiConstant.API_MASTER_ASSET_URL;

@RestController
@RequestMapping(API_MASTER_ASSET_URL)
@Api(API_MASTER_ASSET_URL)
@CrossOrigin
public class AssetController {


    @Inject
    private MasterAssetService masterAssetService;


    @ApiOperation(value = "add master asset")
    @RequestMapping(value = "/add_asset", method = RequestMethod.POST)
    public ResponseEntity<Object> addAsset(@Validated  @RequestBody MasterAssetDto masterAssetDto) {
          return ResponseHandler.generateResponse(HttpStatus.OK, true, masterAssetService.addMasterAsset(masterAssetDto));
    }

    @ApiOperation(value = "get all master asset")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllMasterAsset() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterAssetService.getAllMasterAsset());
    }

    @ApiOperation(value = "update master asset by id")
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateAsset(@PathVariable BigInteger id, @RequestBody MasterAssetDto asset) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterAssetService.updateMasterAsset(id, asset));
    }

    @ApiOperation(value = "delete master asset")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteAsset(@PathVariable BigInteger id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterAssetService.deleteMasterAsset(id));
    }

    @ApiOperation(value = "get master asset by id")
    @GetMapping("/id/{id}")
    public ResponseEntity<Object> getGlobalAssetById(@PathVariable BigInteger id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, masterAssetService.getMasterAssetById(id));
    }


}
