package com.planner.controller;

import com.kairos.dto.planner.planninginfo.PlanningProblemDTO;
import com.planner.commonUtil.ResponseHandler;
import com.planner.service.planning_problem.PlanningProblemService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.planner.constants.ApiConstants.API_PARENT_ORGANIZATION_COUNTRY_PLANNING_PROBLEM_URL;

@RestController
@RequestMapping(API_PARENT_ORGANIZATION_COUNTRY_PLANNING_PROBLEM_URL)
public class PlanningProblemController {
    @Inject
    private PlanningProblemService planningProblemService;

    @PostMapping
    @ApiOperation("Create PlanningProblem")
    public ResponseEntity<Map<String, Object>> createPlanningProblem(@RequestBody PlanningProblemDTO planningProblemDTO) {
        planningProblemService.createPlanningProblem(planningProblemDTO);
        return ResponseHandler.generateResponse("Success", HttpStatus.CREATED);
    }

    
    @GetMapping(value = "/{planningProblemDTOId}")
    @ApiOperation("Get PlanningProblem")
    public ResponseEntity<Map<String, Object>> getPlanningProblem(@PathVariable String planningProblemDTOId) {
        return ResponseHandler.generateResponseWithData("Success", HttpStatus.FOUND,planningProblemService.getPlanningProblem(planningProblemDTOId));
    }

    @GetMapping
    @ApiOperation("GetAll PlanningProblem")
    public ResponseEntity<Map<String, Object>> getAllPlanningProblem() {
        return ResponseHandler.generateResponseWithData("Success", HttpStatus.FOUND,planningProblemService.getAllPlanningProblem());
    }

    /**
     * Always modification no object creation so,Patch
     * @param planningProblemDTO
     * @return
     */
    @PatchMapping
    @ApiOperation("Update PlanningProblem")
    public ResponseEntity<Map<String, Object>> updatePlanningProblem(@RequestBody PlanningProblemDTO planningProblemDTO) {
        planningProblemService.updatePlanningProblem(planningProblemDTO);
        return ResponseHandler.generateResponse("Success", HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{planningProblemDTOId}")
    @ApiOperation("Delete PlanningProblem")
    public ResponseEntity<Map<String, Object>> deletePlanningProblem(@PathVariable String planningProblemDTOId) {
        planningProblemService.deletePlanningProblem(planningProblemDTOId);
        return ResponseHandler.generateResponse("Success", HttpStatus.GONE);
    }

    //=====================================================================

    @PostMapping("/create_default_planningProblem")
    @ApiOperation("Create Default PlanningProblem")
    public ResponseEntity<Map<String, Object>> createDefaultPlanningProblem() {
        return ResponseHandler.generateResponseWithData("Success", HttpStatus.CREATED,planningProblemService.createDefaultPlanningProblem());
    }
}
