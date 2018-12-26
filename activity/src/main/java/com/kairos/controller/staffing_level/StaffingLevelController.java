package com.kairos.controller.staffing_level;

import com.kairos.dto.activity.staffing_level.StaffingLevelFromTemplateDTO;
import com.kairos.persistence.model.staffing_level.StaffingLevel;
import com.kairos.dto.activity.staffing_level.absence.AbsenceStaffingLevelDto;
import com.kairos.dto.activity.staffing_level.presence.PresenceStaffingLevelDto;
import com.kairos.constants.ApiConstants;
import com.kairos.service.staffing_level.StaffingLevelService;
import com.kairos.utils.Message;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;

@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL + "/staffing_level")
@Api(value = API_ORGANIZATION_UNIT_URL + "/staffing_level")
public class StaffingLevelController {

    private Logger logger= LoggerFactory.getLogger(StaffingLevelController.class);
    @Autowired private StaffingLevelService staffingLevelService;


    @RequestMapping(value = "/presence", method = RequestMethod.POST)
    @ApiOperation("Create staffing_level for presence")
    public ResponseEntity<Map<String, Object>> addStaffingLevel(@RequestBody @Valid PresenceStaffingLevelDto presenceStaffingLevelDto,
                                                                @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,
                staffingLevelService.createStaffingLevel(presenceStaffingLevelDto,unitId));
    }


    /**
     * get staffing level between date and unit.
     * @param unitId
     * @return
     */
    @RequestMapping(value = "/presence", method = RequestMethod.GET)
    @ApiOperation("getting  staffing_level between date unit wise ")
    public ResponseEntity<Map<String, Object>> getPresenceStaffingLevels(@PathVariable Long unitId
    , @RequestParam("startDate")@DateTimeFormat(pattern="yyyy-MM-dd") Date startDate, @RequestParam("endDate")@DateTimeFormat(pattern="yyyy-MM-dd")Date endDate) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                staffingLevelService.getPresenceStaffingLevel(unitId,startDate,endDate));
    }


    /**
     * get staffing level by unit and day
     * @param unitId

     * @return
     */
    @RequestMapping(value = "/currentDay", method = RequestMethod.GET)
    @ApiOperation("getting  staffing_level for selected day ")
    public ResponseEntity<Map<String, Object>> getStaffingLevel(@PathVariable Long unitId
            ,@RequestParam("currentDate")Date currentDate) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                staffingLevelService.getPresenceStaffingLevel(unitId,currentDate));
    }

    /**
     * get staffing level by Id
     * @param id

     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation("update staffing_level")
    public ResponseEntity<Map<String, Object>> getStaffingLevel(@PathVariable BigInteger id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                staffingLevelService.getPresenceStaffingLevel(id));
    }

    @RequestMapping(value = "presence/{staffingLevelId}", method = RequestMethod.PUT)
    @ApiOperation("update staffing_level")
    public ResponseEntity<Map<String, Object>> updateStaffingLevel(@RequestBody @Valid PresenceStaffingLevelDto presenceStaffingLevelDto,
        @PathVariable Long unitId,@PathVariable BigInteger staffingLevelId) {
      return ResponseHandler.generateResponse(HttpStatus.OK, true,
                staffingLevelService.updatePresenceStaffingLevel(staffingLevelId,unitId, presenceStaffingLevelDto));
    }


    @RequestMapping(value = "/activity_skills")
    @ApiOperation("Create staffing_level")
    public ResponseEntity<Map<String, Object>> getActivityTypesAndSkillsByUnitId(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                staffingLevelService.getActivityTypesAndSkillsByUnitId(unitId));
    }

    @GetMapping(value = "/phase_daytype")
    @ApiOperation("getting phase and dayType of selected date")
    public ResponseEntity<Map<String, Object>> getPhaseAndDayTypes(@PathVariable Long unitId,@RequestParam("date")Date date ) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                staffingLevelService.getPhaseAndDayTypesForStaffingLevel(unitId,date));
    }


    @MessageMapping("/staffing_level/graph/{unitId}")
    @SendTo(ApiConstants.API_V1+"/ws/dynamic-push/staffing-level/graph/{unitId}")
    public StaffingLevel dynamicStaffingLevelGraphSyncResponse(@DestinationVariable Long unitId, Message message){

        return  staffingLevelService.getPresenceStaffingLevel(unitId,message.getCurrentDate());
    }

    @PostMapping(value = "/submitShiftPlanningInfoToPlanner")
    @ApiOperation("getting phase and dayType of selected date")
    public ResponseEntity<Map<String, Object>> getShiftPlanningInfo(@PathVariable Long unitId ,@RequestParam(value = "startDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate, @RequestParam(value = "endDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate) {
        staffingLevelService.submitShiftPlanningInfoToPlanner(unitId,startDate,endDate);
        return ResponseHandler.generateResponse(HttpStatus.OK, true,null
                );
    }


    @PutMapping(value = "/import_csv")
    @ApiOperation("update staffing level from csv")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    private ResponseEntity<Map<String, Object>> updateStaffingLevelFromCSV(@RequestParam("file") MultipartFile multipartFile,@PathVariable Long unitId) throws Exception{
        staffingLevelService.processStaffingLevel(multipartFile,unitId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }



    @PostMapping(value = "/absence")
    @ApiOperation("update and create staffing_level")
    public ResponseEntity<Map<String, Object>> updateStaffingLevel(@RequestBody @Valid List<AbsenceStaffingLevelDto> absenceStaffingLevelDtos,
                                                                   @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                staffingLevelService.updateAbsenceStaffingLevel(unitId,absenceStaffingLevelDtos));
    }

    /**
     * get staffing level between date and unit.
     * @param unitId
     * @return
     */
    @GetMapping(value = "/")
    @ApiOperation("getting  staffing_level between date unit wise ")
    public ResponseEntity<Map<String, Object>> getStaffingLevels(@PathVariable Long unitId
            , @RequestParam("startDate")@DateTimeFormat(pattern="yyyy-MM-dd") Date startDate, @RequestParam("endDate")@DateTimeFormat(pattern="yyyy-MM-dd")Date endDate) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                staffingLevelService.getStaffingLevel(unitId,startDate,endDate));
    }

    @PostMapping(value = "/copy_from_template/{templateId}")
    @ApiOperation("Create staffing_levels from StaffingLevelTemplate")
    public ResponseEntity<Map<String, Object>> createStaffingLevelFromStaffingLevelTemplate(@PathVariable Long unitId, @RequestBody StaffingLevelFromTemplateDTO staffingLevelFromTemplateDTO,@PathVariable BigInteger templateId) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,
                staffingLevelService.createStaffingLevelFromStaffingLevelTemplate(unitId, staffingLevelFromTemplateDTO,templateId));
    }

    @GetMapping(value = "/updated_staffing_level")
    @ApiOperation("get staffing level if Updated")
    public ResponseEntity<Map<String, Object>> getStaffingLevelIfUpdated(@PathVariable Long unitId, @RequestParam("currentDate")@DateTimeFormat(pattern="yyyy-MM-dd") Date currentDate,@RequestParam("updatedAt")@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss",iso = DateTimeFormat.ISO.DATE) Date updatedAt) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,
                staffingLevelService.getStaffingLevelIfUpdated(unitId, updatedAt,currentDate));
    }
}
