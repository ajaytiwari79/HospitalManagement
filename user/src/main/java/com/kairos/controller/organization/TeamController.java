    package com.kairos.controller.organization;

    import com.kairos.persistence.model.organization.team.TeamDTO;
import com.kairos.service.organization.TeamService;
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

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;
import static com.kairos.constants.AppConstants.TEAM;

    /**
     * Created by oodles on 18/10/16.
     */
    @RestController
    @RequestMapping(API_ORGANIZATION_UNIT_URL)
    public class TeamController {

        @Inject
        TeamService teamService;

        @ApiOperation(value = "Add Team in Unit")
        @RequestMapping(value = "/team", method = RequestMethod.POST)
        // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
        public ResponseEntity<Map<String, Object>> createTeam(@PathVariable Long unitId, @Validated @RequestBody TeamDTO teamDTO) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, teamService.createTeam(unitId, teamDTO));
        }

        @ApiOperation(value = "Update Team of Unit")
        @RequestMapping(value = "/team/{teamId}", method = RequestMethod.PUT)
        // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
        public ResponseEntity<Map<String, Object>> updateTeam(@PathVariable Long unitId, @PathVariable Long teamId, @Validated @RequestBody TeamDTO teamDTO) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, teamService.updateTeam(unitId, teamId, teamDTO));
        }

        @ApiOperation(value = "Get Team Details")
        @RequestMapping(value = "/team/{teamId}", method = RequestMethod.GET)
        // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
        public ResponseEntity<Map<String, Object>> getTeamDetails(@PathVariable Long teamId) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, teamService.getTeamDetails( teamId));
        }


        @ApiOperation(value = "Get All Teams")
        @RequestMapping(value = "/teams", method = RequestMethod.GET)
        // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
        public ResponseEntity<Map<String, Object>> getTeams(@PathVariable long unitId) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true,
                    teamService.getTeams(unitId));

        }

        @ApiOperation(value = "Delete Team By TeamId")
        @RequestMapping(value = "/team/{teamId}", method = RequestMethod.DELETE)
        // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
        public ResponseEntity<Map<String, Object>> deleteTeamByTeamId(@PathVariable Long unitId, @PathVariable Long teamId) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true,
                    teamService.deleteTeamByTeamId(teamId));
        }

        @ApiOperation(value = "Update Activities in Team")
        @RequestMapping(value = "/team/{teamId}/activities", method = RequestMethod.PUT)
        // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
        public ResponseEntity<Map<String, Object>> updateActivitiesOfTeam(@PathVariable Long teamId, @RequestParam(value = "activityIds") List<BigInteger> activityIds) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true,
                    teamService.updateActivitiesOfTeam(teamId, activityIds));
        }

        @ApiOperation(value = "Get Team Selected Skills")
        @RequestMapping(value = "/skill/{teamId}", method = RequestMethod.GET)
        // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
        public ResponseEntity<Map<String, Object>> getTeamSelectedSkills(@PathVariable Long teamId) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, teamService.getTeamSelectedSkills(teamId));
        }


        @ApiOperation(value = "Add Skill to Team")
        @RequestMapping(value = "/skill/{teamId}", method = RequestMethod.PUT)
        //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
        public ResponseEntity<Map<String, Object>> addTeamSkills(@PathVariable Long teamId, @RequestBody Map<String, Long[]> data) {
            Long[] skillIds = data.get("data");
            return ResponseHandler.generateResponse(HttpStatus.OK, true, teamService.addTeamSelectedSkills(teamId, skillIds));
        }

        @ApiOperation(value = "Get Team Selected Service")
        @RequestMapping(value = "/service/{teamId}", method = RequestMethod.GET)
        //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
        public ResponseEntity<Map<String, Object>> getTeamSelectedService(@PathVariable Long teamId) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, this.teamService.getTeamSelectedServices(teamId));
        }

        @ApiOperation(value = "Add Service to Team")
        @RequestMapping(value = "/service/{teamId}", method = RequestMethod.PUT)
        //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
        public ResponseEntity<Map<String, Object>> addTeamService(@PathVariable Long teamId, @RequestBody Map<String, Long[]> data) {
            Long[] serviceIds = data.get("data");
            return ResponseHandler.generateResponse(HttpStatus.OK, true, this.teamService.addTeamSelectedServices(teamId, serviceIds));
        }


        //---------------------------------------Staff--------------------------------------------------//

        // Team
        @ApiOperation(value = "Add Staff to Team")
        @RequestMapping(value = "/team/{teamId}/staff", method = RequestMethod.POST)
        // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
        public ResponseEntity<Map<String, Object>> addStaffInTeam(@PathVariable long unitId,@PathVariable long teamId, @RequestBody Map<String, Object> staffInfo, @RequestParam("type") String type) {
            if(TEAM.equalsIgnoreCase(type)){
                unitId = teamService.getOrganizationIdByTeamId(unitId);
            }
            long staffId = Long.parseLong((String) staffInfo.get("staffId"));
            boolean isAssigned = (boolean) staffInfo.get("isSelected");
            boolean isSuccess = teamService.addStaffInTeam(teamId, staffId, isAssigned, unitId);
            if (isSuccess) {
                return ResponseHandler.generateResponse(HttpStatus.OK, true,
                        true);
            }
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false,
                    false);
        }

        @ApiOperation(value = "Update Staffs in Team")
        @RequestMapping(value = "/team/{teamId}/update_staffs", method = RequestMethod.PUT)
        // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
        public ResponseEntity<Map<String, Object>> updateStaffInTeam(@PathVariable long teamId, @RequestBody List<Long> staffIds) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true,
                    teamService.updateStaffsInTeam(teamId,staffIds));
        }

        @ApiOperation(value = "get staff of unit")
        @RequestMapping(value = "/team/{teamId}/staff", method = RequestMethod.GET)
        //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
        public ResponseEntity<Map<String, Object>> getStaffOfUnit(@PathVariable long teamId) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true,
                    teamService.getStaffForImportInTeam(teamId));
        }

        @ApiOperation(value = "Get Staff in Team")
        @RequestMapping(value = "/staff/{teamI", method = RequestMethod.GET)
        //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
        public ResponseEntity<Map<String, Object>> getStaffOfTeam(@PathVariable Long teamID) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true,
                    teamService.getAllUsers(teamID));
        }

        @ApiOperation(value = "Get User StaffType")
        @RequestMapping(value = "/staff/user/{userId}", method = RequestMethod.GET)
        //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
        public ResponseEntity<Map<String, Object>> getUserStaffType(@PathVariable Long userId) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true,
                    teamService.getUserStaffType(userId));
        }

        @ApiOperation(value = "Get Organization Id by team")
        @RequestMapping(value = "/team/organizationId", method = RequestMethod.GET)
        // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
        public ResponseEntity<Map<String, Object>> getOrganizationIdByTeamId(@PathVariable Long unitId) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, teamService.getOrganizationIdByTeamId(unitId));
        }
    }
