package com.kairos.controller.task;

import com.kairos.dto.activity.task_type.TaskTypeCopyDTO;
import com.kairos.dto.activity.task_type.TaskTypeDTO;
import com.kairos.dto.activity.task_type.TaskTypeSettingDTO;
import com.kairos.dto.activity.task_type.TaskTypeSlaConfigDTO;
import com.kairos.persistence.model.task_type.MapPointer;
import com.kairos.persistence.model.task_type.TaskType;
import com.kairos.persistence.model.task_type.TaskTypeDefination;
import com.kairos.service.MapPointerService;
import com.kairos.service.task_type.TaskTypeService;
import com.kairos.utils.response.ResponseHandler;
import com.kairos.wrapper.task_type.TaskTypeResourceDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;


/**
 * Created by prabjot on 5/10/16.
 */
@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(value = API_ORGANIZATION_URL)
public class TaskTypeController {

    private static final Logger logger = LoggerFactory.getLogger(TaskTypeController.class);

    @Inject
    private TaskTypeService taskTypeService;
    @Inject
    private MapPointerService mapPointerService;

    @RequestMapping(value = "/task_types", method = RequestMethod.POST)
    @ApiOperation("create task_type type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createTaskType(@RequestParam(value = "subServiceId") long subServiceId, @Validated @RequestBody TaskTypeDTO taskTypeDTO) throws ParseException {
        //todo require Implementation
        //String rehabTime = (String) data.get("rehabTime");

        return ResponseHandler.generateResponse(HttpStatus.OK, true, taskTypeService.createTaskType(taskTypeDTO,subServiceId));
    }

    @RequestMapping(value = "/task_types", method = RequestMethod.GET)
    @ApiOperation("get all tasks by sub service")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTaskTypes(@RequestParam(value = "subServiceId", required = false) Long subServiceId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, taskTypeService.getAllTaskTypes(subServiceId));
    }

    @RequestMapping(value = "/all_task_types", method = RequestMethod.GET)
    @ApiOperation("get all tasks ")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTaskTypes() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, taskTypeService.getAllTaskTypes());
    }

    @RequestMapping(value = "/unit/{unitId}/all_task_types_by_unit", method = RequestMethod.GET)
    @ApiOperation("get all tasks for unit")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTaskTypesForAnUnit(@PathVariable("unitId") Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, taskTypeService.getByOrganization(unitId));
    }


    @RequestMapping(value = "/task_types/{taskTypeId}/status", method = RequestMethod.PUT)
    @ApiOperation("update task status")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateStatus(@PathVariable String taskTypeId, @RequestBody Map<String, Object> data) {

        boolean status = (boolean) data.get("status");
        taskTypeService.updateStatus(taskTypeId, status);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, data);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/agreement_settings", method = RequestMethod.POST)
    @ApiOperation("Save task_type type agreement tab data for task_type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveAgreementSetting(@PathVariable String taskTypeId, @RequestBody Map<String, Object> map) throws ParseException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        final String union = (String) map.get("union");
        final String agreement = (String) map.get("agreement");
        final Date startPeriod = simpleDateFormat.parse((String) map.get("startPeriod"));
        final Date endPeriod = simpleDateFormat.parse((String) map.get("endPeriod"));
        return ResponseHandler.generateResponse(HttpStatus.OK, true, taskTypeService.saveAgreementSetting(taskTypeId, union, agreement, startPeriod, endPeriod));
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/agreement_settings", method = RequestMethod.GET)
    @ApiOperation("get agreement tab data for a task type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAgreementSettings(@PathVariable String taskTypeId) {

        Map<String, Object> agreementSettings = taskTypeService.getAgreementSettings(taskTypeId);
        if (agreementSettings == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, agreementSettings);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/balance_settings", method = RequestMethod.POST)
    @ApiOperation("save balance tab data for a task type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveBalanceSettings(@PathVariable String taskTypeId, @RequestBody Map<String, Object> balanceSettings) {

        Map<String, Object> response = taskTypeService.saveBalanceSettings(taskTypeId, balanceSettings);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/balance_settings", method = RequestMethod.GET)
    @ApiOperation("get balance tab data for a task type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getBalanceSettings(@PathVariable String taskTypeId) {

        Map<String, Object> response = taskTypeService.getBalanceSettings(taskTypeId);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/communication_settings", method = RequestMethod.POST)
    @ApiOperation("save communication tab data for a task type")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveCommunicationSettings(@PathVariable String taskTypeId, @RequestBody Map<String, Object> communicationSettings) {
        boolean reminderBySms = (boolean) communicationSettings.get("reminderBySms");
        String minutesBeforeSend = null;
        if (reminderBySms == true) minutesBeforeSend = (String) communicationSettings.get("minutesBeforeSend");
        boolean notificationBySms = (boolean) communicationSettings.get("notificationBySms");
        boolean reminderBySmsBeforeArrival = (boolean) communicationSettings.get("reminderBySmsBeforeArrival");
        String minutesBeforeArrival = (String) communicationSettings.get("minutesBeforeArrival");

        String time = (String) communicationSettings.get("time");
        Map<String, Object> response = taskTypeService.saveCommunicationSettings(taskTypeId, reminderBySms, notificationBySms, minutesBeforeSend, minutesBeforeArrival, reminderBySmsBeforeArrival, time);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/communication_settings", method = RequestMethod.GET)
    @ApiOperation("get communication tab data for a task type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveCommunicationSettings(@PathVariable String taskTypeId) {
        Map<String, Object> response = taskTypeService.getCommunicationSettings(taskTypeId);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/cost_income_settings", method = RequestMethod.POST)
    @ApiOperation("save cost income tab data for a task type")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveCostIncomeSettings(@PathVariable String taskTypeId, @RequestBody Map<String, Object> costIncomeSettings) {
        Map<String, Object> response = taskTypeService.saveCostIncomeSettings(taskTypeId, costIncomeSettings);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/cost_income_settings", method = RequestMethod.GET)
    @ApiOperation("get cost income tab data for a task type")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getCostIncomeSettings(@PathVariable String taskTypeId) {
        Map<String, Object> response = taskTypeService.getCostIncomeSettings(taskTypeId);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/creation_rules", method = RequestMethod.POST)
    @ApiOperation("create creation rules for a task type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveTaskTypeRules(@PathVariable String taskTypeId, @RequestBody Map<String, Object> data) {
        List<String> rules = (List<String>) data.get("rules");
        List<String> response = taskTypeService.saveTaskTypeRules(taskTypeId, rules);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/creation_rules", method = RequestMethod.GET)
    @ApiOperation("get creation rules for a task type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTaskTypeRules(@PathVariable String taskTypeId) {
        List<String> response = taskTypeService.getTaskTypeRules(taskTypeId);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/definitions", method = RequestMethod.POST)
    @ApiOperation("save definations for a task type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveTaskTypeDefinations(@PathVariable String taskTypeId, @RequestBody TaskTypeDefination taskTypeDefinitionDTO) {
        TaskTypeDefination dto = taskTypeService.saveTaskTypeDefinations(taskTypeId, taskTypeDefinitionDTO);
        if (dto == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dto);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/definitions", method = RequestMethod.GET)
    @ApiOperation("get definitions for a task type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTaskTypeDefinitions(@PathVariable String taskTypeId) {
        TaskTypeDefination dto = taskTypeService.getTaskTypeDefinitions(taskTypeId);
        if (dto == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, dto);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/dependencies", method = RequestMethod.POST)
    @ApiOperation("save dependencies for a task type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveTaskTypeDependencies(@PathVariable String taskTypeId, @RequestBody Map<String, Object> dependencies) {
        taskTypeService.saveTaskTypeDependencies(taskTypeId, (boolean) dependencies.get("finishToStart"));
        return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/dependencies", method = RequestMethod.GET)
    @ApiOperation("get dependencies for a task type")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTaskTypeDependencies(@PathVariable String taskTypeId) {
        Map<String, Boolean> response = taskTypeService.getTaskTypeDependencies(taskTypeId);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/general_settings", method = RequestMethod.POST)
    @ApiOperation("get dependencies for a task type")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveGeneralSettings(@PathVariable String taskTypeId, @RequestBody Map<String, Object> generalSettings) {
        Map<String, Object> response = taskTypeService.saveGeneralSettings(taskTypeId, generalSettings);

        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/general_settings", method = RequestMethod.GET)
    @ApiOperation("get general settings for a task type")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getGeneralSettings(@PathVariable String taskTypeId, @RequestParam(value = "type",required = false) String type) {
        Map<String, Object> response = taskTypeService.getGeneralSettings(taskTypeId, type);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/logging", method = RequestMethod.POST)
    @ApiOperation("save logging/history settings for a task type")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveLoggingSettings(@PathVariable String taskTypeId, @RequestBody Map<String, Object> loggingSettings) {
        boolean hasDateOfCreation = (boolean) loggingSettings.get("hasDateOfCreation");
        boolean hasDateOfChange = (boolean) loggingSettings.get("hasDateOfChange");

        Map<String, Object> response = taskTypeService.saveLoggingSettings(taskTypeId, hasDateOfChange, hasDateOfCreation);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/logging", method = RequestMethod.GET)
    @ApiOperation("get logging/history settings for a task type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getLoggingSettings(@PathVariable String taskTypeId) {
        Map<String, Object> response = taskTypeService.getLoggingSettings(taskTypeId);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);

    }

    @RequestMapping(value = "/task_types/{taskTypeId}/main_task", method = RequestMethod.POST)
    @ApiOperation("save main task settings for a task type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveMainTaskSettings(@PathVariable String taskTypeId, @RequestBody Map<String, Object> mainTaskSettings) {
        boolean hasCompositeShift = (boolean) mainTaskSettings.get("hasCompositeShift");
        boolean isMainTask = (boolean) mainTaskSettings.get("isMainTask");
        boolean finishToStart = (boolean) mainTaskSettings.get("finishToStart");
        List<String> subTask = (List<String>) mainTaskSettings.get("subTask");
        Map<String, Object> response = taskTypeService.saveMainTaskSettings(taskTypeId, isMainTask, hasCompositeShift, subTask, finishToStart);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/main_task", method = RequestMethod.GET)
    @ApiOperation("get main task settings for a task type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getMainTaskSettings(@PathVariable String taskTypeId) {
        Map<String, Object> response = taskTypeService.getMainTaskSettings(taskTypeId);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/notification_setting", method = RequestMethod.POST)
    @ApiOperation("save notification settings for a task type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveNotificationSettings(@PathVariable String taskTypeId, @RequestBody Map<String, Object> settings) {
        Map<String, Object> response = taskTypeService.saveNotificationSettings(taskTypeId, settings);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/notification_setting", method = RequestMethod.GET)
    @ApiOperation("get notification settings for a task type")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getNotificationSettings(@PathVariable String taskTypeId) {
        Map<String, Object> response = taskTypeService.getNotificationSettings(taskTypeId);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/planning_rules", method = RequestMethod.POST)
    @ApiOperation("set planner rules for a task type")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> savePlanningRules(@PathVariable String taskTypeId, @RequestBody Map<String, Object> planningRules) {
        boolean isAssignedToClipBoard = (boolean) planningRules.get("isAssignedToClipBoard");
        boolean useInShiftPlanning = (boolean) planningRules.get("useInShiftPlanning");
        List<String> shiftPlanningPhases = (List<String>) planningRules.get("shiftPlanningPhases");
        taskTypeService.savePlanningRules(taskTypeId, isAssignedToClipBoard, useInShiftPlanning, shiftPlanningPhases);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, planningRules);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/planning_rules", method = RequestMethod.GET)
    @ApiOperation("set planner rules for a task type")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getPlanningRules(@PathVariable String taskTypeId) {
        Map<String, Object> response = taskTypeService.getPlanningRules(taskTypeId);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/points_methods", method = RequestMethod.POST)
    @ApiOperation("save methods for calculating pointMethods for a task type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveMethodsForPoints(@PathVariable String taskTypeId, @RequestBody Map<String, Object> methods) {
        List<String> pointMethods = (List<String>) methods.get("pointMethods");
        List<String> response = taskTypeService.saveMethodsForPoints(taskTypeId, pointMethods);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_LIST);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/points_methods", method = RequestMethod.GET)
    @ApiOperation("save methods for calculating pointMethods for a task type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getMethodsForPoints(@PathVariable String taskTypeId) {
        List<String> response = taskTypeService.getMethodsForPoints(taskTypeId);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_LIST);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/resources", method = RequestMethod.POST)
    @ApiOperation("save required resources for a task type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveResourcesForTaskType(@PathVariable String taskTypeId, @RequestBody TaskTypeResourceDTO taskTypeResourceDTO) {
        boolean response = taskTypeService.saveResources(taskTypeId, taskTypeResourceDTO);
        if (response) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, taskTypeResourceDTO);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP);

    }

    @RequestMapping(value = "/task_types/{taskTypeId}/resources", method = RequestMethod.GET)
    @ApiOperation("get  resources for a task type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getResourcesForTaskType(@PathVariable String taskTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, taskTypeService.getResources(taskTypeId));
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/rest_time", method = RequestMethod.POST)
    @ApiOperation("save resting time for a task type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveRestingTime(@PathVariable String taskTypeId, @RequestBody Map<String, Object> data) {
        String time = (String) data.get("restTime");
        taskTypeService.saveRestingTime(taskTypeId, time);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, data);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/rest_time", method = RequestMethod.GET)
    @ApiOperation("get resting time for a task type")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getRestingTime(@PathVariable String taskTypeId) {
        Map<String, String> response = taskTypeService.getRestingTime(taskTypeId);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_LIST);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/skills", method = RequestMethod.POST)
    @ApiOperation("save skills for a task type")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveTaskTypeSkills(@PathVariable String taskTypeId, @RequestBody List<Map<String, Object>> data) {
        logger.info("data received in json" + data.toString());
        taskTypeService.saveSkillsForTaskType(taskTypeId, data);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_LIST);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/skills", method = RequestMethod.GET)
    @ApiOperation("get skills for a task type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTaskTypeSkills(@PathVariable String taskTypeId) {
        Map<String, Object> taskTypeSkills = taskTypeService.getSkills(taskTypeId);
        if (taskTypeSkills == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_LIST);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, taskTypeSkills);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/staff", method = RequestMethod.POST)
    @ApiOperation("save staff settings for a task type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveTaskTypeStaff(@PathVariable String taskTypeId, @RequestBody Map<String, Object> data) {
        taskTypeService.saveTaskTypeStaff(taskTypeId, data);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, data);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/staff", method = RequestMethod.GET)
    @ApiOperation("get staff settings for a task type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTaskTypeStaff(@PathVariable String taskTypeId) {
        Map<String, Object> response = taskTypeService.getTaskTypeStaff(taskTypeId);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_LIST);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/time_frames", method = RequestMethod.POST)
    @ApiOperation("save time frames for a task type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveTimeFrames(@PathVariable String taskTypeId, @RequestBody Map<String, Object> data) {
        taskTypeService.saveTimeFrames(taskTypeId, data);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, data);
    }

    @RequestMapping(value = "/unit/{unitId}/task_types/{taskTypeId}/time_frames", method = RequestMethod.GET)
    @ApiOperation("get time frames for a task type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTimeFrames(@PathVariable Long unitId, @PathVariable String taskTypeId) {
        Map<String, Object> response = taskTypeService.getTimeFrames(unitId, taskTypeId);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_LIST);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @RequestMapping(value = COUNTRY_URL + "/task_types/{taskTypeId}/time_frames", method = RequestMethod.GET)
    @ApiOperation("get time frames for a task type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTimeFramesByCountryId(@PathVariable Long countryId, @PathVariable String taskTypeId) {
        Map<String, Object> response = taskTypeService.getTimeFramesByCountryIdAndTaskTypeId(countryId, taskTypeId);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_LIST);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/time_rules", method = RequestMethod.POST)
    @ApiOperation("save time rules for a task type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveTimeRules(@PathVariable String taskTypeId, @RequestBody Map<String, Object> data) {
        Map<String, Object> response = taskTypeService.saveTimeRules(taskTypeId, data);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/time_rules", method = RequestMethod.GET)
    @ApiOperation("get time rules for a task type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTimeRules(@PathVariable String taskTypeId) {
        Map<String, Object> response = taskTypeService.getTimeRules(taskTypeId);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/visitation", method = RequestMethod.POST)
    @ApiOperation("save visitation settings for a task type")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveVisitationSettings(@PathVariable String taskTypeId, @RequestBody Map<String, Object> data) {
        boolean onlyVisitatorCanAssignDuration = (boolean) data.get("onlyVisitatorCanAssignDuration");
        boolean onlyVisitatorCanTaskFrequency = (boolean) data.get("onlyVisitatorCanTaskFrequency");
        boolean onlyVisitatorCanAssignToClients = (boolean) data.get("onlyVisitatorCanAssignToClients");
        Map<String, Boolean> response = taskTypeService.saveVisitationSettings(taskTypeId, onlyVisitatorCanAssignDuration, onlyVisitatorCanTaskFrequency, onlyVisitatorCanAssignToClients);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/visitation", method = RequestMethod.GET)
    @ApiOperation("get visitation settings for a task type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getVisitationSettings(@PathVariable String taskTypeId) {
        Map<String, Boolean> response = taskTypeService.getVisitationSettings(taskTypeId);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/copy/settings", method = RequestMethod.POST)
    @ApiOperation("copy settings from a task type")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> copySettings(@PathVariable String taskTypeId, @RequestBody Map<String, Object> data) {

        Map<String, Object> response;
        if (data.get("sourceTaskId") instanceof Integer) {
            int sourceTaskTypeId = (int) data.get("sourceTaskId");
            response = taskTypeService.copySettings(taskTypeId, String.valueOf(sourceTaskTypeId));
        } else {
            throw new NumberFormatException("Incorrect format of task type id, expecting string");
        }
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    // Selected
    @RequestMapping(value = "/sub_service/task_types", method = RequestMethod.GET)
    @ApiOperation("Get Available Tasks in SubService of Organization")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAvailableTaskSubServiceOrganization(@PathVariable Long subServiceId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                taskTypeService.getAvailableTaskToSubServiceOfOrganization(subServiceId));
    }

    // Selected
    @RequestMapping(value = UNIT_URL+"/sub_service/{subServiceId}/task_types", method = RequestMethod.GET)
    @ApiOperation("Get Selected Tasks in SubService of Organization")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTaskTypesOfOrganizations(@PathVariable long unitId, @PathVariable long subServiceId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                taskTypeService.getTaskTypesOfOrganizations(unitId, subServiceId));
    }


    // ---------------TaskType Creation data----------------------------//

    // MapPointers
    @RequestMapping(value = "/task_type/map_pointers/", method = RequestMethod.GET)
    @ApiOperation("Get Map Pointer symbols")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getMapPointers() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, mapPointerService.getAllMapPointer());
    }


    // MapPointers
    @RequestMapping(value = "/task_type/map_pointers/{pointerId}", method = RequestMethod.GET)
    @ApiOperation("Get Map Pointer symbols")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getMapPointersById(@PathVariable String pointerId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, mapPointerService.getMapPointer(pointerId));
    }


    @RequestMapping(value = "/task_type/map_pointers/", method = RequestMethod.POST)
    @ApiOperation("Get Map Pointer symbols")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addMapPointers(@RequestBody MapPointer mapPointer) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, mapPointerService.addMapPointer(mapPointer));
    }


    @RequestMapping(value = "/task_types/{taskTypeId}/organization_type", method = RequestMethod.PUT)
    @ApiOperation("linking of task types with an organization types")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> linkWithOrganizationTypes(@PathVariable String taskTypeId, @RequestBody TaskType taskType, @RequestParam("isSelected") boolean isSelected) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, taskTypeService.linkWithOrganizationTypes(taskTypeId, taskType.getOrganizationSubTypes(), isSelected));

    }

    @RequestMapping(value = "/unit/{unitId}/task_type/{taskTypeId}/sla_config", method = RequestMethod.PUT)
    @ApiOperation("Save TaskType Sla Config for Unit")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveTaskTypeSlaConfig(@PathVariable("unitId") Long unitId, @PathVariable String taskTypeId,
                                                                     @RequestBody TaskTypeSlaConfigDTO taskTypeSlaConfigDTO) {
        /*Map<String, Object> response = taskTypeService.saveTaskTypeSlaConfig(unitId, taskTypeId, taskTypeSlaConfig);

        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP);
        }*/
        return ResponseHandler.generateResponse(HttpStatus.OK, true, taskTypeService.saveTaskTypeSlaConfig(unitId, taskTypeId, taskTypeSlaConfigDTO));
    }

    @RequestMapping(value = "/unit/{unitId}/task_type/{taskTypeId}/sla_config", method = RequestMethod.GET)
    @ApiOperation("GET TaskType Sla Config for Unit")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTaskTypeSlaConfig(@PathVariable("unitId") Long unitId, @PathVariable String taskTypeId) {
        Map<String, Object> response = taskTypeService.getTaskTypeSlaConfig(unitId, taskTypeId);

        /*if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);*/

        return (response == null) ? ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP) : ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @RequestMapping(value = COUNTRY_URL + "/task_type/{taskTypeId}/sla_config", method = RequestMethod.GET)
    @ApiOperation("GET TaskType Sla Config for Country")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTaskTypeSlaConfigForCountry(@PathVariable BigInteger taskTypeId,@PathVariable Long countryId) {

        return  ResponseHandler.generateResponse(HttpStatus.OK, true, taskTypeService.getTaskTypeSlaConfigForCountry(taskTypeId,countryId));
    }

    @RequestMapping(value = COUNTRY_URL + "/task_type/{taskTypeId}/sla_config", method = RequestMethod.PUT)
    @ApiOperation("Save TaskType Sla Config for Unit")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveTaskTypeSlaConfigForCountry(@PathVariable BigInteger taskTypeId,
                                                                               @RequestBody TaskTypeSlaConfigDTO taskTypeSlaConfigDTO,
                                                                               @PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, taskTypeService.saveTaskTypeSlaConfigForCountry(countryId, taskTypeId, taskTypeSlaConfigDTO));
    }

    @RequestMapping(value = "/task_types/{taskTypeId}/photo", method = RequestMethod.POST)
    @ApiOperation("upload portrait")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> uploadPhoto(@PathVariable String taskTypeId, @RequestParam("file") MultipartFile file) {
        if (file != null && file.getSize() == 0) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, Collections.EMPTY_MAP);
        }
        Map<String, Object> iconMap = taskTypeService.uploadPhoto(taskTypeId, file);
        if (iconMap == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, iconMap);
    }

    /**
     * @param serviceIds
     * @param organizationId
     * @return
     * @auther anil maurya
     * this endpoint called from user micro service
     */
    @RequestMapping(value = "/task_types/getAllAvlSkill", method = RequestMethod.POST)
    @ApiOperation("getAllAvlSkill")
    public ResponseEntity<Map<String, Object>> getAllAvailableSkillsTaskType(@RequestBody List<Long> serviceIds, @PathVariable Long organizationId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, taskTypeService
                .getTaskTypeList(serviceIds, organizationId));
    }

    /**
     * @param subServiceId
     * @param unitId
     * @param type
     * @return
     * @auther anil maurya
     * bring this endpoint from organization controller
     */
    @ApiOperation(value = "Get Available Task from serviceId")
    @RequestMapping(value = "/unit/{unitId}/service/{subServiceId}/task_type", method = RequestMethod.GET)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTaskTypes(@PathVariable long subServiceId, @PathVariable long unitId, @RequestParam("type") String type) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                taskTypeService.getTaskTypes(unitId, subServiceId, type));
    }

    @ApiOperation(value = "Add/ Remove TaskType to Organization")
    @RequestMapping(value = "/unit/{unitId}/service/{subServiceId}/task_type", method = RequestMethod.POST)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addTaskTypeInOrganization(@PathVariable long organizationId, @PathVariable long unitId, @PathVariable long subServiceId,
                                                                         @RequestBody Map<String, Object> data, @RequestParam("type") String type) throws CloneNotSupportedException {
        String taskTypeId = data.get("taskTyp/task_types/getAllAvlSkilleId") + "";
        boolean isSelected = (boolean) data.get("isSelected");
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                taskTypeService.updateTaskType(unitId, subServiceId, taskTypeId, isSelected, type));
    }

    @ApiOperation(value = "Add/ Remove TaskType to Organization")
    @RequestMapping(value = "/task_type/{taskTypeId}/clone", method = RequestMethod.POST)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createCopyForTaskType(@PathVariable BigInteger taskTypeId,
                                                                     @RequestBody TaskTypeCopyDTO taskTypeCopyDTO) throws CloneNotSupportedException {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,
                taskTypeService.createCopiesForTaskType(taskTypeId, taskTypeCopyDTO.getTaskTypeNames()));
    }


    @ApiOperation(value = "Update taskType setting for Staff")
    @PutMapping(value = UNIT_URL+"/staff/{staffId}/task_type_setting")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateTaskTypeSettingForStaff(@PathVariable Long staffId,@RequestBody TaskTypeSettingDTO taskTypeSettingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,
                taskTypeService.updateOrCreateTaskTypeSettingForStaff(staffId,taskTypeSettingDTO));
    }

    @ApiOperation(value = "Update taskType setting for Staff")
    @PutMapping(value = UNIT_URL+"/client/{clientId}/task_type_setting")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateTaskTypeSettingForClient(@PathVariable Long clientId,@RequestBody TaskTypeSettingDTO taskTypeSettingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,
                taskTypeService.updateOrCreateTaskTypeSettingForClient(clientId,taskTypeSettingDTO));
    }


    /*@ApiOperation(value = "get taskType setting for Staff")
    @GetMapping(value = UNIT_URL+"/staff/{staffId}/task_type_setting")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTaskTypeSettingForStaff(@PathVariable Long staffId) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,
                taskTypeService.getTaskTypeSettingByStaff(staffId));
    }*/

    @ApiOperation(value = "get taskType of organisation")
    @GetMapping(value = UNIT_URL+"/staff/{staffId}/staff_task_type_setting")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTaskTypeOfOrganisationAndStaffSetting(@PathVariable Long unitId,@PathVariable Long staffId) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,
                taskTypeService.getTaskTypeByOrganisationAndStaffSetting(unitId,staffId));
    }

    @ApiOperation(value = "get taskType of organisation")
    @GetMapping(value = UNIT_URL+"/client/{clientId}/client_task_type_setting")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTaskTypeOfOrganisationAndClientSetting(@PathVariable Long unitId,@PathVariable Long clientId) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,
                taskTypeService.getTaskTypeByOrganisationAndClientSetting(unitId,clientId));
    }

}
