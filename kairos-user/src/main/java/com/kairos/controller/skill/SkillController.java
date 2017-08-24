package com.kairos.controller.skill;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.service.skill.SkillService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_V1;
import static com.kairos.constants.ApiConstants.PARENT_ORGANIZATION_URL;

/**
 * Created by oodles on 15/9/16B.
 */

@RestController
@Api(API_V1)
@RequestMapping(API_V1+PARENT_ORGANIZATION_URL )
public class SkillController {

    @Inject
    private SkillService skillService;

    @ApiOperation(value = "Get a skill by id ")
    @RequestMapping(value = "/skill/{id}", method = RequestMethod.GET)
    @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
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
    @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllSkill(@PathVariable long countryId) {
        List<Map<String, Object>> skills = skillService.getAllSkills(countryId);
        if (skills == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, skills);
    }
}