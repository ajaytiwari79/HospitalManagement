package com.kairos.controller.skill;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.service.skill.SkillService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.constants.ApiConstants.API_V1;


/**
 * Created by oodles on 15/9/16B.
 */

@RestController
@Api(API_V1)
@RequestMapping(API_V1 )
public class SkillController {

    @Inject
    private SkillService skillService;

    @ApiOperation(value = "Get a skill by id ")
    @RequestMapping(value = "/skill/{id}", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getSkillById(@PathVariable Long id) {
        if (id != null) {
            Skill skill = skillService.getSkillById(id, 2);
            if (skill != null) {
                return ResponseHandler.generateResponse(HttpStatus.OK, true, skill);
            }
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);

    }

    @ApiOperation(value = "Get all skills by countryId")
    @RequestMapping(value = "/country/{countryId}/skills", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllSkill(@PathVariable long countryId) {
        List<Map<String, Object>> skills = skillService.getAllSkills(countryId);
        if (skills == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, skills);
    }

    @ApiOperation(value = "Get all skills by countryId")
    @RequestMapping(value = "/country/{countryId}/skills_by_name", method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> getSkillsByName(@RequestBody Set<String> skillsName){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,skillService.getSkillsByName(skillsName));
    }
}