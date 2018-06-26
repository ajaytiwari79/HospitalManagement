package com.kairos.controller.template_type;
import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistance.model.template_type.TemplateType;
import com.kairos.service.template_type.TemplateTypeService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.validate_list.ValidateListOfRequestBody;
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
     * @throws
     * @return list
     */
    @ApiOperation(value = "create new Template type")
    @PostMapping("/createTemplate")
    public ResponseEntity<Object> createTemplateType(@PathVariable Long countryId,@Valid  @RequestBody ValidateListOfRequestBody<TemplateType> templateData) {
        if (templateData.getRequestBody().size()>0) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, templateTypeService.createTemplateType(countryId,templateData.getRequestBody()));
        }
        return ResponseHandler.invalidResponse(HttpStatus.OK, true, "Data not found");
    }

    /**
     * @description get template by name
     * @author vikash patwal
     * @param countryId
     * @param templateName
     * @throws DataNotExists
     * @return TemplateType
     */
    @ApiOperation(value="Get template by name")
    @GetMapping("/getTemplateByName")
    public ResponseEntity<Object> getTemplateByName(@PathVariable Long countryId,@RequestParam String templateName) {
        if (StringUtils.isBlank(templateName)) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "templateName parameter is null or empty");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, templateTypeService.getTemplateByName(countryId,templateName));
    }

    /**
     * @description update template by id
     * @author vikash patwal
     * @param id
     * @param countryId
     * @param templateType
     * @throws DuplicateDataException
     * @return TemplateType
     */
    @ApiOperation(value="update template")
    @RequestMapping(value = "/updateTemplate", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateTemplate(@RequestParam BigInteger id,@PathVariable Long countryId, @Valid @RequestBody TemplateType templateType) {
        if (StringUtils.isBlank(templateType.getTemplateName())) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "templateName parameter is null or empty");
        }
        else if(id==null){
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id parameter is null or empty");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, templateTypeService.updateTemplateName(id,countryId,templateType));
    }

    /**
     * @description template by id
     * @author vikash patwal
     * @param countryId
     * @param id
     * @throws DataNotFoundByIdException
     * @returne Boolean
     */
    @ApiOperation(value="delete template by id")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteTemplateType(@PathVariable Long countryId,@RequestParam BigInteger id) {
        if (id == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_GATEWAY, false, "id cannot be null");
        } else
            return ResponseHandler.generateResponse(HttpStatus.OK, true, templateTypeService.deleteTemplateType(id));

    }
}
