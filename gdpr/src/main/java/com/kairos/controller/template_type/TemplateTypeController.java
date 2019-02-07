package com.kairos.controller.template_type;

import com.kairos.persistence.model.template_type.TemplateType;
import com.kairos.service.template_type.TemplateTypeService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.ValidateRequestBodyList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import static com.kairos.constants.ApiConstant.*;

@RestController
@Api(API_TEMPLATE_TYPE_URL)
@RequestMapping(API_TEMPLATE_TYPE_URL)
public class TemplateTypeController {


    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateTypeController.class);


    @Inject
    private TemplateTypeService templateTypeService;


    /**
     * @param countryId
     * @param templateData
     * @return list
     * @description Create template type. Create form will have only name field. We can create multiple template type in one go.
     * @author vikash patwal
     */

    @ApiOperation(value = "create new Template type")
    @PostMapping("/createTemplate")
    public ResponseEntity<Object> createTemplateType(@PathVariable Long countryId, @Valid @RequestBody ValidateRequestBodyList<TemplateType> templateData) {
        if (CollectionUtils.isEmpty(templateData.getRequestBody())) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "message.enter.valid.data");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, templateTypeService.createTemplateType(countryId, templateData.getRequestBody()));
    }


    /**
     * @param id
     * @param countryId
     * @param templateType
     * @return TemplateType
     * @description this template is used for update template type by id.
     * @author vikash patwal
     */
    @ApiOperation(value = "update template")
    @PutMapping(value = "/updateTemplate/{id}")
    public ResponseEntity<Object> updateTemplate(@PathVariable Long id, @PathVariable Long countryId, @Valid @RequestBody TemplateType templateType) {

        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id parameter is null or empty");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, templateTypeService.updateTemplateName(id, countryId, templateType));
    }

    /**
     * @param countryId
     * @param id
     * @description this template is used for delete template by id.
     * @author vikash patwal
     * @returne Boolean
     */
    @ApiOperation(value = "delete template by id")
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Object> deleteTemplateType(@PathVariable Long countryId, @PathVariable Long id) {
        if (id == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_GATEWAY, false, "id cannot be null");
        } else
            return ResponseHandler.generateResponse(HttpStatus.OK, true, templateTypeService.deleteTemplateType(id, countryId));

    }

    /**
     * @param countryId
     * @return List<TemplateType>
     * @description this template is used for get all template type
     * @author vikash patwal
     */
    @ApiOperation(value = "All Template Type type ")
    @GetMapping(value = "/all")
    public ResponseEntity<Object> getAllTemplateType(@PathVariable Long countryId) {
        if (countryId != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, templateTypeService.getAllTemplateType(countryId));
        } else {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");

        }


    }
}
