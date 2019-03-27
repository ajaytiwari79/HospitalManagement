package com.kairos.service.organization;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.config.env.EnvConfig;
import com.kairos.dto.activity.activity.ActivityCategoryListDTO;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.ActivityCategoryDTO;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationContactAddress;
import com.kairos.persistence.model.organization.team.Team;
import com.kairos.persistence.model.organization.team.TeamDTO;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetailDTO;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.TeamGraphRepository;
import com.kairos.persistence.repository.user.client.ContactAddressGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.region.MunicipalityGraphRepository;
import com.kairos.persistence.repository.user.region.RegionGraphRepository;
import com.kairos.persistence.repository.user.region.ZipCodeGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.ActivityIntegrationService;
import com.kairos.service.skill.SkillService;
import com.kairos.service.staff.StaffService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

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
    private ZipCodeGraphRepository zipCodeGraphRepository;
    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private MunicipalityGraphRepository municipalityGraphRepository;
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

    public TeamDTO createTeam(Long unitId, TeamDTO teamDTO) {

        OrganizationContactAddress organizationContactAddress = organizationGraphRepository.getOrganizationByOrganizationId(unitId);
        if (organizationContactAddress.getOrganization() == null) {
            exceptionService.dataNotFoundByIdException("message.teamservice.unit.id.notFound.by.group");
        }
        boolean teamExistInOrganizationAndGroupByName = teamGraphRepository.teamExistInOrganizationByName(unitId, -1L, "(?i)" + teamDTO.getName());
        if (teamExistInOrganizationAndGroupByName) {
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

        if(ObjectUtils.isNotNull(teamDTO.getTeamLeaderStaffId())){
            teamGraphRepository.assignTeamLeaderToTeam(team.getId(), teamDTO.getTeamLeaderStaffId());
        }
        return teamDTO;
    }

    public TeamDTO updateTeam(Long unitId, Long teamId, TeamDTO teamDTO) {
        boolean teamExistInOrganizationAndGroupByName = teamGraphRepository.teamExistInOrganizationByName(unitId, teamId, "(?i)" + teamDTO.getName());
        if (teamExistInOrganizationAndGroupByName) {
            exceptionService.duplicateDataException("message.teamservice.team.alreadyexists.in.unit", teamDTO.getName());
        }
        Team team = teamGraphRepository.findOne(teamId);
        if (team != null) {
            team.setName(teamDTO.getName());
            team.setDescription(teamDTO.getDescription());
            teamGraphRepository.save(team);
        } else {
            exceptionService.dataNotFoundByIdException("message.teamservice.team.notFound");
        }
        if(ObjectUtils.isNotNull(teamDTO.getTeamLeaderStaffId())) {
            teamGraphRepository.updateTeamLeaderOfTeam(team.getId(), teamDTO.getTeamLeaderStaffId());
        }
        return teamDTO;
    }

    public boolean updateActivitiesOfTeam(Long teamId, List<BigInteger> activityIds) {
        Team team = teamGraphRepository.findOne(teamId);
        if (team != null) {
            team.setActivityIds(activityIds);
            teamGraphRepository.save(team);
        } else {
            exceptionService.dataNotFoundByIdException("message.teamservice.team.notFound");
        }
        return true;
    }

    public boolean updateStaffsInTeam(Long teamId, Set<Long> staffIds) {
        if(ObjectUtils.isCollectionEmpty(staffIds)){
            teamGraphRepository.removeAllStaffsFromTeam(teamId);
        }else{
            teamGraphRepository.updateStaffsInTeam(teamId,staffIds);
        }
        return true;
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
                Collectors.groupingBy(activityType -> new ActivityCategoryDTO(activityType.getCategoryId(), activityType.getCategoryName()),Collectors.toList()));
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

    public List<Staff> getAllUsers(Long teamID) {
        return teamGraphRepository.getStaffInTeam(teamID);
    }

    public String getUserStaffType(Long userId) {
        return teamGraphRepository.getStaffType(userId);
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

    public boolean addTeamSelectedSkills(Long teamId, List<Long> skillIds) {
        if(ObjectUtils.isCollectionNotEmpty(skillIds)){
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

    public Long getOrganizationIdByTeamId(Long teamId) {
        Organization organization = organizationGraphRepository.getOrganizationByTeamId(teamId);
        return organization.getId();
    }

    public Organization getOrganizationByTeamId(Long teamId) {
        Organization organization = organizationGraphRepository.getOrganizationByTeamId(teamId);
        return organization;
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
}
