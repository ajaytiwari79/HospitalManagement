package com.kairos.controller.time_care;

import com.kairos.persistence.model.time_care.TimeCareSkill;
import com.kairos.persistence.model.staff.TimeCareEmploymentDTO;
import com.kairos.persistence.model.staff.TimeCareStaffDTO;
import com.kairos.service.skill.SkillService;
import com.kairos.service.staff.StaffService;
import com.kairos.service.unit_position.UnitPositionService;
import com.kairos.utils.response.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_V1;
import static com.kairos.constants.ApiConstants.COUNTRY_URL;

/**
 * Created by prabjot on 16/1/18.
 */
@RestController
@RequestMapping(API_V1 + "/time_care")
public class TimeCareController {

    @Inject
    private SkillService skillService;
    @Inject
    private StaffService staffService;
    @Inject
    private UnitPositionService unitPositionService;

    @RequestMapping(value = COUNTRY_URL+"/skills",method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> importSkillsFromTimeCare(@PathVariable Long countryId,@RequestBody List<TimeCareSkill> timeCareSkills){

        return ResponseHandler.generateResponse(HttpStatus.CREATED,true,skillService.importSkillsFromTimeCare(timeCareSkills,countryId));
    }

    @RequestMapping(value = "/organization/{organizationExternalId}/staff",method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> importStaffFromTimeCare(@RequestBody List<TimeCareStaffDTO> timeCareStaffDTOS,@PathVariable String organizationExternalId){

        return ResponseHandler.generateResponse(HttpStatus.CREATED,true,staffService.importStaffFromTimeCare(timeCareStaffDTOS,organizationExternalId));
    }

    @RequestMapping(value = "/organization/{organizationId}/unit/{unitId}/staff/employments",method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> importEmploymentsFromTimeCare(@RequestBody List<TimeCareEmploymentDTO> timeCareEmploymentDTOS,
                                                                            @RequestParam(value = "expertiseId",required = false) Long expertiseId){

        return ResponseHandler.generateResponse(HttpStatus.CREATED,true, unitPositionService.importAllEmploymentsFromTimeCare(timeCareEmploymentDTOS, expertiseId));
    }

}
