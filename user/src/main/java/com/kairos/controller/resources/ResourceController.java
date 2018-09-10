package com.kairos.controller.resources;

/**
 * Created by oodles on 17/10/16.
 */

import com.kairos.persistence.model.user.resources.Resource;
import com.kairos.service.resources.ResourceService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_RESOURCE_URL;


/**
 * ResourceController
 * 1.Calls Resource Service
 * 2. Call for CRUD operation on Resource using ResourceService.
 */
@RestController
@RequestMapping(API_RESOURCE_URL)
@Api(API_RESOURCE_URL)
public class ResourceController {

    @Inject
    private ResourceService resourceService;

    /**
     * Create a new resource in Organization for OrgId in URL
     *
     * @param resource
     * @return resource
     */
    @ApiOperation(value = "Add a new resource to organization")
    @RequestMapping(value = "/organizationId/{organizationId}", method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> addResource(@PathVariable Long organizationId, @RequestBody Resource resource) {

        if (resource != null && organizationId != null) {
            Resource resourceAdded = resourceService.addResourceToOrganization(organizationId, resource);
            if (resourceAdded != null) {
                return ResponseHandler.generateResponse(HttpStatus.OK, true, resourceAdded);
            }
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);

    }

    /**
     * Get the resource by resourceId provided in url
     *
     * @param resourceId
     * @return resource
     */
    @ApiOperation(value = "Get a resource by resourceId")
    @RequestMapping(value = "/{resourceId}", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getResourceById(@PathVariable Long resourceId) {
        Resource resource = resourceService.getResourceById(resourceId);
        if (resource != null)
            return ResponseHandler.generateResponse(HttpStatus.OK, true, resource);
        else
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
    }


    /**
     * Gets the list of all resources for a Organization
     *
     * @return list of resources for a Organization
     */
    @ApiOperation(value = "Get all resources for organization by organizationId")
    @RequestMapping(value = "/organizationId/{organizationId}", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getAllResourcesByOrgId(@PathVariable Long organizationId) {
        List<Map<String, Object>> resourcesList = resourceService.getAllResourcesByOrgId(organizationId);
        if (resourcesList != null && resourcesList.size() != 0) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, resourcesList);
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
    }

    /**
     * * Update Resource for resourceId in URL
     * and return Updated Resource
     *
     * @param resource
     * @param resourceId
     * @return resource
     */
    /*@ApiOperation("Update a resource by resourceId")
    @RequestMapping(value = "/{resourceId}", method = RequestMethod.PUT)
    ResponseEntity<Map<String, Object>> getResourceById(@RequestBody Resource resource, @PathVariable Long resourceId) {
        if (resourceService.getResourceById(resourceId) != null) {
//            resource.setId(resourceId);
            return ResponseHandler.generateResponse(HttpStatus.OK, true, resourceService.updateResource(resource));
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);

    }*/



}
