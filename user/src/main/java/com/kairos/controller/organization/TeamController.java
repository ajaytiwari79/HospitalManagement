    package com.kairos.controller.organization;

    import com.kairos.persistence.model.organization.team.TeamDTO;
    import com.kairos.persistence.model.staff.StaffTeamDTO;
    import com.kairos.service.organization.TeamService;
    import com.kairos.utils.response.ResponseHandler;
    import io.swagger.annotations.ApiOperation;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.validation.annotation.Validated;
    import org.springframework.web.bind.annotation.*;

    import javax.inject.Inject;
    import java.math.BigInteger;
    import java.text.ParseException;
    import java.util.Map;
    import java.util.Set;

    import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;
    import static com.kairos.constants.AppConstants.TEAM;

    /**
     * Created by oodles on 18/10/16.
     */
    @RestController
    @RequestMapping(API_ORGANIZATION_UNIT_URL)
    public class TeamController {

        @Inject
        private TeamService teamService;

        @ApiOperation(value = "Add Team in Unit")
        @PostMapping(value = "/team")
        // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
        public ResponseEntity<Map<String, Object>> createTeam(@PathVariable Long unitId, @Validated @RequestBody TeamDTO teamDTO) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, teamService.createTeam(unitId, teamDTO));
        }

        @ApiOperation(value = "Update Team of Unit")
        @PutMapping(value = "/team/{teamId}")
        // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
        public ResponseEntity<Map<String, Object>> updateTeam(@PathVariable Long unitId, @PathVariable Long teamId, @Validated @RequestBody TeamDTO teamDTO) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, teamService.updateTeam(unitId, teamId, teamDTO));
        }

        @ApiOperation(value = "Get Team Details")
        @GetMapping(value = "/team/{teamId}")
        // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
        public ResponseEntity<Map<String, Object>> getTeamDetails(@PathVariable Long teamId) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, teamService.getTeamDetails( teamId));
        }

        @ApiOperation(value = "Get all teams of unit")
        @GetMapping(value = "/teams")
        // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
        public ResponseEntity<Map<String, Object>> getAllTeamByUnitId(@PathVariable Long unitId) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, teamService.getAllTeamsOfOrganization( unitId));
        }


        @ApiOperation(value = "Get All Teams with additional data")
        @GetMapping(value = "/teams_prerequisite")
        // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
        public ResponseEntity<Map<String, Object>> getTeams(@PathVariable long unitId) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true,
                    teamService.getTeamsAndPrerequisite(unitId));

        }

        @ApiOperation(value = "Delete Team By TeamId")
        @DeleteMapping(value = "/team/{teamId}")
        // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
        public ResponseEntity<Map<String, Object>> deleteTeamByTeamId(@PathVariable Long unitId, @PathVariable Long teamId) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true,
                    teamService.deleteTeamByTeamId(teamId));
        }

        @ApiOperation(value = "Update Activities in Team")
        @PutMapping(value = "/team/{teamId}/update_activities")
        // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
        public ResponseEntity<Map<String, Object>> updateActivitiesOfTeam(@PathVariable Long teamId,  @RequestBody Set<BigInteger> activityIds) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true,
                    teamService.updateActivitiesOfTeam(teamId, activityIds));
        }

        @ApiOperation(value = "Get Team Selected Skills")
        @GetMapping(value = "/skill/{teamId}")
        // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
        public ResponseEntity<Map<String, Object>> getTeamSelectedSkills(@PathVariable Long teamId) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, teamService.getTeamSelectedSkills(teamId));
        }


        @ApiOperation(value = "Add Skill to Team")
        @PutMapping(value = "/team/{teamId}/update_skills")
        //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
        public ResponseEntity<Map<String, Object>> addTeamSkills(@PathVariable Long teamId, @RequestBody Set<Long> skillIds) {
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
        public ResponseEntity<Map<String, Object>> updateStaffInTeam(@PathVariable long teamId, @RequestBody StaffTeamDTO staffDetails) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true,
                    teamService.updateStaffsInTeam(teamId,staffDetails));
        }

        @ApiOperation(value = "Remove Staff from Team")
        @DeleteMapping(value = "/team/{teamId}/remove_staff/{staffId}")
        // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
        public ResponseEntity<Map<String, Object>> removeStaffFromTeam(@PathVariable Long teamId,@PathVariable Long staffId ) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true,
                    teamService.removeStaffFromTeam(teamId,staffId));
        }

        @ApiOperation(value = "get staff of unit")
        @RequestMapping(value = "/team/{teamId}/staff", method = RequestMethod.GET)
        //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
        public ResponseEntity<Map<String, Object>> getStaffOfUnit(@PathVariable long teamId) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true,
                    teamService.getStaffForImportInTeam(teamId));
        }

        @ApiOperation(value = "Get Organization Id by team")
        @RequestMapping(value = "/team/organizationId", method = RequestMethod.GET)
        // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
        public ResponseEntity<Map<String, Object>> getOrganizationIdByTeamId(@PathVariable Long unitId) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, teamService.getOrganizationIdByTeamId(unitId));
        }

        @ApiOperation(value = "Get Organization Id by team")
        @RequestMapping(value = "/staff/{staffId}/team_activities", method = RequestMethod.GET)
        // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
        public ResponseEntity<Map<String, Object>> getTeamActivitiesOfStaff(@PathVariable Long staffId) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, teamService.getTeamActivitiesOfStaff(staffId));
        }

        @PutMapping("/team/general")
        @ApiOperation("update general details of team")
        //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
        public ResponseEntity<Map<String, Object>> updateTeamGeneralDetails(@PathVariable long unitId, @Validated @RequestBody TeamDTO teamDTO) throws ParseException {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, teamService.updateTeamGeneralDetails(unitId, teamDTO));
        }

        @GetMapping("/team/is_activity_assigned")
        @ApiOperation("verify is activity assign to any team")
        //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
        public ResponseEntity<Map<String, Object>> isActivityAssignedToTeam(@RequestParam BigInteger activityId) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true,
                    teamService.isActivityAssignedToTeam(activityId));
        }

    }
