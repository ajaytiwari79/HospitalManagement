package com.kairos.controller.organization;

import com.kairos.persistence.model.user.office_esources_and_metadata.OfficeResources;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;

/**
 * Created by @pankaj on 13/2/17.
 */
@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
public class OfficeResourceController {

   /* @Inject
    OfficeResourceAndMetadataService officeResourceAndMetadataService;

    @ApiOperation(value = "Get a Resources and Resource Types)")
    @RequestMapping(value = "/office_resource_and_resource_types/list", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> listofficeResourceAndResourceTypes(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                officeResourceAndMetadataService.officeResourceToSelect(unitId));
    }

    @ApiOperation(value = "Get a Resources and Resource Types)")
    @RequestMapping(value = "/office_resource_and_resource_types/office_resource/{id}", method = RequestMethod.PUT)
    ResponseEntity<Map<String, Object>> updateResource(@PathVariable long id, @RequestBody OfficeResources officeResources) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                officeResourceAndMetadataService.updateResources(id, officeResources));
    }

    @ApiOperation(value = "Get a Resources and Resource Types)")
    @RequestMapping(value = "/office_resource_and_resource_types/office_resource", method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> saveResource(@PathVariable long unitId, @RequestBody OfficeResources officeResources) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                officeResourceAndMetadataService.saveResources(unitId, officeResources));
    }

    @ApiOperation(value = "Get a Resources and Resource Types)")
    @RequestMapping(value = "/office_resource_and_resource_types/office_resource/{id}", method = RequestMethod.DELETE)
    ResponseEntity<Map<String, Object>> deleteResource(@PathVariable long id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                officeResourceAndMetadataService.deleteResources(id));
    }*/
}
