package com.planner.controller;

import com.kairos.dto.planner.planninginfo.PlanningSubmissionDTO;
import com.kairos.dto.planner.vrp.vrpPlanning.VrpTaskPlanningDTO;
import com.planner.commonUtil.OptaNotFoundException;
import com.planner.commonUtil.ResponseHandler;
import com.planner.commonUtil.StaticField;
import com.planner.service.taskPlanningService.PlannerService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Map;

import static com.planner.constants.ApiConstants.API_UNIT_URL;
import static com.planner.constants.ApiConstants.SHIFTPLANNING;

@RestController
@RequestMapping(API_UNIT_URL + "/planner")
public class PlanningController {
	private static final Logger log = LoggerFactory.getLogger(PlanningController.class);
	@Autowired private PlannerService plannerService;
	@Inject
	private TemplateEngine templateEngine;



	@RequestMapping(value = SHIFTPLANNING+"/start", method = RequestMethod.POST)
    	ResponseEntity<Map<String, Object>> startShiftPlanningSolver(@RequestBody PlanningSubmissionDTO planningSubmissionDTO, @PathVariable Long unitId) {
        plannerService.submitShiftPlanningProblem(unitId,planningSubmissionDTO);
		return ResponseHandler.generateResponse("saveEntity Data sucessFully", HttpStatus.OK);
	}

	@PostMapping(value = "/submitVRPPlanning")
	ResponseEntity<Map<String, Object>> submitVRPPlanning(@RequestBody VrpTaskPlanningDTO vrpTaskPlanningDTO) {
		 plannerService.submitVRPPlanning(vrpTaskPlanningDTO);
		return ResponseHandler.generateResponse(StaticField.VRPPROBLEM_SUBMIT, HttpStatus.OK);
	}

	@DeleteMapping(value = "/vrp/{solverConfigId}")
	ResponseEntity<Map<String, Object>> stopVRPPlanning(@PathVariable BigInteger solverConfigId) {
		plannerService.stopVRPPlanning(solverConfigId);
		return ResponseHandler.generateResponse(StaticField.VRPPROBLEM_SUBMIT, HttpStatus.OK);
	}

	@GetMapping(value = "/vrp/{solverConfigId}")
	ResponseEntity<Map<String, Object>> getSolutionBySolverConfigId(@PathVariable BigInteger solverConfigId) {
		return ResponseHandler.generateResponseWithData(StaticField.VRPPROBLEM_SUBMIT, HttpStatus.OK,plannerService.getSolutionBySolverConfigId(solverConfigId));
	}

	@GetMapping(value = "/vrp/{solverConfigId}/get_indictment")
	ResponseEntity<Map<String, Object>> getIndictmentBySolverConfigId(@PathVariable BigInteger solverConfigId) {
		return ResponseHandler.generateResponseWithData(StaticField.VRPPROBLEM_SUBMIT, HttpStatus.OK,plannerService.getIndictmentBySolverConfigId(solverConfigId));
	}

	@ApiOperation("test")
	@GetMapping("/test")
	public String test(){
		return templateEngine.process("test.html", new Context());
	}






	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public void handleTodoNotFound(OptaNotFoundException ex) {
		//log.error("Handling error with message: {}", ex.getMessage());
	}


}
