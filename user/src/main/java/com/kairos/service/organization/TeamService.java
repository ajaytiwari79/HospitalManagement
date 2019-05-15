package com.kairos.service.organization;

import com.kairos.commons.utils.DateUtils;
import com.kairos.config.env.EnvConfig;
import com.kairos.dto.activity.activity.ActivityCategoryListDTO;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.ActivityCategoryDTO;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationContactAddress;
import com.kairos.persistence.model.organization.StaffTeamRelationShipQueryResult;
import com.kairos.persistence.model.organization.StaffTeamRelationship;
import com.kairos.persistence.model.organization.team.Team;
import com.kairos.persistence.model.organization.team.TeamDTO;
import com.kairos.persistence.model.staff.StaffTeamDTO;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetailDTO;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.TeamGraphRepository;
import com.kairos.persistence.repository.user.client.ContactAddressGraphRepository;
import com.kairos.persistence.repository.user.region.RegionGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffTeamRelationshipGraphRepository;
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
    private OrganizationGraphRepository organizationGraphRepository;
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

    public TeamDTO createTeam(Long unitId, TeamDTO teamDTO) {

        OrganizationContactAddress organizationContactAddress = organizationGraphRepository.getOrganizationByOrganizationId(unitId);
        if (organizationContactAddress.getOrganization() == null) {
            exceptionService.dataNotFoundByIdException("message.teamservice.unit.id.notFound.by.group");
        }
        boolean teamExistInOrganizationByName = teamGraphRepository.teamExistInOrganizationByName(unitId, -1L, "(?i)" + teamDTO.getName());
        if (teamExistInOrganizationByName) {
            exceptionService.duplicateDataException("message.teamservice.team.alreadyexists.in.unit", teamDTO.getName());
        }

        Organization organization = organizationContactAddress.getOrganization();

        ContactAddress contactAddress;
        ZipCode zipCode;
        Municipality municipality;

        zipCode = organizationContactAddress.getZipCode();
        LOGGER.debug("zip code found is " + zipCode);
        if (zipCode == null) {
            exceptionService.dataNotFoundByIdException("message.zipCode.notFound");
        }
        municipality = organizationContactAddress.getMunicipality();
        if (municipality == null) {
            exceptionService.dataNotFoundByIdException("message.municipality.notFound");

        }
        Map<String, Object> geographyData = regionGraphRepository.getGeographicData(municipality.getId());
        if (geographyData == null) {
            LOGGER.info("Geography  not found with zipcodeId: " + zipCode.getId());
            exceptionService.dataNotFoundByIdException("message.geographyData.notFound", municipality.getId());
        }
        contactAddress = new ContactAddress();
        contactAddress.setMunicipality(municipality);
        contactAddress.setLongitude(organizationContactAddress.getContactAddress().getLongitude());
        contactAddress.setProvince(organizationContactAddress.getContactAddress().getProvince());
        contactAddress.setRegionName(organizationContactAddress.getContactAddress().getRegionName());
        contactAddress.setCity(organizationContactAddress.getContactAddress().getCity());
        contactAddress.setCountry(organizationContactAddress.getContactAddress().getCountry());
        contactAddress.setZipCode(zipCode);
        contactAddress.setLatitude(organizationContactAddress.getContactAddress().getLatitude());
        contactAddress.setHouseNumber(organizationContactAddress.getContactAddress().getHouseNumber());
        contactAddress.setStreet(organizationContactAddress.getContactAddress().getStreet());
        contactAddress.setStreetUrl(organizationContactAddress.getContactAddress().getStreetUrl());
        contactAddress.setStreet(organizationContactAddress.getContactAddress().getStreet());
        contactAddress.setFloorNumber(organizationContactAddress.getContactAddress().getFloorNumber());
        contactAddressGraphRepository.save(contactAddress, 2);
        Team team = new Team(teamDTO.getName(), teamDTO.getDescription(), contactAddress);
        teamGraphRepository.save(team);
        teamDTO.setId(team.getId());

        organization.getTeams().add(team);
        organizationGraphRepository.save(organization, 2);
        teamDTO.setId(team.getId());
        assignTeamLeadersToTeam(teamDTO, team);
        return teamDTO;
    }

    public TeamDTO updateTeam(Long unitId, Long teamId, TeamDTO teamDTO) {
        boolean teamExistInOrganizationAndGroupByName = teamGraphRepository.teamExistInOrganizationByName(unitId, teamId, "(?i)" + teamDTO.getName());
        if (teamExistInOrganizationAndGroupByName) {
            exceptionService.duplicateDataException("message.teamservice.team.alreadyexists.in.unit", teamDTO.getName());
        }
        Team team = teamGraphRepository.findOne(teamId, 0);
        if (team != null) {
            team.setName(teamDTO.getName());
            team.setDescription(teamDTO.getDescription());
            teamGraphRepository.save(team);
        } else {
            exceptionService.dataNotFoundByIdException("message.teamservice.team.notFound");
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
            exceptionService.dataNotFoundByIdException("message.teamservice.team.notFound");
        }
        return true;
    }

    public StaffTeamDTO updateStaffsInTeam(Long teamId, StaffTeamDTO staffTeamDTO) {
        if (staffTeamRelationshipGraphRepository.anyMainTeamExists(staffTeamDTO.getStaffId(), teamId)) {
            exceptionService.actionNotPermittedException("staff.main_team.exists");
        }
        Team team = teamGraphRepository.findByIdAndDeletedFalse(teamId);
        Staff staff = staffGraphRepository.findByStaffId(staffTeamDTO.getStaffId());
        StaffTeamRelationShipQueryResult staffTeamRelationShipQueryResult = staffTeamRelationshipGraphRepository.findByStaffIdAndTeamId(staffTeamDTO.getStaffId(), teamId);
        StaffTeamRelationship staffTeamRelationship = isNull(staffTeamRelationShipQueryResult) ? new StaffTeamRelationship(null, team, staff, staffTeamDTO.getLeaderType(), staffTeamDTO.getTeamType()) :
                new StaffTeamRelationship(staffTeamRelationShipQueryResult.getId(), team, staff, staffTeamRelationShipQueryResult.getLeaderType(), staffTeamDTO.getTeamType());
        staffTeamRelationshipGraphRepository.save(staffTeamRelationship);
        return staffTeamDTO;
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
            exceptionService.dataNotFoundByIdException("message.teamservice.team.notFound");
        }
        return true;
    }



    public boolean addStaffInTeam(long teamId, long staffId, boolean isAssigned, long unitId) {

        Staff staff = staffGraphRepository.findOne(staffId);
        Team team = teamGraphRepository.findOne(teamId, 0);
        if (staff == null || team == null) {
            exceptionService.internalServerError("error.teamservice.stafforteam.notEmpty");
        }
        int countOfRel = teamGraphRepository.countRelBetweenStaffAndTeam(teamId, staffId);
        if (countOfRel == 0) {

            int countOfRelCreated = teamGraphRepository.linkOfTeamAndStaff(teamId, staffId, DateUtils.getCurrentDate().getTime(), DateUtils.getCurrentDate().getTime());
            if (countOfRelCreated > 0) {
                return true;
            } else {
                exceptionService.dataNotFoundByIdException("message.teamservice.somethingwrong");
            }
        } else {
            int countOfRelCreated = teamGraphRepository.updateStaffTeamRelationship(teamId, staffId, DateUtils.getCurrentDate().getTime(), isAssigned);
            if (countOfRelCreated > 0) {
                if (!isAssigned) {
                    staffGraphRepository.save(staff);
                }
                return true;
            } else {
                exceptionService.dataNotFoundByIdException("message.teamservice.somethingwrong");

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
            exceptionService.dataNotFoundByIdException("message.teamservice.team.notFound", teamId);
        }
        List<Map<String, Object>> queryResult = teamGraphRepository.getAllStaffByOrganization(teamId, envConfig.getServerHost() + FORWARD_SLASH);
        List<Map<String, Object>> staff = new ArrayList<>();
        for (Map<String, Object> staffInfo : queryResult) {
            staff.add((Map<String, Object>) staffInfo.get("data"));
        }
        return staff;
    }


    public List<Map<String, Object>> getTeamSelectedServices(Long teamId) {
        return organizationGraphRepository.getTeamAllSelectedSubServices(teamId);
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

    public List<TeamDTO> getAllTeamsOfOrganization(Long unitId){
        return teamGraphRepository.findAllTeamsInOrganization(unitId);
    }

    public Long getOrganizationIdByTeamId(Long teamId) {
        Organization organization = organizationGraphRepository.getOrganizationByTeamId(teamId);
        return organization.getId();
    }

    public Organization getOrganizationByTeamId(Long teamId) {
        return organizationGraphRepository.getOrganizationByTeamId(teamId);
    }

    public TeamDTO updateTeamGeneralDetails(long teamId, TeamDTO teamDTO) {

        Team team = teamGraphRepository.findOne(teamId);
        if (team == null) {
            exceptionService.dataNotFoundByIdException("message.teamservice.team.notFound", teamId);
        }
        team.setName(teamDTO.getName());
        teamGraphRepository.save(team);
        return teamDTO;
    }

    public List<Object> getTeamsInUnit(Long unitId) {
        List<Map<String, Object>> data = organizationGraphRepository.getUnitTeams(unitId);
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
        staffList.forEach(staff -> {
            staffTeamRelationships.add(new StaffTeamRelationship(team, staff, teamDTO.getMainTeamLeaderIds().contains(staff.getId()) ? StaffTeamRelationship.LeaderType.MAIN_LEAD : StaffTeamRelationship.LeaderType.ACTING_LEAD));
        });
        if (isCollectionNotEmpty(staffTeamRelationships)) {
            staffTeamRelationshipGraphRepository.saveAll(staffTeamRelationships);
        }
    }

    public void assignStaffInTeams(Staff staff, List<StaffTeamDTO> staffTeamDetails) {
        teamGraphRepository.removeStaffFromAllTeams(staff.getId());
        List<Team> teams=teamGraphRepository.findAllById(new ArrayList<>(staffTeamDetails.stream().map(k->k.getTeamId()).collect(Collectors.toSet())));
        Map<Long,Team> teamMap=teams.stream().collect(Collectors.toMap(k->k.getId(),Function.identity()));
        List<StaffTeamRelationship> staffTeamRelationshipList = staffTeamDetails.stream().map(staffTeamDetail ->new StaffTeamRelationship(null,teamMap.get(staffTeamDetail.getTeamId()),staff,staffTeamDetail.getLeaderType(),staffTeamDetail.getTeamType())).collect(Collectors.toList());
        if(isCollectionNotEmpty(staffTeamRelationshipList)){
            staffTeamRelationshipGraphRepository.saveAll(staffTeamRelationshipList);
        }
    }

    public boolean removeStaffFromTeam(Long teamId, Long staffId) {
        return teamGraphRepository.removeStaffFromTeam(staffId, teamId);
    }
}
