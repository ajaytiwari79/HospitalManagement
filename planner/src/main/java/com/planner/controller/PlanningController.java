package com.planner.controller;

import com.kairos.dto.planner.planninginfo.PlanningSubmissionDTO;
import com.kairos.dto.planner.vrp.vrpPlanning.VrpTaskPlanningDTO;
import com.planner.commonUtil.OptaNotFoundException;
import com.planner.commonUtil.ResponseHandler;
import com.planner.commonUtil.StaticField;
import com.planner.service.taskPlanningService.PlannerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.Map;

import static com.planner.constants.ApiConstants.API_UNIT_URL;

@RestController
@RequestMapping(API_UNIT_URL + "/planner")
public class PlanningController {
	private static final Logger log = LoggerFactory.getLogger(PlanningController.class);
	@Autowired private PlannerService plannerService;
	@RequestMapping(value = "/start", method = RequestMethod.POST)
    	ResponseEntity<Map<String, Object>> startShiftPlanningSolver(@RequestBody PlanningSubmissionDTO planningSubmissionDTO, @PathVariable Long unitId) {
        plannerService.submitShiftPlanningProblem(unitId,planningSubmissionDTO);
		return ResponseHandler.generateResponse("save Data sucessFully", HttpStatus.ACCEPTED);
	}

/*

	private static final Logger log = LoggerFactory.getLogger(PlanningController.class);

	@Autowired private TaskPlanningService taskPlanningService;
	@Autowired private ShiftPlanningService shiftPlanningService;
	@Autowired private PlannerService plannerService;
	@Autowired private GraphHopperService graphHopperService;
	@Autowired private SolverConfigService solverConfigService;
	@Autowired private ShiftRequestPhasePlanningSolutionService shiftPlanningSolutionService;



	@RequestMapping(value = "/submitXml", method = RequestMethod.POST)
	Map<String, Object> submitXml(@RequestBody Map requestData) {
		log.info("call");
		return ResponseHandler.generateResponse(StaticField.SAVE_SUCCESS, HttpStatus.ACCEPTED, false,
				taskPlanningService.renderSolutionFromXML((String) requestData.get("xml")));
	}

	@RequestMapping(value = "/shiftPlanningXml", method = RequestMethod.POST)
	Map<String, Object> shiftPlanningXml(@RequestBody Map requestData) {
		log.info("call");
		return ResponseHandler.generateResponse("save Data sucessFully", HttpStatus.ACCEPTED, false,
				shiftPlanningService.renderShiftSolutionFromXML((String) requestData.get("xml")));
	}

	@RequestMapping(value = "/submitTaskPlanningProblem", method = RequestMethod.POST)
	Map<String, Object> submitTaskPlanningProblem(@RequestBody TaskPlanningDTO taskPlanningDTO) {
		log.info("call");
		return ResponseHandler.generateResponse(StaticField.SAVE_SUCCESS, HttpStatus.ACCEPTED, false,
				plannerService.submitTaskPlanningProblem(taskPlanningDTO));
	}

	@RequestMapping(value = "/submitRecomendationProblem", method = RequestMethod.POST)
	Map<String, Object> submitRecomendationProblem(@RequestBody Map planningDTO) {
		log.info("call");
		return ResponseHandler.generateResponse(StaticField.SAVE_SUCCESS, HttpStatus.ACCEPTED, false,
				shiftPlanningSolutionService.getShiftPlanningSolution(planningDTO));
	}


	@RequestMapping(value = "/getProblemIds", method = RequestMethod.GET)
	Map<String, Object> getProblemIds() {
		return ResponseHandler.generateResponse(StaticField.FETCH_SUCCESS, HttpStatus.ACCEPTED, false,
				null);//taskPlanningService.getAllPlanning());
	}

	@RequestMapping(value = "/getStatus", method = RequestMethod.GET)
	Map<String, Object> getStatus(@RequestParam String id) {
		return ResponseHandler.generateResponse(StaticField.SAVE_SUCCESS, HttpStatus.ACCEPTED, false,
				plannerService.getPlanningProblemByid(id));
	}

	@RequestMapping(value = "/getAllSolverConfig", method = RequestMethod.GET)
	Map<String, Object> getAllSolverConfig(@RequestParam Long unitId) {
		return ResponseHandler.generateResponse(StaticField.FETCH_SUCCESS, HttpStatus.ACCEPTED, false,solverConfigService.getAllSolverConfig(unitId));
	}

	@RequestMapping(value = "/saveSolverConfig", method = RequestMethod.POST)
	Map<String, Object> getAllSolverConfig(@RequestBody SolverConfigWTADTO solverConfigDTO) {
		return ResponseHandler.generateResponse(StaticField.SAVE_SUCCESS, HttpStatus.ACCEPTED, false,solverConfigService.saveSolverConfig(solverConfigDTO));
	}

	@RequestMapping(value = "/getOneSolverConfig", method = RequestMethod.GET)
	Map<String, Object> getOneSolverConfig(@RequestParam String solverConfigId) {
		return ResponseHandler.generateResponse(StaticField.FETCH_SUCCESS, HttpStatus.ACCEPTED, false,solverConfigService.getOne(solverConfigId));
	}

	@RequestMapping(value = "/saveRulesByJson", method = RequestMethod.POST)
	Map<String, Object> saveRulesByJson(@RequestBody Map requestMap) {
		return ResponseHandler.generateResponse(StaticField.SAVE_SUCCESS, HttpStatus.ACCEPTED, false,solverConfigService.saveRulesByJson(requestMap));
	}

	@RequestMapping(value = "/updateRulesByJson", method = RequestMethod.POST)
	Map<String, Object> updateRulesByJson(@RequestBody Map requestMap) {
		return ResponseHandler.generateResponse(StaticField.SAVE_SUCCESS, HttpStatus.ACCEPTED, false,solverConfigService.updateRulesByJson(requestMap));
	}

	@RequestMapping(value = "/saveDefaultSolverConfigByJson", method = RequestMethod.POST)
	Map<String, Object> saveDefaultSolverConfig(@RequestBody SolverConfigWTADTO solverConfigDTO) {
		return ResponseHandler.generateResponse(StaticField.SAVE_SUCCESS, HttpStatus.ACCEPTED, false,solverConfigService.saveDefaultSolverConfig(solverConfigDTO));
	}


	@RequestMapping(value = "/verifyAddress", method = RequestMethod.POST)
	Map<String, Object> verifyAddress(@RequestBody OptaLocationDTO optaLocationDTO) {
		return ResponseHandler.generateResponse(StaticField.VERIFY_ADDRESS_SUCCESS, HttpStatus.ACCEPTED, false, graphHopperService.getLatLongByAddress(optaLocationDTO));
	}


*/

	@PostMapping(value = "/submitVRPPlanning")
	ResponseEntity<Map<String, Object>> submitVRPPlanning(@RequestBody VrpTaskPlanningDTO vrpTaskPlanningDTO) {
		 plannerService.submitVRPPlanning(vrpTaskPlanningDTO);
		return ResponseHandler.generateResponse(StaticField.VRPPROBLEM_SUBMIT, HttpStatus.ACCEPTED);
	}

	@DeleteMapping(value = "/vrp/{solverConfigId}")
	ResponseEntity<Map<String, Object>> stopVRPPlanning(@PathVariable BigInteger solverConfigId) {
		plannerService.stopVRPPlanning(solverConfigId);
		return ResponseHandler.generateResponse(StaticField.VRPPROBLEM_SUBMIT, HttpStatus.ACCEPTED);
	}

	@GetMapping(value = "/vrp/{solverConfigId}")
	ResponseEntity<Map<String, Object>> getSolutionBySolverConfigId(@PathVariable BigInteger solverConfigId) {
		return ResponseHandler.generateResponseWithData(StaticField.VRPPROBLEM_SUBMIT, HttpStatus.ACCEPTED,plannerService.getSolutionBySolverConfigId(solverConfigId));
	}

	@GetMapping(value = "/vrp/{solverConfigId}/get_indictment")
	ResponseEntity<Map<String, Object>> getIndictmentBySolverConfigId(@PathVariable BigInteger solverConfigId) {
		return ResponseHandler.generateResponseWithData(StaticField.VRPPROBLEM_SUBMIT, HttpStatus.ACCEPTED,plannerService.getIndictmentBySolverConfigId(solverConfigId));
	}






	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public void handleTodoNotFound(OptaNotFoundException ex) {
		//log.error("Handling error with message: {}", ex.getMessage());
	}


}
