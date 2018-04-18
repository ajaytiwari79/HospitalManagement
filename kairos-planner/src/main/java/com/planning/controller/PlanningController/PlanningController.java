package com.planning.controller.PlanningController;

import com.planning.commonUtil.OptaNotFoundException;
import com.planning.commonUtil.ResponseHandler;
import com.planning.commonUtil.StaticField;
import com.planning.responseDto.PlanningDto.shiftPlanningDto.RecomendationPlanningDTO;
import com.planning.responseDto.PlanningDto.taskplanning.TaskPlanningDTO;
import com.planning.responseDto.config.SolverConfigDTO;
import com.planning.responseDto.locationDto.OptaLocationDTO;
import com.planning.service.config.SolverConfigService;
import com.planning.service.locationService.GraphHopperService;
import com.planning.service.shiftPlanningService.ShiftPlanningService;
import com.planning.service.shiftPlanningService.ShiftRequestPhasePlanningSolutionService;
import com.planning.service.taskPlanningService.PlanningService;
import com.planning.service.taskPlanningService.TaskPlanningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(StaticField.PLANNING)
public class PlanningController {

	private static final Logger log = LoggerFactory.getLogger(PlanningController.class);

	@Autowired private TaskPlanningService taskPlanningService;
	@Autowired private ShiftPlanningService shiftPlanningService;
	@Autowired private PlanningService planningService;
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
				planningService.submitTaskPlanningProblem(taskPlanningDTO));
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
				planningService.getPlanningProblemByid(id));
	}

	@RequestMapping(value = "/getAllSolverConfig", method = RequestMethod.GET)
	Map<String, Object> getAllSolverConfig(@RequestParam Long unitId) {
		return ResponseHandler.generateResponse(StaticField.FETCH_SUCCESS, HttpStatus.ACCEPTED, false,solverConfigService.getAllSolverConfig(unitId));
	}

	@RequestMapping(value = "/saveSolverConfig", method = RequestMethod.POST)
	Map<String, Object> getAllSolverConfig(@RequestBody SolverConfigDTO solverConfigDTO) {
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
	Map<String, Object> saveDefaultSolverConfig(@RequestBody SolverConfigDTO solverConfigDTO) {
		return ResponseHandler.generateResponse(StaticField.SAVE_SUCCESS, HttpStatus.ACCEPTED, false,solverConfigService.saveDefaultSolverConfig(solverConfigDTO));
	}


	@RequestMapping(value = "/verifyAddress", method = RequestMethod.POST)
	Map<String, Object> verifyAddress(@RequestBody OptaLocationDTO optaLocationDTO) {
		return ResponseHandler.generateResponse(StaticField.VERIFY_ADDRESS_SUCCESS, HttpStatus.ACCEPTED, false, graphHopperService.getLatLongByAddress(optaLocationDTO));
	}





	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public void handleTodoNotFound(OptaNotFoundException ex) {
		log.error("Handling error with message: {}", ex.getMessage());
	}


}
