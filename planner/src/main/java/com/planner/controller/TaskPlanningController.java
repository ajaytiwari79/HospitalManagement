package com.planner.controller;


import com.planner.commonUtil.StaticField;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(StaticField.INTEGRATION)
@Deprecated
public class TaskPlanningController {

   /* private static final Logger log = LoggerFactory.getLogger(TaskPlanningController.class);

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private LocationService locationService;
    @Autowired
    private PlanningTaskService taskService;
    @Autowired
    private TaskTypeService taskTypeService;
    @Autowired
    private TaskStaffService staffService;
    @Autowired
    private ShiftService shiftService;
    @Autowired
    private CitizenService citizenService;
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private SkillService skillService;
    @Autowired private WorkingTimeAgreementService workingTimeAgreementService;


    @RequestMapping(value = "/saveTasks", method = RequestMethod.POST)
    Map<String, Object> saveTasks(@RequestBody List<OptaTaskDTO> optaTaskDTOS) {
        return ResponseHandler.generateResponse(StaticField.SAVE_SUCCESS, HttpStatus.ACCEPTED, false,taskService.saveTasks(optaTaskDTOS));
    }

    @RequestMapping(value = "/saveTask", method = RequestMethod.POST)
    Map<String, Object> saveTask(@RequestBody OptaTaskDTO optaTaskDTO) {
        return ResponseHandler.generateResponse(StaticField.SAVE_SUCCESS, HttpStatus.ACCEPTED, false,taskService.saveTask(optaTaskDTO));
    }

    @RequestMapping(value = "/updateTasks", method = RequestMethod.PUT)
    Map<String, Object> updateTasks(@RequestBody List<OptaTaskDTO> optaTaskDTOS) {
        return ResponseHandler.generateResponse(StaticField.UPDATE_SUCCESS, HttpStatus.ACCEPTED, false,taskService.updateTasks(optaTaskDTOS));
    }

    @RequestMapping(value = "/updateTask", method = RequestMethod.PUT)
    Map<String, Object> updateTask(@RequestBody OptaTaskDTO OptaTaskDTO) {
        return ResponseHandler.generateResponse(StaticField.UPDATE_SUCCESS, HttpStatus.ACCEPTED, false,taskService.updateTask(OptaTaskDTO));
    }

    @RequestMapping(value = "/deleteTask", method = RequestMethod.DELETE)
    Map<String, Object> deleteTask(@RequestBody OptaTaskDTO OptaTaskDTO) {
        return ResponseHandler.generateResponse(StaticField.DELETE_SUCCESS, HttpStatus.ACCEPTED, false,taskService.deleteByObject(OptaTaskDTO) );
    }

    @RequestMapping(value = "/saveTaskTypes", method = RequestMethod.POST)
    Map<String, Object> saveTaskTypes(@RequestBody List<OptaTaskTypeDTO> optaTaskTypeDTOS) {
        return ResponseHandler.generateResponse(StaticField.SAVE_SUCCESS, HttpStatus.ACCEPTED, false,taskTypeService.saveTaskTypes(optaTaskTypeDTOS));
    }

    @RequestMapping(value = "/saveTaskType", method = RequestMethod.POST)
    Map<String, Object> saveTaskTypes(@RequestBody OptaTaskTypeDTO optaTaskTypeDTO) {
        return ResponseHandler.generateResponse(StaticField.SAVE_SUCCESS, HttpStatus.ACCEPTED, false,taskTypeService.saveTaskType(optaTaskTypeDTO));
    }

    @RequestMapping(value = "/updateTaskType", method = RequestMethod.PUT)
    Map<String, Object> updateTaskType(@RequestBody OptaTaskTypeDTO optaTaskTypeDTO) {
        return ResponseHandler.generateResponse(StaticField.UPDATE_SUCCESS, HttpStatus.ACCEPTED, false,taskTypeService.updateTaskType(optaTaskTypeDTO));
    }

    @RequestMapping(value = "/deleteTaskType", method = RequestMethod.DELETE)
    Map<String, Object> deleteTaskType(@RequestBody OptaTaskTypeDTO optaTaskTypeDTO) {
        return ResponseHandler.generateResponse(StaticField.DELETE_SUCCESS, HttpStatus.ACCEPTED, false,taskTypeService.deleteByObject(optaTaskTypeDTO));
    }

    @RequestMapping(value = "/saveCitizens", method = RequestMethod.POST)
    Map<String, Object> saveCitizens(@RequestBody List<OptaCitizenDTO> optaCitizenDTOS) {
        return ResponseHandler.generateResponse(StaticField.SAVE_SUCCESS, HttpStatus.ACCEPTED, false,citizenService.saveCitizens(optaCitizenDTOS));
    }

    @RequestMapping(value = "/updateCitizen", method = RequestMethod.PUT)
    Map<String, Object> updateCitizen(@RequestBody OptaCitizenDTO optaCitizenDTO) {
        return ResponseHandler.generateResponse(StaticField.UPDATE_SUCCESS, HttpStatus.ACCEPTED, false,citizenService.updateCitizen(optaCitizenDTO));
    }

    @RequestMapping(value = "/deleteCitizen", method = RequestMethod.DELETE)
    Map<String, Object> deleteCitizen(@RequestBody OptaCitizenDTO optaCitizenDTO) {
        return ResponseHandler.generateResponse(StaticField.DELETE_SUCCESS, HttpStatus.ACCEPTED, false,citizenService.deleteByObject(optaCitizenDTO));
    }

    @RequestMapping(value = "/saveVehicles", method = RequestMethod.POST)
    Map<String, Object> saveVehicles(@RequestBody List<OptaVehicleDTO> optaVehicleDTOS) {
        return ResponseHandler.generateResponse(StaticField.SAVE_SUCCESS, HttpStatus.ACCEPTED, false,vehicleService.saveVehicles(optaVehicleDTOS));
    }

    @RequestMapping(value = "/updateVehicle", method = RequestMethod.PUT)
    Map<String, Object> updateVehicle(@RequestBody OptaVehicleDTO optaVehicleDTO) {
        return ResponseHandler.generateResponse(StaticField.UPDATE_SUCCESS, HttpStatus.ACCEPTED, false,vehicleService.updateVehicle(optaVehicleDTO));
    }

    @RequestMapping(value = "/deleteVehicle", method = RequestMethod.DELETE)
    Map<String, Object> deleteVehicle(@RequestBody OptaVehicleDTO optaVehicleDTO) {
        return ResponseHandler.generateResponse(StaticField.DELETE_SUCCESS, HttpStatus.ACCEPTED, false,vehicleService.deleteByObject(optaVehicleDTO));
    }

    @RequestMapping(value = "/saveStaffs", method = RequestMethod.POST)
    Map<String, Object> saveStaffs(@RequestBody List<OptaStaffDTO> optaStaffDTOS) {
        return ResponseHandler.generateResponse(StaticField.SAVE_SUCCESS, HttpStatus.ACCEPTED, false,staffService.saveStaffs(optaStaffDTOS));
    }

    @RequestMapping(value = "/saveStaff", method = RequestMethod.POST)
    Map<String, Object> saveStaff(@RequestBody OptaStaffDTO optaStaffDTO) {
        return ResponseHandler.generateResponse(StaticField.SAVE_SUCCESS, HttpStatus.ACCEPTED, false,staffService.saveStaff(optaStaffDTO));
    }

    @RequestMapping(value = "/updateStaff", method = RequestMethod.PUT)
    Map<String, Object> updateStaff(@RequestBody OptaStaffDTO optaStaffDTO) {
        return ResponseHandler.generateResponse(StaticField.UPDATE_SUCCESS, HttpStatus.ACCEPTED, false,staffService.updateStaff(optaStaffDTO));
    }

    @RequestMapping(value = "/deleteStaff", method = RequestMethod.DELETE)
    Map<String, Object> deleteStaff(@RequestBody OptaStaffDTO optaStaffDTO) {
        return ResponseHandler.generateResponse(StaticField.DELETE_SUCCESS, HttpStatus.ACCEPTED, false,staffService.deleteByObject(optaStaffDTO));
    }

    @RequestMapping(value = "/saveShifts", method = RequestMethod.POST)
    Map<String, Object> saveShifts(@RequestBody List<OptaShiftDTO> optaShiftDTOS) {
        return ResponseHandler.generateResponse(StaticField.SAVE_SUCCESS, HttpStatus.ACCEPTED, false,shiftService.saveShifts(optaShiftDTOS));
    }

    @RequestMapping(value = "/updateShift", method = RequestMethod.PUT)
    Map<String, Object> updateShift(@RequestBody OptaShiftDTO optaShiftDTO) {
        return ResponseHandler.generateResponse(StaticField.UPDATE_SUCCESS, HttpStatus.ACCEPTED, false,shiftService.updateShift(optaShiftDTO));
    }

    @RequestMapping(value = "/deleteShift", method = RequestMethod.DELETE)
    Map<String, Object> deleteShift(@RequestBody OptaShiftDTO optaShiftDTO) {
        return ResponseHandler.generateResponse(StaticField.DELETE_SUCCESS, HttpStatus.ACCEPTED, false,shiftService.deleteByObject(optaShiftDTO));
    }

    @RequestMapping(value = "/saveSkills", method = RequestMethod.POST)
    Map<String, Object> saveSkills(@RequestBody List<OptaSkillDTO> optaSkillDTOS) {
        return ResponseHandler.generateResponse(StaticField.SAVE_SUCCESS, HttpStatus.ACCEPTED, false,skillService.saveSkills(optaSkillDTOS));
    }

    @RequestMapping(value = "/saveSkill", method = RequestMethod.POST)
    Map<String, Object> saveSkills(@RequestBody OptaSkillDTO optaSkillDTO) {
        return ResponseHandler.generateResponse(StaticField.SAVE_SUCCESS, HttpStatus.ACCEPTED, false,skillService.saveSkill(optaSkillDTO));
    }

    @RequestMapping(value = "/updateSkill", method = RequestMethod.PUT)
    Map<String, Object> updateSkill(@RequestBody OptaSkillDTO optaSkillDTO) {
        return ResponseHandler.generateResponse(StaticField.UPDATE_SUCCESS, HttpStatus.ACCEPTED, false,skillService.updateSkill(optaSkillDTO));
    }

    @RequestMapping(value = "/deleteSkill", method = RequestMethod.DELETE)
    Map<String, Object> deleteSkill(@RequestBody OptaSkillDTO optaSkillDTO) {
        return ResponseHandler.generateResponse(StaticField.DELETE_SUCCESS, HttpStatus.ACCEPTED, false,skillService.deleteByObject(optaSkillDTO));
    }

    @RequestMapping(value = "/saveLocationDistance", method = RequestMethod.POST)
    Map<String, Object> saveLocationDistance() {
        applicationContext.getBean(LocationService.class).saveLocationDistances();
        return ResponseHandler.generateResponse(StaticField.DELETE_SUCCESS, HttpStatus.ACCEPTED, false,null);
    }*/

    /*@RequestMapping(value = "/saveWorkingTimeAgreement", method = RequestMethod.POST)
    Map<String, Object> saveWorkingTimeAgreement(@RequestBody WorkingTimeAggrementDTO workingTimeAggrementDTO) {
        return ResponseHandler.generateResponse(StaticField.DELETE_SUCCESS, HttpStatus.ACCEPTED, false, workingTimeAgreementService.saveWorkingTimeAgreement(workingTimeAggrementDTO));
    }

    @RequestMapping(value = "/updateWorkTimeAgreement", method = RequestMethod.PUT)
    Map<String, Object> updateWorkingTimeAgreement(@RequestBody WorkingTimeAggrementDTO workingTimeAggrementDTO) {
        return ResponseHandler.generateResponse(StaticField.UPDATE_SUCCESS, HttpStatus.ACCEPTED, false, workingTimeAgreementService.updateWorkingTimeAgreement(workingTimeAggrementDTO));
    }

    @RequestMapping(value = "/deleteWorkingTimeAgreement", method = RequestMethod.PUT)
    Map<String, Object> deleteWorkingTimeAgreement(@RequestBody WorkingTimeAggrementDTO workingTimeAggrementDTO) {
        return ResponseHandler.generateResponse(StaticField.UPDATE_SUCCESS, HttpStatus.ACCEPTED, false, workingTimeAgreementService.deleteWorkingTimeAgreement(workingTimeAggrementDTO));
    }
*/
}
