package com.kairos.controller.skill;

import com.kairos.persistence.model.user.skill.SkillCategory;
import com.kairos.service.country.CountryService;
import com.kairos.service.skill.SkillCategoryService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;


/**
 * SkillCategoryController
 * 1.Calls SkillCategoryService
 * 2. Call for CRUD operation on SkillCategory using SkillCategoryService.
 */
@RequestMapping(API_V1)
@Api(API_V1)
@RestController
public class SkillCategoryController {

    @Inject
    private SkillCategoryService skillCategoryService;

    @Inject
    private CountryService countryService;

    @RequestMapping(value = COUNTRY_URL+"/skill_category/{id}", method = RequestMethod.GET)
    @ApiOperation("Get a skillCategory by id")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getSkillCategory(@PathVariable Long id) {
        if (id != null) {
            SkillCategory skillCategory = skillCategoryService.getSkillCategorybyId(id);
            if (skillCategory != null) {
                return ResponseHandler.generateResponse(HttpStatus.OK, true, skillCategory);
            }
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
    }



    @RequestMapping(value = "/skill_category", method = RequestMethod.GET)
    @ApiOperation("Get all skillCategory")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllSkillCategory() {
        List<SkillCategory> skillCategoryList = skillCategoryService.getAllSkillCategory();
        if (skillCategoryList.size()!=0)
            return ResponseHandler.generateResponse(HttpStatus.OK,true,skillCategoryList);
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST,false,null);
    }


//
//    @RequestMapping(value = "/skill_category/{skillCategoryId}", method = RequestMethod.PUT)
//    @ApiOperation("Update a skillCategory  by id")
//    ResponseEntity<Map<String, Object>> updateSkillCategoryById(@PathVariable long skillCategoryId, @RequestBody SkillCategory skillData) {
//
//        Map<String, Object> updatedSkillCategory = skillCategoryService.updateSkillCategory(skillData);
//        if (updatedSkillCategory == null) {
//            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, updatedSkillCategory);
//        }
//        return ResponseHandler.generateResponse(HttpStatus.OK, true, updatedSkillCategory);
//    }

    @RequestMapping(value = "/skill_category/{skillCategoryId}", method = RequestMethod.DELETE)
    @ApiOperation("Delete a skillCategory  by id")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteSkillCategoryById(@PathVariable long skillCategoryId) {

        skillCategoryService.deleteSkillCategorybyId(skillCategoryId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }


}
