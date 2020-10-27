package com.kairos.controller.time_slot;

import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotSetDTO;
import com.kairos.service.time_slot.TimeSlotSetService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.constants.ApiConstants.UNIT_URL;

@RestController
public class TimeSlotController {

    @Inject
    private TimeSlotSetService timeSlotService;

    @ApiOperation(value = "Get Organization Time Slots")
    @GetMapping(UNIT_URL + "/time_slot_set/{timeSlotSetId}/time_slot")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTimeSlots(@PathVariable Long timeSlotSetId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeSlotService.getTimeSlotByTimeSlotSet(timeSlotSetId));
    }

    @ApiOperation(value = "update time slot type")
    @PutMapping(UNIT_URL + "/time_slot_type")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateTimeSlotType(@PathVariable long unitId, @RequestBody Map<String, Object> timeSlotType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeSlotService.updateTimeSlotType(unitId, (boolean) timeSlotType.get("standardTimeSlot")));
    }

    @ApiOperation(value = "Get Organization Time Slot sets")
    @GetMapping(UNIT_URL + "/time_slot_set")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTimeSlotSets(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeSlotService.getTimeSlotSets(unitId));
    }

    @ApiOperation(value = "create new time slot set")
    @PostMapping(UNIT_URL + "/time_slot")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createTimeSlotSet(@PathVariable long unitId, @Validated @RequestBody TimeSlotSetDTO timeSlotSetDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeSlotService.createTimeSlotSet(unitId, timeSlotSetDTO));
    }

    @ApiOperation(value = "create new time slot set")
    @PostMapping(UNIT_URL + "/time_slot_set/{timeSlotSetId}/time_slot")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createTimeSlot(@PathVariable BigInteger timeSlotSetId, @Validated @RequestBody TimeSlotDTO timeSlotDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeSlotService.createTimeSlot(timeSlotSetId, timeSlotDTO));
    }

    @ApiOperation(value = "update time slot set")
    @PutMapping(UNIT_URL + "/time_slot_set/{timeSlotSetId}")
    public ResponseEntity<Map<String, Object>> updateTimeSlotSet(@PathVariable Long unitId, @PathVariable BigInteger timeSlotSetId, @Validated @RequestBody TimeSlotSetDTO timeSlotSetDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeSlotService.updateTimeSlotSet(timeSlotSetId, timeSlotSetDTO));
    }

    @ApiOperation(value = "Update time slot")
    @PutMapping(UNIT_URL + "/time_slot_set/{timeSlotId}/time_slot")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateTimeSlot(@Validated @RequestBody List<TimeSlotDTO> timeSlotDTO, @PathVariable BigInteger timeSlotId) {
        TimeSlotSetDTO timeSlotSetDTO=new TimeSlotSetDTO();
        timeSlotSetDTO.setTimeSlots(timeSlotDTO);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeSlotService.updateTimeSlotSet(timeSlotId,timeSlotSetDTO));
    }

    @ApiOperation(value = "delete time slot set")
    @DeleteMapping(UNIT_URL + "/time_slot_set/{timeSlotId}")
    public ResponseEntity<Map<String, Object>> deleteTimeSlotSet(@PathVariable Long unitId, @PathVariable BigInteger timeSlotId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeSlotService.deleteTimeSlotSet( timeSlotId));
    }

    @ApiOperation(value = "Get current time slots of organization")
    @GetMapping(UNIT_URL + "/current/time_slots")
    public ResponseEntity<Map<String, Object>> getCurrentTimeSlotsOfOrganization(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeSlotService.getCurrentTimeSlotOfUnit(unitId));
    }

    @ApiOperation(value = "Get time slots of organization")
    @GetMapping(UNIT_URL + "/get_time_slots")
    public ResponseEntity<Map<String, Object>> getTimeSlotOfUnit(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeSlotService.getUnitTimeSlot(unitId));
    }

    @ApiOperation(value = "Get time slots of organization")
    @GetMapping(UNIT_URL + "/get_time_slots_by_id")
    public ResponseEntity<Map<String, Object>> getTimeSlotOfUnitById(@PathVariable Long unitId, @RequestParam Set<String> timeSlotIds) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeSlotService.getUnitTimeSlotByNames(unitId, timeSlotIds));
    }

    @ApiOperation(value = "Get Organization Time Slots")
    @GetMapping(UNIT_URL + "/time_slot_set/{timeSlotSetId}/shift_planning/time_slot")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getShiftPlanningTimeSlotsByUnit(@PathVariable Long timeSlotSetId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeSlotService.getShiftPlanningTimeSlotsById(timeSlotSetId));
    }

    @ApiOperation(value = "Get Organization Time Slot sets")
    @GetMapping(UNIT_URL + "/shift_planning/time_slot_set")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getShiftPlanningTimeSlotSetsByUnit(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeSlotService.getShiftPlanningTimeSlotSetsByUnit(unitId));
    }




}
