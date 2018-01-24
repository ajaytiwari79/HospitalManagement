package com.kairos.controller.time_care;

import com.kairos.persistence.model.time_care.TimeCareSkill;
import com.kairos.persistence.model.user.staff.TimeCareStaffDTO;
import com.kairos.service.skill.SkillService;
import com.kairos.service.staff.StaffService;
import com.kairos.util.response.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_V1;

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

    @RequestMapping(value = "/skills",method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> importSkillsFromTimeCare(@RequestBody List<TimeCareSkill> timeCareSkills){

        return ResponseHandler.generateResponse(HttpStatus.CREATED,true,skillService.importSkillsFromTimeCare(timeCareSkills,53L));
    }

    @RequestMapping(value = "/organization/{organizationExternalId}/staff",method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> importStaffFromTimeCare(@RequestBody List<TimeCareStaffDTO> timeCareStaffDTOS,@PathVariable String externalId){

        return ResponseHandler.generateResponse(HttpStatus.CREATED,true,staffService.importStaffFromTimeCare(timeCareStaffDTOS,externalId));
    }


}
