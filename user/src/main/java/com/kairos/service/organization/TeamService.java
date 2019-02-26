package com.kairos.service.organization;

import com.kairos.commons.utils.DateUtils;
import com.kairos.config.env.EnvConfig;
import com.kairos.dto.user.organization.AddressDTO;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationContactAddress;
import com.kairos.persistence.model.organization.group.Group;
import com.kairos.persistence.model.organization.team.Team;
import com.kairos.persistence.model.organization.team.TeamDTO;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.repository.organization.GroupGraphRepository;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.TeamGraphRepository;
import com.kairos.persistence.repository.user.client.ContactAddressGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.region.MunicipalityGraphRepository;
import com.kairos.persistence.repository.user.region.RegionGraphRepository;
import com.kairos.persistence.repository.user.region.ZipCodeGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.service.client.AddressVerificationService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.FormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

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
    private GroupGraphRepository groupGraphRepository;
    @Inject
    private ZipCodeGraphRepository zipCodeGraphRepository;
    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private AddressVerificationService addressVerificationService;
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


    public Map<String, Object> createTeam(Long groupId, Long unitId, TeamDTO teamDTO, String type) {
        // Long orgId = organizationService.getOrganizationIdByTeamIdOrGroupIdOrOrganizationId(type, unitId);
        Group group = groupGraphRepository.findOne(groupId);
        if (group == null) {
            exceptionService.dataNotFoundByIdException("message.teamservice.group.id.notFound");
        }

        OrganizationContactAddress organizationContactAddress = organizationGraphRepository.getOrganizationByGroupId(groupId);
        if (organizationContactAddress.getOrganization() == null) {
            exceptionService.dataNotFoundByIdException("message.teamservice.unit.id.notFound.by.group");
        }

        boolean teamExistInOrganizationAndGroupByName = teamGraphRepository.teamExistInOrganizationAndGroupByName(unitId, group.getId(), teamDTO.getId() != null ? teamDTO.getId() : -1L, "(?i)" + teamDTO.getName());
        if (teamExistInOrganizationAndGroupByName) {
            exceptionService.duplicateDataException("message.teamservice.team.alreadyexists.in.group", teamDTO.getName(), group.getName());
        }

        ContactAddress contactAddress = null;
        ZipCode zipCode = null;
        Municipality municipality = null;
        if (!teamDTO.isHasAddressOfUnit()) {
            LOGGER.info("Setting Contact Address of Team different from Unit");
            AddressDTO addressDTO = teamDTO.getContactAddress();
            contactAddress = new ContactAddress();
            // -------Parse Address from DTO -------- //
            //ZipCode
            if (addressDTO.getZipCodeId() == null) {
                LOGGER.info("No ZipCode value received");
                return null;
            }
            zipCode = zipCodeGraphRepository.findOne(addressDTO.getZipCodeId(), 0);
            if (zipCode == null) {
                exceptionService.dataNotFoundByIdException("message.zipcode.notFound");
            }
            if (addressDTO.getMunicipalityId() != null) {
                municipality = municipalityGraphRepository.findOne(addressDTO.getMunicipalityId(), 0);
                if (municipality == null) {
                    exceptionService.dataNotFoundByIdException("message.municipality.notFound");
                }
                contactAddress.setMunicipality(municipality);
                Map<String, Object> geographyData = regionGraphRepository.getGeographicData(municipality.getId());
                if (geographyData == null) {
                    exceptionService.dataNotFoundByIdException("message.geographyData.notFound", municipality.getId());
                }
                contactAddress.setProvince(String.valueOf(geographyData.get("provinceName")));
                contactAddress.setRegionName(String.valueOf(geographyData.get("regionName")));

            }
            contactAddress.setCity(addressDTO.getCity());
            contactAddress.setFloorNumber(addressDTO.getFloorNumber());
            contactAddress.setHouseNumber(addressDTO.getHouseNumber());
            contactAddress.setStreet(addressDTO.getStreet());
            contactAddress.setZipCode(zipCode);
        } else {
            LOGGER.info("Setting Contact Address of Team same as Unit");
            if (organizationContactAddress.getOrganization() != null) {
                contactAddress = organizationContactAddress.getContactAddress();

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
                contactAddress.setCity(organizationContactAddress.getContactAddress().getCity());
                contactAddress.setCountry(organizationContactAddress.getContactAddress().getCountry());
                contactAddress.setZipCode(zipCode);
                contactAddress.setLatitude(organizationContactAddress.getContactAddress().getLatitude());
                contactAddress.setHouseNumber(organizationContactAddress.getContactAddress().getHouseNumber());
                contactAddress.setStreet(organizationContactAddress.getContactAddress().getStreet());
                contactAddress.setStreetUrl(organizationContactAddress.getContactAddress().getStreetUrl());
                contactAddress.setStreet(organizationContactAddress.getContactAddress().getStreet());
                contactAddress.setFloorNumber(organizationContactAddress.getContactAddress().getFloorNumber());
            }
        }
        contactAddressGraphRepository.save(contactAddress,2);
        Team team;
        if (teamDTO.getId() == null) {
            team = new Team(teamDTO.getName(), teamDTO.isHasAddressOfUnit(), contactAddress);
        } else {
            team = teamGraphRepository.findOne(teamDTO.getId());
            team.setName(teamDTO.getName());
            team.setHasAddressOfUnit(teamDTO.isHasAddressOfUnit());
            team.setContactAddress(contactAddress);
        }
        group.getTeamList().add(team);
        groupGraphRepository.save(group, 2);
        Map<String, Object> response = new HashMap<>();
        response.put("id", team.getId());
        response.put("name", team.getName());
        response.put("contactAddress", new AddressDTO(contactAddress.getId(), contactAddress.getStreet(), contactAddress.getHouseNumber(),contactAddress.getCity(), municipality.getName(), zipCode.getId(), contactAddress.getRegionName(), municipality.getId()));
        return response;
    }

    public Map<String, Object> getTeams(long groupId) {
        OrganizationContactAddress organizationContactAddress = organizationGraphRepository.getOrganizationByGroupId(groupId);

        Long countryId = countryGraphRepository.getCountryIdByUnitId(organizationContactAddress.getOrganization().getId());

        List<Map<String, Object>> teams = teamGraphRepository.getTeams(groupId);
        Map<String, Object> map = new HashMap<>();
        map.put("teams", (teams.size() != 0) ? teams.get(0).get("teams") : Collections.emptyList());
        if (countryId != null) {
            map.put("zipCodes", FormatUtil.formatNeoResponse(zipCodeGraphRepository.getAllZipCodeByCountryId(countryId)));
        }
        return map;
    }

    public boolean deleteTeamByTeamId(long  teamId) {
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

       /*
        By Yasir
        Commented below method as we are no longer using FLS Visitour
       */

    private boolean updateTeamIdOfStaffInVisitour(String teamId, long staffId, long unitId) {

        /*LOGGER.info("Updating staff in visitour");
        Map<String, String> flsCredentials = integrationService.getFLS_Credentials(unitId);
        Map <String,Object> engineerMetaData = new HashMap<>();
        engineerMetaData.put("fmvtid",staffId);
        engineerMetaData.put("fmextID",staffId);
        engineerMetaData.put("teamID",teamId);
        int code = scheduler.createEngineer(engineerMetaData, flsCredentials);
        LOGGER.info("FLS staff sync status-->" + code);
        if(code == 0){
            return true;
        }*/
        return false;
    }

    public List<Map<String, Object>> getTeamSelectedServices(Long teamId) {
        return organizationGraphRepository.getTeamAllSelectedSubServices(teamId);
    }

    public List<Map<String, Object>> getTeamAvailableServices(Long teamId) {
        return organizationGraphRepository.getTeamGroupServices(teamId);
    }

    public List<com.kairos.persistence.model.organization.services.OrganizationService> addTeamSelectedServices(Long teamId, Long[] organizationService) {
        return teamGraphRepository.addSelectedSevices(teamId, organizationService);

    }

    public List<Skill> addTeamSelectedSkills(Long teamId, Long[] skill) {
        return teamGraphRepository.saveSkill(teamId, skill);
    }

    public List<Map<String, Object>> getTeamSelectedSkills(Long teamId) {
        return teamGraphRepository.getSelectedSkills(teamId);

    }

    public List<Map<String, Object>> getTeamAvailableSkills(Long teamId) {
        return organizationGraphRepository.getTeamGroupSkill(teamId);
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
}
