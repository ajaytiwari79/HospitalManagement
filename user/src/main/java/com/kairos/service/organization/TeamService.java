package com.kairos.service.organization;

import com.kairos.commons.utils.DateUtils;
import com.kairos.config.env.EnvConfig;
import com.kairos.dto.activity.activity.ActivityCategoryListDTO;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.ActivityCategoryDTO;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.organization.OrganizationContactAddress;
import com.kairos.persistence.model.organization.StaffTeamRelationShipQueryResult;
import com.kairos.persistence.model.organization.StaffTeamRelationship;
import com.kairos.persistence.model.organization.Unit;
import com.kairos.persistence.model.organization.team.Team;
import com.kairos.persistence.model.organization.team.TeamDTO;
import com.kairos.persistence.model.staff.StaffTeamDTO;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetailDTO;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.persistence.repository.organization.TeamGraphRepository;
import com.kairos.persistence.repository.organization.UnitGraphRepository;
import com.kairos.persistence.repository.user.client.ContactAddressGraphRepository;
import com.kairos.persistence.repository.user.region.RegionGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffTeamRelationshipGraphRepository;
import com.kairos.service.access_permisson.AccessGroupService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.ActivityIntegrationService;
import com.kairos.service.skill.SkillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ArrayUtil.getUnionOfList;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.constants.AppConstants.FORWARD_SLASH;
import static com.kairos.constants.UserMessagesConstants.*;
/**
 * Created by oodles on 7/10/16.
 */
@Transactional
@Service
public class TeamService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TeamService.class);
    @Inject
    private TeamGraphRepository teamGraphRepository;
    @Inject
    private UnitGraphRepository unitGraphRepository;
    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private RegionGraphRepository regionGraphRepository;
    @Inject
    private EnvConfig envConfig;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private ContactAddressGraphRepository contactAddressGraphRepository;
    @Inject
    private OrganizationService organizationService;
    @Inject
    private ActivityIntegrationService activityIntegrationService;
    @Inject
    private SkillService skillService;
    @Inject
    private StaffTeamRelationshipGraphRepository staffTeamRelationshipGraphRepository;
    @Inject
    private AccessGroupService accessGroupService;

    public TeamDTO createTeam(Long unitId, TeamDTO teamDTO) {

        OrganizationContactAddress organizationContactAddress = unitGraphRepository.getOrganizationByOrganizationId(unitId);
        if (organizationContactAddress.getUnit() == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_TEAMSERVICE_UNIT_ID_NOTFOUND_BY_GROUP);
        }
        boolean teamExistInOrganizationByName = teamGraphRepository.teamExistInOrganizationByName(unitId, -1L, "(?i)" + teamDTO.getName());
        if (teamExistInOrganizationByName) {
            exceptionService.duplicateDataException(MESSAGE_TEAMSERVICE_TEAM_ALREADYEXISTS_IN_UNIT, teamDTO.getName());
        }

        Unit unit = organizationContactAddress.getUnit();

        ContactAddress contactAddress;
        ZipCode zipCode;
        Municipality municipality;

        zipCode = organizationContactAddress.getZipCode();
        LOGGER.debug("zip code found is " + zipCode);
        if (zipCode == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ZIPCODE_NOTFOUND);
        }
        municipality = organizationContactAddress.getMunicipality();
        if (municipality == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_MUNICIPALITY_NOTFOUND);

        }
        Map<String, Object> geographyData = regionGraphRepository.getGeographicData(municipality.getId());
        if (geographyData == null) {
            LOGGER.info("Geography  not found with zipcodeId: " + zipCode.getId());
            exceptionService.dataNotFoundByIdException(MESSAGE_GEOGRAPHYDATA_NOTFOUND, municipality.getId());
        }
        contactAddress = new ContactAddress(municipality, organizationContactAddress.getContactAddress().getLongitude(), organizationContactAddress.getContactAddress().getLatitude(),
                organizationContactAddress.getContactAddress().getProvince(), organizationContactAddress.getContactAddress().getRegionName(), organizationContactAddress.getContactAddress().getCity(),
                organizationContactAddress.getContactAddress().getCountry(), zipCode, organizationContactAddress.getContactAddress().getHouseNumber(),
                organizationContactAddress.getContactAddress().getStreet(), organizationContactAddress.getContactAddress().getStreetUrl(), organizationContactAddress.getContactAddress().getFloorNumber()
        );
        contactAddressGraphRepository.save(contactAddress, 2);
        Team team = new Team(teamDTO.getName(), teamDTO.getDescription(), contactAddress);
        teamGraphRepository.save(team);
        teamDTO.setId(team.getId());

        unit.getTeams().add(team);
        unitGraphRepository.save(unit, 2);
        teamDTO.setId(team.getId());
        assignTeamLeadersToTeam(teamDTO, team);
        return teamDTO;
    }

    public TeamDTO updateTeam(Long unitId, Long teamId, TeamDTO teamDTO) {
        boolean teamExistInOrganizationAndGroupByName = teamGraphRepository.teamExistInOrganizationByName(unitId, teamId, "(?i)" + teamDTO.getName());
        if (teamExistInOrganizationAndGroupByName) {
            exceptionService.duplicateDataException(MESSAGE_TEAMSERVICE_TEAM_ALREADYEXISTS_IN_UNIT, teamDTO.getName());
        }
        Team team = teamGraphRepository.findOne(teamId, 0);
        if (team != null) {
            team.setName(teamDTO.getName());
            team.setDescription(teamDTO.getDescription());
            teamGraphRepository.save(team);
        } else {
            exceptionService.dataNotFoundByIdException(MESSAGE_TEAMSERVICE_TEAM_NOTFOUND);
        }
        assignTeamLeadersToTeam(teamDTO, team);
        return teamDTO;
    }

    public boolean updateActivitiesOfTeam(Long teamId, Set<BigInteger> activityIds) {
        Team team = teamGraphRepository.findOne(teamId);
        if (team != null) {
            team.setActivityIds(activityIds);
            teamGraphRepository.save(team);
        } else {
            exceptionService.dataNotFoundByIdException(MESSAGE_TEAMSERVICE_TEAM_NOTFOUND);
        }
        return true;
    }

    public List<StaffTeamDTO> updateStaffsInTeam(Long unitId, Long teamId, List<StaffTeamDTO> staffTeamDTOs) {
        for (StaffTeamDTO staffTeamDTO : staffTeamDTOs) {
            if (StaffTeamRelationship.TeamType.MAIN.equals(staffTeamDTO.getTeamType()) && staffTeamRelationshipGraphRepository.anyMainTeamExists(staffTeamDTO.getStaffId(), teamId)) {
                exceptionService.actionNotPermittedException("staff.main_team.exists");
            }
            if (staffTeamDTO.getLeaderType() != null && !accessGroupService.findStaffAccessRole(unitId, staffTeamDTO.getStaffId()).getManagement()) {
                exceptionService.actionNotPermittedException(STAFF_CAN_NOT_BE_TEAM_LEADER);
            }
            Team team = teamGraphRepository.findByIdAndDeletedFalse(teamId);
            Staff staff = staffGraphRepository.findByStaffId(staffTeamDTO.getStaffId());
            StaffTeamRelationShipQueryResult staffTeamRelationShipQueryResult = staffTeamRelationshipGraphRepository.findByStaffIdAndTeamId(staffTeamDTO.getStaffId(), teamId);
            StaffTeamRelationship staffTeamRelationship = isNull(staffTeamRelationShipQueryResult) ? new StaffTeamRelationship(null, team, staff, staffTeamDTO.getLeaderType(), staffTeamDTO.getTeamType()) :
                    new StaffTeamRelationship(staffTeamRelationShipQueryResult.getId(), team, staff, staffTeamRelationShipQueryResult.getLeaderType(), staffTeamDTO.getTeamType());
            staffTeamRelationshipGraphRepository.save(staffTeamRelationship);
        }
        return staffTeamDTOs;
    }

    public TeamDTO getTeamDetails(Long teamId) {
        return teamGraphRepository.getTeamDetailsById(teamId);
    }

    public Map<String, Object> getTeamsAndPrerequisite(long unitId) {
        List<Map<String, Object>> teams = teamGraphRepository.getTeams(unitId);
        Map<String, Object> map = new HashMap<>();
        map.put("teams", (teams.size() != 0) ? teams.get(0).get("teams") : Collections.emptyList());
        List<StaffPersonalDetailDTO> staffPersonalDetailDTOS = staffGraphRepository.getAllStaffPersonalDetailsByUnit(unitId, envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
        map.put("staffList", staffPersonalDetailDTOS);
        map.put("skillList", skillService.getSkillsOfOrganization(unitId));

        List<ActivityDTO> activityDTOList = activityIntegrationService.getActivitiesWithCategories(unitId);
        Map<ActivityCategoryDTO, List<ActivityDTO>> activityTypeCategoryListMap = activityDTOList.stream().collect(
                Collectors.groupingBy(activityType -> new ActivityCategoryDTO(activityType.getCategoryId(), activityType.getCategoryName()), Collectors.toList()));
        List<ActivityCategoryListDTO> activityCategoryListDTOS = activityTypeCategoryListMap.entrySet().stream().map(activity -> new ActivityCategoryListDTO(activity.getKey(),
                activity.getValue())).collect(Collectors.toList());
        map.put("activityList", activityCategoryListDTOS);
        return map;
    }

    public boolean deleteTeamByTeamId(long teamId) {
        Team team = teamGraphRepository.findOne(teamId);
        if (team != null) {
            team.setEnabled(false);
            teamGraphRepository.save(team);
        } else {
            exceptionService.dataNotFoundByIdException(MESSAGE_TEAMSERVICE_TEAM_NOTFOUND);
        }
        return true;
    }


    public boolean addStaffInTeam(long teamId, long staffId, boolean isAssigned, long unitId) {

        Staff staff = staffGraphRepository.findOne(staffId);
        Team team = teamGraphRepository.findOne(teamId, 0);
        if (staff == null || team == null) {
            exceptionService.internalServerError(ERROR_TEAMSERVICE_STAFFORTEAM_NOTEMPTY);
        }
        int countOfRel = teamGraphRepository.countRelBetweenStaffAndTeam(teamId, staffId);
        if (countOfRel == 0) {

            int countOfRelCreated = teamGraphRepository.linkOfTeamAndStaff(teamId, staffId, DateUtils.getCurrentDate().getTime(), DateUtils.getCurrentDate().getTime());
            if (countOfRelCreated > 0) {
                return true;
            } else {
                exceptionService.dataNotFoundByIdException(MESSAGE_TEAMSERVICE_SOMETHINGWRONG);
            }
        } else {
            int countOfRelCreated = teamGraphRepository.updateStaffTeamRelationship(teamId, staffId, DateUtils.getCurrentDate().getTime(), isAssigned);
            if (countOfRelCreated > 0) {
                if (!isAssigned) {
                    staffGraphRepository.save(staff);
                }
                return true;
            } else {
                exceptionService.dataNotFoundByIdException(MESSAGE_TEAMSERVICE_SOMETHINGWRONG);

            }
        }
        return false;
    }

    /**
     * this method will return all staff in unit
     * and parameter {isSelected} in query response has value true or false
     * if true then staff is already in team otherwise it can be imported in team
     *
     * @return
     */
    public List<Map<String, Object>> getStaffForImportInTeam(long teamId) {
        Team team = teamGraphRepository.findOne(teamId, 0);
        if (team == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_TEAMSERVICE_TEAM_NOTFOUND, teamId);
        }
        List<Map<String, Object>> queryResult = teamGraphRepository.getAllStaffByOrganization(teamId, envConfig.getServerHost() + FORWARD_SLASH);
        List<Map<String, Object>> staff = new ArrayList<>();
        for (Map<String, Object> staffInfo : queryResult) {
            staff.add((Map<String, Object>) staffInfo.get("data"));
        }
        return staff;
    }


    public List<Map<String, Object>> getTeamSelectedServices(Long teamId) {
        return unitGraphRepository.getTeamAllSelectedSubServices(teamId);
    }

    public List<com.kairos.persistence.model.organization.services.OrganizationService> addTeamSelectedServices(Long teamId, Long[] organizationService) {
        return teamGraphRepository.addSelectedSevices(teamId, organizationService);

    }

    public boolean addTeamSelectedSkills(Long teamId, Set<Long> skillIds) {
        if (isCollectionNotEmpty(skillIds)) {
            teamGraphRepository.saveSkill(teamId, skillIds);
        } else {
            teamGraphRepository.removeAllSkillsFromTeam(teamId);
        }
        return true;
    }

    public List<Map<String, Object>> getTeamSelectedSkills(Long teamId) {
        return teamGraphRepository.getSelectedSkills(teamId);

    }

    public List<Map<String, Object>> getAllTeamsInOrganization(Long unitId) {

        List<Map<String, Object>> queryResult = teamGraphRepository.getAllTeamsInOrganization(unitId);

        List<Map<String, Object>> teams = new ArrayList<>();
        for (Map<String, Object> staffInfo : queryResult) {
            teams.add((Map<String, Object>) staffInfo.get("data"));
        }
        return teams;

    }

    public List<TeamDTO> getAllTeamsOfOrganization(Long unitId) {
        return teamGraphRepository.findAllTeamsInOrganization(unitId);
    }

    public Long getOrganizationIdByTeamId(Long teamId) {
        Unit unit = unitGraphRepository.getOrganizationByTeamId(teamId);
        return unit.getId();
    }

    public Unit getOrganizationByTeamId(Long teamId) {
        return unitGraphRepository.getOrganizationByTeamId(teamId);
    }

    public TeamDTO updateTeamGeneralDetails(long teamId, TeamDTO teamDTO) {

        Team team = teamGraphRepository.findOne(teamId);
        if (team == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_TEAMSERVICE_TEAM_NOTFOUND, teamId);
        }
        team.setName(teamDTO.getName());
        teamGraphRepository.save(team);
        return teamDTO;
    }

    public List<Object> getTeamsInUnit(Long unitId) {
        List<Map<String, Object>> data = unitGraphRepository.getUnitTeams(unitId);
        List<Object> response = new ArrayList<>();
        for (Map<String, Object> map :
                data) {
            Object o = map.get("result");
            response.add(o);
        }
        return response;
    }

    public List<BigInteger> getTeamActivitiesOfStaff(Long staffId) {
        return teamGraphRepository.getTeamActivitiesOfStaff(staffId);
    }

    public boolean isActivityAssignedToTeam(BigInteger activityId) {
        return teamGraphRepository.activityExistInTeamByActivityId(activityId);

    }

    private void assignTeamLeadersToTeam(TeamDTO teamDTO, Team team) {
        Set<Long> staffIds = getUnionOfList(new ArrayList<>(teamDTO.getMainTeamLeaderIds()), new ArrayList<>(teamDTO.getActingTeamLeaderIds()));
        List<Staff> staffList = staffGraphRepository.findAllById(new ArrayList<>(staffIds));
        teamGraphRepository.removeAllStaffsFromTeam(teamDTO.getId());

        List<StaffTeamRelationship> staffTeamRelationships = new ArrayList<>();
        List<StaffTeamRelationship>  staffTeamRelationShipQueryResults = staffTeamRelationshipGraphRepository.findByStaffIdsAndTeamId(staffIds,teamDTO.getId());
        Map<Long,StaffTeamRelationship> staffTeamRelationShipQueryResultMap = staffTeamRelationShipQueryResults.stream().collect(Collectors.toMap(k->k.getStaff().getId(),v->v));
        staffList.forEach(staff -> {
            if(staffTeamRelationShipQueryResultMap.containsKey(staff.getId())){
                StaffTeamRelationship staffTeamRelationship = staffTeamRelationShipQueryResultMap.get(staff.getId());
                staffTeamRelationship.setLeaderType(teamDTO.getMainTeamLeaderIds().contains(staff.getId()) ? StaffTeamRelationship.LeaderType.MAIN_LEAD : StaffTeamRelationship.LeaderType.ACTING_LEAD);
                staffTeamRelationships.add(staffTeamRelationship);
            }else {
                staffTeamRelationships.add(new StaffTeamRelationship(team, staff, teamDTO.getMainTeamLeaderIds().contains(staff.getId()) ? StaffTeamRelationship.LeaderType.MAIN_LEAD : StaffTeamRelationship.LeaderType.ACTING_LEAD));
            }
        });
        if (isCollectionNotEmpty(staffTeamRelationships)) {
            staffTeamRelationshipGraphRepository.saveAll(staffTeamRelationships);
        }
    }

    public void assignStaffInTeams(Staff staff, List<StaffTeamDTO> staffTeamDetails, Long unitId) {
        if (staffTeamDetails.stream().anyMatch(k -> k.getLeaderType() != null) && !accessGroupService.findStaffAccessRole(unitId, staff.getId()).getManagement()) {
            exceptionService.actionNotPermittedException(STAFF_CAN_NOT_BE_TEAM_LEADER);
        }
        teamGraphRepository.removeStaffFromAllTeams(staff.getId());
        List<Team> teams = teamGraphRepository.findAllById(new ArrayList<>(staffTeamDetails.stream().map(k -> k.getTeamId()).collect(Collectors.toSet())));
        Map<Long, Team> teamMap = teams.stream().collect(Collectors.toMap(k -> k.getId(), Function.identity()));
        List<StaffTeamRelationship> staffTeamRelationshipList = staffTeamDetails.stream().map(staffTeamDetail -> new StaffTeamRelationship(null, teamMap.get(staffTeamDetail.getTeamId()), staff, staffTeamDetail.getLeaderType(), staffTeamDetail.getTeamType())).collect(Collectors.toList());
        if (isCollectionNotEmpty(staffTeamRelationshipList)) {
            staffTeamRelationshipGraphRepository.saveAll(staffTeamRelationshipList);
        }
    }

    public boolean removeStaffsFromTeam(Long teamId, List<Long> staffIds) {
        return teamGraphRepository.removeStaffsFromTeam(staffIds, teamId);
    }
}
