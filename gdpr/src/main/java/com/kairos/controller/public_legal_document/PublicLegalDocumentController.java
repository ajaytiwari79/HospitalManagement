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
import javax.validation.Valid;

import static com.kairos.constants.ApiConstant.*;

/**
 * Created By G.P.Ranjan on 26/6/19
 **/
@RestController
@RequestMapping(API_ORGANIZATION_COUNTRY_URL)
@Api(API_ORGANIZATION_COUNTRY_URL)
public class PublicLegalDocumentController {
    @Inject
    private PublicLegalDocumentService publicLegalDocumentService;

    @ApiOperation("Save Public Legal Document")
    @PostMapping("/public_legal_document")
    public ResponseEntity<Object> createPublicLegalDocument(@RequestBody @Valid PublicLegalDocumentDTO publicLegalDocumentDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, publicLegalDocumentService.createPublicLegalDocument(publicLegalDocumentDTO));
    }

    @ApiOperation("Upload Public Legal Document Logo")
    @PostMapping("/public_legal_document/logo")
    public ResponseEntity<Object> uploadPublicLegalDocumentLogo(@RequestParam("file") MultipartFile file) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, publicLegalDocumentService.uploadPublicLegalDocumentLogo(file));
    }
    @ApiOperation("Update Public Legal Document By Id")
    @PutMapping("/public_legal_document/{publicLegalDocumentId}")
    public ResponseEntity<Object> updateMasterAgreementTemplate(@PathVariable Long publicLegalDocumentId,@RequestBody @Valid PublicLegalDocumentDTO publicLegalDocumentDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, publicLegalDocumentService.updatePublicLegalDocument(publicLegalDocumentId,publicLegalDocumentDTO));

    }

    @ApiOperation("Delete Public Legal Document By Id")
    @DeleteMapping("/public_legal_document/{publicLegalDocumentId}")
    public ResponseEntity<Object> removePublicLegalDocument(@PathVariable Long publicLegalDocumentId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, publicLegalDocumentService.removePublicLegalDocument(publicLegalDocumentId));
    }

    @ApiOperation("Get All Public Legal Document")
    @GetMapping("/public_legal_document/all")
    public ResponseEntity<Object> getAllPublicLegalDocument() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, publicLegalDocumentService.getAllPublicLegalDocument());
    }
}
