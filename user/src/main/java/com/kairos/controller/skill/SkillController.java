package com.kairos.controller.skill;

import com.kairos.dto.TranslationInfo;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.service.skill.SkillService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.constants.ApiConstants.API_V1;
import static com.kairos.constants.ApiConstants.UNIT_URL;


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



    @PostMapping("/country/{countryId}/get_Skill_and_level_by_staff_ids")
    @ApiOperation("Get Staff's SkillId And Level")
    public ResponseEntity<Map<String, Object>> getStaffSkillAndLevelByStaffIds(@RequestBody List<Long> staffIds, @RequestParam("selectedFromDate")@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate selectedFromDate, @RequestParam(value = "selectedToDate", required = false)@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate selectedToDate) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, skillService.getStaffSkillAndLevelByStaffIds(staffIds, selectedFromDate, isNull(selectedToDate) ? selectedFromDate : selectedToDate));
    }

    @PostMapping("/country/{countryId}/get_Skill_ALL_and_level_by_staff_ids")
    @ApiOperation("Get Staff's All SkillId And Level")
    public ResponseEntity<Map<String, Object>> getStaffAllSkillAndLevelByStaffIds(@RequestBody List<Long> StaffIds) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, skillService.getStaffAllSkillAndLevelByStaffIds(StaffIds));
    }

    @ApiOperation(value = "Get all skills by unitId")
    @GetMapping(value = "/unit/{unitId}/skills_and_expertise_by_unit")
    public ResponseEntity<Map<String,Object>> getSkillsByUnit(@PathVariable Long unitId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,skillService.getSkillByUnit(unitId));
    }

    @PutMapping(value = UNIT_URL + "/skill/{id}/language_settings")
    @ApiOperation("Add translated data")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateTranslationsOfOrganizationSkill(@PathVariable Long id, @RequestBody Map<String, TranslationInfo> translations) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, skillService.updateTranslationOfOrganizationSkills(id,translations));
    }



}