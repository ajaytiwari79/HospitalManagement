package com.kairos.controller.public_legal_document;

import com.kairos.response.dto.public_legal_document.PublicLegalDocumentDTO;
import com.kairos.service.public_legal_document.PublicLegalDocumentService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;

import static com.kairos.constants.ApiConstant.*;

/**
 * Created By G.P.Ranjan on 26/6/19
 **/
@RestController
@RequestMapping(API_V1)
@Api(API_V1)
public class PublicLegalDocumentController {
    @Inject
    private PublicLegalDocumentService publicLegalDocumentService;

    @ApiOperation("Save Public Legal Document")
    @PostMapping(COUNTRY_URL + "/public_legal_document")
    public ResponseEntity<Object> createPublicLegalDocument(@RequestParam(value = "file", required = false) MultipartFile file,@RequestBody @Validated PublicLegalDocumentDTO publicLegalDocumentDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, publicLegalDocumentService.createPublicLegalDocument(publicLegalDocumentDTO));
    }

    @ApiOperation("Upload Public Legal Document Logo")
    @PostMapping(COUNTRY_URL + "/public_legal_document/logo")
    public ResponseEntity<Object> uploadPublicLegalDocumentLogo(@RequestParam("logoFile") MultipartFile logoFile) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, publicLegalDocumentService.uploadPublicLegalDocumentLogo(logoFile));
    }
    @ApiOperation("Update Public Legal Document By Id")
    @PutMapping(COUNTRY_URL + "/public_legal_document/{publicLegalDocumentId}")
    public ResponseEntity<Object> updateMasterAgreementTemplate(@PathVariable Long publicLegalDocumentId,@RequestBody PublicLegalDocumentDTO publicLegalDocumentDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, publicLegalDocumentService.updatePublicLegalDocument(publicLegalDocumentId,publicLegalDocumentDTO));

    }

    @ApiOperation("Delete Public Legal Document By Id")
    @DeleteMapping(COUNTRY_URL + "/public_legal_document/{publicLegalDocumentId}")
    public ResponseEntity<Object> removePublicLegalDocument(@PathVariable Long publicLegalDocumentId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, publicLegalDocumentService.removePublicLegalDocument(publicLegalDocumentId));
    }

    @ApiOperation("Get All Public Legal Document")
    @GetMapping(COUNTRY_URL + "/public_legal_document/all")
    public ResponseEntity<Object> getAllPublicLegalDocument() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, publicLegalDocumentService.getAllPublicLegalDocument());
    }
}
