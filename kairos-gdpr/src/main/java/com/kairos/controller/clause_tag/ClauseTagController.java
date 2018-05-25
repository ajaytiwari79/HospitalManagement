package com.kairos.controller.clause_tag;


import com.kairos.service.clause_tag.ClauseTagService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static  com.kairos.constant.ApiConstant.API_CLAUSE_TAG_URL;
import javax.inject.Inject;
import java.math.BigInteger;
/*
 *
 *  created by bobby 06/5/2018
 * */


@RestController
@RequestMapping(API_CLAUSE_TAG_URL)
@Api(API_CLAUSE_TAG_URL)
@CrossOrigin
public class ClauseTagController {


    @Inject
    private ClauseTagService clauseTagService;

    @ApiOperation("add clauseTag")
    @PostMapping("/add")
    public ResponseEntity<Object> createAssetType(@PathVariable Long countryId,@RequestParam  String clauseTag) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseTagService.createClauseTag(clauseTag));

    }


    @ApiOperation("get clauseTag by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getClauseTagById(@PathVariable Long countryId,@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "request id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseTagService.getClauseTagById(countryId,id));

    }


    @ApiOperation("get all clauseTag ")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllClauseTag() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseTagService.getAllClauseTag());

    }


    @ApiOperation("delete clauseTag  by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteClauseTagById(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "request id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseTagService.deleteClauseTagById(id));

    }

    @ApiOperation("update clauseTag by id")
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateClauseTag(@PathVariable BigInteger id, @RequestParam  String clauseTag) {

        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "request id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseTagService.updateClauseTag(id, clauseTag));

    }




}
