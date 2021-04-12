package com.kairos.controller.staff_settings;

import com.kairos.dto.activity.unit_settings.activity_configuration.ActivityRankingDTO;
import com.kairos.dto.user.staff.staff_settings.StaffTeamRankingDTO;
import com.kairos.service.staff_settings.StaffTeamRankingService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_UNIT_URL;

@RestController
@RequestMapping(API_UNIT_URL)
@Api(API_UNIT_URL)
public class StaffTeamRankingController {
    @Inject
    private StaffTeamRankingService staffTeamRankingService;


    @ApiOperation(value = "update a activity_ranking settings")
    @PutMapping(value = "/staff_team_ranking")
    public ResponseEntity<Map<String, Object>> updateStaffTeamRanking( @RequestBody @Valid StaffTeamRankingDTO staffTeamRankingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffTeamRankingService.updateStaffTeamRanking(staffTeamRankingDTO));
    }

    @ApiOperation(value = "published a absence_ranking settings")
    @PutMapping(value =  "staff/{staffId}/staff_team_ranking/{id}/publish")
    public ResponseEntity<Map<String, Object>> publishStaffTeamRanking(@PathVariable Long staffId, @PathVariable BigInteger id, @RequestParam("publishedDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate publishedDate) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffTeamRankingService.publishStaffTeamRanking(id, staffId, publishedDate));
    }

    @ApiOperation("Get Staff Personalized team ranking")
    @GetMapping("staff/{staffId}/staff_team_ranking")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getStaffTeamRankings(@PathVariable Long staffId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffTeamRankingService.getStaffTeamRankings(staffId));
    }
}
