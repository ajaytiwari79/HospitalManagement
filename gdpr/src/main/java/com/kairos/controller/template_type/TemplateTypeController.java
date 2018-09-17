package com.kairos.controller.template_type;
import com.kairos.persistence.model.template_type.TemplateType;
import com.kairos.service.template_type.TemplateTypeService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.ValidateRequestBodyList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import java.math.BigInteger;

import static com.kairos.constants.ApiConstant.*;

@RestController
@Api(API_TEMPLATE_TYPE_URL)
@RequestMapping(API_TEMPLATE_TYPE_URL)
public class TemplateTypeController {


    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateTypeController.class);


    @Inject
    private TemplateTypeService templateTypeService;


    /**
     * @description Create template type. Create form will have only name field. We can create multiple template type in one go.
     * @author vikash patwal
     * @param countryId
     * @param templateData
     * @return list
     */
    @ApiOperation(value = "create new Template type")
    @PostMapping("/createTemplate")
    public ResponseEntity<Object> createTemplateType(@PathVariable Long countryId,@Valid  @RequestBody ValidateRequestBodyList<TemplateType> templateData) {
        if (templateData.getRequestBody().size()>0) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, templateTypeService.createTemplateType(countryId,templateData.getRequestBody()));
        }
        return ResponseHandler.invalidResponse(HttpStatus.OK, true, "Data not found");
    }

    /**
     * @description this method is used get template by name for update purpose.
     * @author vikash patwal
     * @param countryId
     * @param templateName
     * @return TemplateType
     */
    @ApiOperation(value="Get template by name")
    @GetMapping("/getTemplateByName/{templateName}")
    public ResponseEntity<Object> getTemplateByName(@PathVariable Long countryId,@PathVariable String templateName) {
        if (StringUtils.isBlank(templateName)) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "templateName parameter is null or empty");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, templateTypeService.getTemplateByName(countryId,templateName));
    }

    /**
     * @description this template is used for update template type by id.
     * @author vikash patwal
     * @param id
     * @param countryId
     * @param templateType
     * @return TemplateType
     */
    @ApiOperation(value="update template")
    @PutMapping(value = "/updateTemplate/{id}")
    public ResponseEntity<Object> updateTemplate(@PathVariable BigInteger id,@PathVariable Long countryId, @Valid @RequestBody TemplateType templateType) {

         if(id==null){
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id parameter is null or empty");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, templateTypeService.updateTemplateName(id,countryId,templateType));
    }

    /**
     * @description this template is used for delete template by id.
     * @author vikash patwal
     * @param countryId
     * @param id
     * @returne Boolean
     */
    @ApiOperation(value="delete template by id")
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Object> deleteTemplateType(@PathVariable Long countryId,@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_GATEWAY, false, "id cannot be null");
        } else
            return ResponseHandler.generateResponse(HttpStatus.OK, true, templateTypeService.deleteTemplateType(id,countryId));

    }

    /**
     * @description this template is used for get all template type
     * @author vikash patwal
     * @param countryId
     * @return  List<TemplateType>
     */
    @ApiOperation(value = "All Template Type type ")
    @GetMapping(value = "/all")
    public ResponseEntity<Object> getAllTemplateType(@PathVariable Long countryId) {
        if (countryId != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, templateTypeService.getAllTemplateType(countryId));
        }
        else {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");

        }


    }
}
