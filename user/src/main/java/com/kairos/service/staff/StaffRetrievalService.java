package com.kairos.service.staff;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.config.env.EnvConfig;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.open_shift.priority_group.StaffIncludeFilterDTO;
import com.kairos.dto.activity.shift.FunctionDTO;
import com.kairos.dto.activity.shift.StaffEmploymentDetails;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.skill.SkillDTO;
import com.kairos.dto.user.expertise.SeniorAndChildCareDaysDTO;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.dto.user.staff.StaffWithSkillDTO;
import com.kairos.dto.user.staff.employment.StaffEmploymentUnitDataWrapper;
import com.kairos.dto.user.staff.staff.StaffDTO;
import com.kairos.dto.user.staff.staff.StaffResultDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.TimeSlotType;
import com.kairos.enums.reason_code.ReasonCodeType;
import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.access_permission.StaffAccessGroupQueryResult;
import com.kairos.persistence.model.access_permission.query_result.AccessGroupDayTypesQueryResult;
import com.kairos.persistence.model.access_permission.query_result.AccessGroupStaffQueryResult;
import com.kairos.persistence.model.access_permission.query_result.DayTypeCountryHolidayCalenderQueryResult;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.country.default_data.DayType;
import com.kairos.persistence.model.country.default_data.EngineerTypeDTO;
import com.kairos.persistence.model.country.reason_code.ReasonCode;
import com.kairos.persistence.model.country.reason_code.ReasonCodeResponseDTO;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.services.OrganizationServicesAndLevelQueryResult;
import com.kairos.persistence.model.organization.time_slot.TimeSlotWrapper;
import com.kairos.persistence.model.query_wrapper.CountryHolidayCalendarQueryResult;
import com.kairos.persistence.model.staff.*;
import com.kairos.persistence.model.staff.permission.UnitStaffQueryResult;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.staff.personal_details.StaffAdditionalInfoQueryResult;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetail;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetailDTO;
import com.kairos.persistence.model.staff.position.EmploymentAndPositionDTO;
import com.kairos.persistence.model.staff.position.StaffPositionDTO;
import com.kairos.persistence.model.user.employment.query_result.EmploymentQueryResult;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.Response.ExpertiseQueryResult;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import com.kairos.persistence.model.user.language.Language;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.OrganizationServiceRepository;
import com.kairos.persistence.repository.organization.TeamGraphRepository;
import com.kairos.persistence.repository.organization.time_slot.TimeSlotGraphRepository;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.DayTypeGraphRepository;
import com.kairos.persistence.repository.user.country.EngineerTypeGraphRepository;
import com.kairos.persistence.repository.user.country.ReasonCodeGraphRepository;
import com.kairos.persistence.repository.user.employment.EmploymentFunctionRelationshipRepository;
import com.kairos.persistence.repository.user.employment.EmploymentGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.language.LanguageGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffExpertiseRelationShipGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.service.access_permisson.AccessGroupService;
import com.kairos.service.employment.EmploymentService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.expertise.ExpertiseService;
import com.kairos.service.integration.ActivityIntegrationService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.utils.CPRUtil;
import com.kairos.utils.FormatUtil;
import com.kairos.utils.user_context.UserContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.getDate;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.constants.UserMessagesConstants.*;
import static com.kairos.enums.Day.EVERYDAY;

/*
 *Created By Pavan on 13/11/18
 *
 */

@Transactional
@Service
public class StaffRetrievalService {
    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private EngineerTypeGraphRepository engineerTypeGraphRepository;
    @Inject
    private LanguageGraphRepository languageGraphRepository;
    @Inject
    private OrganizationServiceRepository organizationServiceRepository;
    @Inject
    private ExpertiseGraphRepository expertiseGraphRepository;
    @Inject
    private PositionService positionService;
    @Inject
    private UserGraphRepository userGraphRepository;
    @Inject
    private EnvConfig envConfig;
    @Inject
    private StaffExpertiseRelationShipGraphRepository staffExpertiseRelationShipGraphRepository;
    @Inject
    private EmploymentGraphRepository employmentGraphRepository;
    @Inject
    private OrganizationService organizationService;
    @Inject
    private AccessGroupRepository accessGroupRepository;
    @Inject
    private StaffFilterService staffFilterService;
    @Inject
    private AccessGroupService accessGroupService;
    @Inject
    private EmploymentService employmentService;
    @Inject
    private TimeSlotGraphRepository timeSlotGraphRepository;
    @Inject
    private EmploymentFunctionRelationshipRepository employmentFunctionRelationshipRepository;
    @Inject
    private DayTypeGraphRepository dayTypeGraphRepository;
    @Inject
    private ExpertiseService expertiseService;
    @Inject
    private ReasonCodeGraphRepository reasonCodeGraphRepository;
    @Inject
    private StaffAddressService staffAddressService;
    @Inject
    private TeamGraphRepository teamGraphRepository;


    public Map<String, Object> getPersonalInfo(long staffId, long unitId, String type) {

        StaffPositionDTO staffPositionDTO = null;
        Staff staff = null;
        Organization unit = organizationGraphRepository.findOne(unitId);
        Organization parentOrganization = (unit.isParentOrganization()) ? unit : organizationGraphRepository.getParentOfOrganization(unit.getId());
        if (TEAM.equalsIgnoreCase(type)) {
            staff = staffGraphRepository.getTeamStaff(unitId, staffId);
            staffPositionDTO = new StaffPositionDTO(staff, null);
        } else if (ORGANIZATION.equalsIgnoreCase(type)) {
            staffPositionDTO = staffGraphRepository.getStaffAndEmploymentByUnitId(parentOrganization.getId(), staffId);
            staff = Optional.ofNullable(staffPositionDTO).isPresent() ? staffPositionDTO.getStaff() : null;
        }
        if (staff == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_STAFF_IDANDUNITID_NOTFOUND, staffId, type, unitId);

        }

        Map<String, Object> personalInfo = new HashMap<>(2);
        Long countryId = countryGraphRepository.getCountryIdByUnitId(unitId);

        List<Language> languages;
        List<EngineerTypeDTO> engineerTypes;
        if (countryId != null) {
            engineerTypes = engineerTypeGraphRepository.findEngineerTypeByCountry(countryId);
            languages = languageGraphRepository.getLanguageByCountryId(countryId);
        } else {
            languages = Collections.emptyList();
            engineerTypes = Collections.emptyList();
        }
        personalInfo.put("employmentInfo", positionService.retrieveEmploymentDetails(staffPositionDTO));

        personalInfo.put("personalInfo", retrievePersonalInfo(staff,unitId));
        personalInfo.put("expertise", getExpertisesOfUnitByCountryId(countryId, unit.getId()));
        personalInfo.put("languages", languages);
        personalInfo.put("engineerTypes", engineerTypes);

        personalInfo.putAll(staffAddressService.getAddress(unitId, staffId));
        return personalInfo;
    }

    public List<ExpertiseQueryResult> getExpertisesOfUnitByCountryId(Long countryId, long unitId) {
        List<ExpertiseQueryResult> expertises = new ArrayList<>();
        OrganizationServicesAndLevelQueryResult servicesAndLevel = organizationServiceRepository.getOrganizationServiceIdsByOrganizationId(unitId);
        if (Optional.ofNullable(servicesAndLevel).isPresent() && Optional.ofNullable(servicesAndLevel.getLevelId()).isPresent()) {
            expertises = expertiseGraphRepository.findExpertiseByCountryAndOrganizationServices(countryId, servicesAndLevel.getServicesId(), servicesAndLevel.getLevelId());
        } else if (Optional.ofNullable(servicesAndLevel).isPresent()) {
            expertises = expertiseGraphRepository.findExpertiseByCountryAndOrganizationServices(countryId, servicesAndLevel.getServicesId());
        }
        return expertises;
    }


    private Map<String, Object> retrievePersonalInfo(Staff staff,Long unitId) {
        User user = userGraphRepository.getUserByStaffId(staff.getId());
        Map<String, Object> map = new HashMap<>();
        map.put("userNameUpdated",user.isUserNameUpdated());
        map.put("userName", user.getUserName());
        map.put("firstName", staff.getFirstName());
        map.put("lastName", staff.getLastName());
        map.put("profilePic", (isNotNull(staff.getProfilePic())) ? envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath() + staff.getProfilePic() : staff.getProfilePic());
        map.put("familyName", staff.getFamilyName());
        map.put("currentStatus", staff.getCurrentStatus());
        map.put("signature", staff.getSignature());
        Date inactiveFrom = Optional.ofNullable(staff.getInactiveFrom()).isPresent() ? getDate(staff.getInactiveFrom()) : null;
        map.put("inactiveFrom", inactiveFrom);
        map.put("languageId", staffGraphRepository.getLanguageId(staff.getId()));
        map.put("contactDetail", staffGraphRepository.getContactDetail(staff.getId()));
        map.put("cprNumber", user.getCprNumber());
        map.put("careOfName", staff.getCareOfName());
        map.put("gender", user.getGender());
        map.put("pregnant", user.isPregnant());
        List<SectorAndStaffExpertiseQueryResult> staffExpertiseQueryResults = ObjectMapperUtils.copyPropertiesOfListByMapper(staffExpertiseRelationShipGraphRepository.getSectorWiseExpertiseWithExperience(staff.getId()), SectorAndStaffExpertiseQueryResult.class);
        map.put("sectorWiseExpertise", getSectorWiseStaffAndExpertise(staffExpertiseQueryResults));
        map.put("expertiseIds", getExpertiseIds(staffExpertiseQueryResults));
        map.put("speedPercent", staff.getSpeedPercent());
        map.put("workPercent", staff.getWorkPercent());
        map.put("overtime", staff.getOvertime());
        map.put("costDay", staff.getCostDay());
        map.put("costCall", staff.getCostCall());
        map.put("costKm", staff.getCostKm());
        map.put("costHour", staff.getCostHour());
        map.put("costHourOvertime", staff.getCostHourOvertime());
        map.put("capacity", staff.getCapacity());
        map.put("teamDetails", teamGraphRepository.getTeamDetailsOfStaff(staff.getId(),unitId));

        return map;
    }

    private Integer nextSeniorityLevelInMonths(List<SeniorityLevel> seniorityLevels, int currentExperienceInMonths) {
        Collections.sort(seniorityLevels);
        Integer nextSeniorityLevelInMonths = null;
        for (int i = 0; i < seniorityLevels.size(); i++) {
            if (currentExperienceInMonths < seniorityLevels.get(i).getFrom() * 12) {
                nextSeniorityLevelInMonths = seniorityLevels.get(i).getFrom() * 12 - currentExperienceInMonths;
                break;
            }
        }
        return nextSeniorityLevelInMonths;
    }

    private SeniorityLevel calculateApplicableSeniorityLevel(List<SeniorityLevel> seniorityLevels, int maxExperience) {
        Collections.sort(seniorityLevels);
        SeniorityLevel seniorityLevel = null;
        for (int i = 0; i < seniorityLevels.size(); i++) {
            if (seniorityLevels.get(i).getTo() == null) {
                seniorityLevel = seniorityLevels.get(i);
                break;
            }
            if (maxExperience < seniorityLevels.get(i).getTo() * 12) {
                seniorityLevel = seniorityLevels.get(i);
                break;
            }
        }
        return seniorityLevel;
    }

    public List<StaffDTO> getStaffByUnit(Long unitId) {
        Organization organization = organizationService.fetchParentOrganization(unitId);
        List<Staff> staffs = staffGraphRepository.getAllStaffByUnitId(organization.getId());
        return ObjectMapperUtils.copyPropertiesOfListByMapper(staffs, StaffDTO.class);
    }

    public List<StaffResultDTO> getStaffIdsAndReasonCodeByUserId(Long userId) {
        List<StaffInformationQueryResult> staffUnitWrappers = staffGraphRepository.getStaffAndUnitTimezoneByUserIdAndReasonCode(userId, ReasonCodeType.ATTENDANCE);
        return ObjectMapperUtils.copyPropertiesOfListByMapper(staffUnitWrappers, StaffResultDTO.class);

    }

    public List<StaffResultDTO> getStaffIdsUnitByUserId(Long userId) {
        List<StaffInformationQueryResult> staffUnitWrappers = staffGraphRepository.getStaffIdsAndUnitByUserId(userId);
        return ObjectMapperUtils.copyPropertiesOfListByMapper(staffUnitWrappers, StaffResultDTO.class);

    }


    public List<StaffPersonalDetail> getStaffDetailByIds(Set<Long> staffIds) {
        return employmentGraphRepository.getStaffDetailByIds(staffIds, DateUtils.getCurrentLocalDate());
    }

    public Long getStaffIdOfLoggedInUser(Long unitId) {
        Organization parentOrganization = organizationService.fetchParentOrganization(unitId);
        return staffGraphRepository.findStaffIdByUserId(UserContext.getUserDetails().getId(), parentOrganization.getId());
    }

    public StaffAccessGroupQueryResult getAccessGroupIdsOfStaff(Long unitId) {
        StaffAccessGroupQueryResult staffAccessGroupQueryResult;
        Long staffId = getStaffIdOfLoggedInUser(unitId);
        long loggedinUserId = UserContext.getUserDetails().getId();
        Boolean isSuperAdmin = false;
        staffAccessGroupQueryResult = accessGroupRepository.getAccessGroupIdsByStaffIdAndUnitId(staffId, unitId);
        if (!Optional.ofNullable(staffAccessGroupQueryResult).isPresent()) {
            staffAccessGroupQueryResult = new StaffAccessGroupQueryResult();
            isSuperAdmin = userGraphRepository.checkIfUserIsCountryAdmin(loggedinUserId, AppConstants.SUPER_ADMIN);
            Organization parentOrganization = organizationService.fetchParentOrganization(unitId);
            staffAccessGroupQueryResult.setCountryId(UserContext.getUserDetails().getCountryId());
            staffId = staffGraphRepository.findHubStaffIdByUserId(UserContext.getUserDetails().getId(), parentOrganization.getId());
        }
        UserAccessRoleDTO userAccessRoleDTO = accessGroupService.findUserAccessRole(unitId);
        staffAccessGroupQueryResult.setManagement(userAccessRoleDTO.getManagement());
        staffAccessGroupQueryResult.setCountryAdmin(isSuperAdmin);
        staffAccessGroupQueryResult.setStaffId(staffId);
        return staffAccessGroupQueryResult;
    }

    public Map<String, Object> getNotes(long staffId) {
        Staff staff = staffGraphRepository.findOne(staffId);
        if (staff == null) {
            return null;
        }
        return staff.retrieveNotes();
    }


    public Map<String, Object> getStaffWithFilter(Long unitId, String type, long id, StaffFilterDTO staffFilterDTO, String moduleId) {

        List<AccessGroup> roles = null;
        Map<String, Object> map = new HashMap<>();
        if (ORGANIZATION.equalsIgnoreCase(type)) {
            map.put("staffList", staffFilterService.getAllStaffByUnitId(unitId, staffFilterDTO, moduleId).getStaffList());
            roles = accessGroupService.getAccessGroups(unitId);
        }
        map.put("roles", roles);
        List<Map<String, Object>> teams = teamGraphRepository.getTeams(unitId);
        map.put("teamList", (teams.size() != 0) ? teams.get(0).get("teams") : Collections.emptyList());
        return map;
    }


    /**
     * Method to set current staff role if present
     *
     * @param accessGroupQueryResult
     * @return
     * @author mohit
     */
    public String getStaffAccessRole(AccessGroupStaffQueryResult accessGroupQueryResult) {
        ZoneId organizationTimeZoneId = accessGroupQueryResult.getOrganization().getTimeZone();
        LocalDate loginDate = ZonedDateTime.now(organizationTimeZoneId).toLocalDate();
        DayOfWeek loginDay = loginDate.getDayOfWeek();
        String STAFF_CURRENT_ROLE = null;
        AccessGroupStaffQueryResult accessGroupQueryResultCopy = ObjectMapperUtils.copyPropertiesByMapper(accessGroupQueryResult, AccessGroupStaffQueryResult.class);
        for (AccessGroupDayTypesQueryResult accessGroupDayTypes : accessGroupQueryResultCopy.getDayTypesByAccessGroup()) {
            if (!accessGroupDayTypes.getAccessGroup().isAllowedDayTypes()) {
                STAFF_CURRENT_ROLE = accessGroupDayTypes.getAccessGroup().getRole().name();
                if (AccessGroupRole.MANAGEMENT.name().equals(STAFF_CURRENT_ROLE)) {
                    break;
                }
            }
            List<DayTypeCountryHolidayCalenderQueryResult> dayTypeList = accessGroupDayTypes.getDayTypes();
            String staffRole;
            if (CollectionUtils.isNotEmpty(dayTypeList)) {
                for (DayTypeCountryHolidayCalenderQueryResult dayType : dayTypeList) {
                    if (!dayType.isHolidayType()) {
                        if (isNull(dayType.getValidDays())) {
                            exceptionService.dataNotMatchedException(MESSAGE_DAY_TYPE_ABSENT, accessGroupDayTypes.getAccessGroup().getName());
                        }
                        List<String> validDays = dayType.getValidDays().stream().map(day -> day.name()).collect(Collectors.toList());
                        if (validDays.contains(loginDay.toString()) || validDays.contains(EVERYDAY)) {
                            staffRole = accessGroupDayTypes.getAccessGroup().getRole().name();
                            if (AccessGroupRole.MANAGEMENT.name().equals(staffRole)) {
                                STAFF_CURRENT_ROLE = staffRole;
                                //staffListByRole = staff;
                                break;
                            } else if (AccessGroupRole.STAFF.name().equals(staffRole)) {
                                STAFF_CURRENT_ROLE = staffRole;
                            }
                        }
                    } else if (dayType.isHolidayType() && !dayType.isAllowTimeSettings() && CollectionUtils.isNotEmpty(dayType.getCountryHolidayCalenders())) {
                        Set<LocalDate> dates = dayType.getCountryHolidayCalenders().stream().filter(cal -> cal.getHolidayDate() != null).map(date -> ZonedDateTime.ofInstant(DateUtils.asDate(date.getHolidayDate()).toInstant(), organizationTimeZoneId).toLocalDate()).collect(Collectors.toSet());
                        if (dates.contains(loginDate)) {
                            staffRole = accessGroupDayTypes.getAccessGroup().getRole().name();
                            if (AccessGroupRole.MANAGEMENT.name().equals(staffRole)) {
                                STAFF_CURRENT_ROLE = staffRole;
                                // staffListByRole = staff;
                                break;
                            } else if (AccessGroupRole.STAFF.name().equals(staffRole)) {
                                STAFF_CURRENT_ROLE = staffRole;
                            }
                        }
                    } else if (dayType.isHolidayType() && dayType.isAllowTimeSettings() && CollectionUtils.isNotEmpty(dayType.getCountryHolidayCalenders())) {
                        Optional<CountryHolidayCalendarQueryResult> countryHolidayCalender = dayType.getCountryHolidayCalenders().stream().filter(cal -> cal.getHolidayDate() != null && ZonedDateTime.ofInstant(DateUtils.asDate(cal.getHolidayDate()).toInstant(), organizationTimeZoneId).toLocalDate().equals(loginDate)).findFirst();
                        LocalTime localTime = LocalTime.now();
                        if (countryHolidayCalender.isPresent() && (countryHolidayCalender.get().getStartTime().isBefore(localTime) || countryHolidayCalender.get().getStartTime().equals(localTime)) && (countryHolidayCalender.get().getEndTime().isAfter(localTime) || countryHolidayCalender.get().getEndTime().equals(localTime))) {
                            staffRole = accessGroupDayTypes.getAccessGroup().getRole().name();
                            if (AccessGroupRole.MANAGEMENT.name().equals(staffRole)) {
                                STAFF_CURRENT_ROLE = staffRole;
                                break;
                            } else if (AccessGroupRole.STAFF.name().equals(staffRole)) {
                                STAFF_CURRENT_ROLE = staffRole;
                            }
                        }

                    }
                }
            }
            if (AccessGroupRole.MANAGEMENT.name().equals(STAFF_CURRENT_ROLE)) {
                break;
            }
        }
        return STAFF_CURRENT_ROLE;
    }


    public Map<String, Object> getStaff(String type, long id, Boolean allStaffRequired) {

        List<StaffPersonalDetailDTO> staff = null;
        Long countryId = null;
        List<AccessGroup> roles = null;
        List<EngineerTypeDTO> engineerTypes = null;
        if (ORGANIZATION.equalsIgnoreCase(type)) {
            staff = getStaffWithBasicInfo(id, allStaffRequired);
            roles = accessGroupService.getAccessGroups(id);
            countryId = countryGraphRepository.getCountryIdByUnitId(id);
            engineerTypes = engineerTypeGraphRepository.findEngineerTypeByCountry(countryId);
        } else if (TEAM.equalsIgnoreCase(type)) {
            staff = staffGraphRepository.getStaffByTeamId(id, envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
            Organization organization = organizationGraphRepository.getOrganizationByTeamId(id);
            roles = accessGroupService.getAccessGroups(organization.getId());
            countryId = countryGraphRepository.getCountryIdByUnitId(organization.getId());
        }

        Map<String, Object> map = new HashMap<>();
        map.put("staffList", staff);
        map.put("engineerTypes", engineerTypes);
        map.put("engineerList", engineerTypeGraphRepository.findEngineerTypeByCountry(countryId));
        map.put("roles", roles);
        return map;
    }

    /* @Modified by VIPUL
     * */

    // TODO NEED TO FIX map
    public List<StaffPersonalDetailDTO> getStaffWithBasicInfo(long unitId) {
        Organization unit = organizationGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(unit).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_UNIT_ID_NOTFOUND, unitId);
        }
        return staffGraphRepository.getAllStaffHavingEmploymentByUnitIdMap(unitId, envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
    }

    public List<StaffPersonalDetailDTO> getStaffWithBasicInfo(long unitId, Boolean allStaffRequired) {
        Organization unit = organizationGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(unit).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_UNIT_ID_NOTFOUND, unitId);

        }
        List<StaffPersonalDetailDTO> staffPersonalDetailDTOS;
        if (allStaffRequired) {
            Organization parentOrganization = (unit.isParentOrganization()) ? unit : organizationGraphRepository.getParentOfOrganization(unit.getId());
            staffPersonalDetailDTOS = staffGraphRepository.getAllStaffByUnitId(parentOrganization.getId(), envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
        } else {
            staffPersonalDetailDTOS = staffGraphRepository.getAllStaffHavingEmploymentByUnitId(unitId, envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
        }
        return staffPersonalDetailDTOS;
    }

    public List<StaffAdditionalInfoQueryResult> getStaffWithAdditionalInfo(long unitId) {
        Organization unit = organizationGraphRepository.findOne(unitId, 0);
        if (unit == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_UNIT_NOTFOUND, unitId);
        }
        return staffGraphRepository.getStaffAndCitizenDetailsOfUnit(unitId);
    }

    public Map<String, Object> getExpertiseOfStaff(long countryId, long staffId) {
        Staff staff = staffGraphRepository.findOne(staffId);
        if (staff == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("allExpertise", expertiseGraphRepository.getAllExpertiseByCountry(countryId));
        map.put("myExpertise", staffExpertiseRelationShipGraphRepository.getAllExpertiseByStaffId(staffId).stream().map(Expertise::getId).collect(Collectors.toList()));
        return map;
    }

    public List<Staff> getPlannerInOrganization(Long organizationId) {
        return staffGraphRepository.findAllPlanners(organizationId);

    }

    public List<Staff> getManagersInOrganization(Long organizationId) {
        return staffGraphRepository.findAllManager(organizationId);

    }

    public List<Staff> getVisitatorsInOrganization(Long organizationId) {
        return staffGraphRepository.findAllVisitator(organizationId);

    }

    public List<Staff> getTeamLeadersInOrganization(Long organizationId) {
        return staffGraphRepository.findAllTeamLeader(organizationId);
    }

    public UserAccessRoleDTO getAccessRolesOfStaffByUserId(Long unitId) {
        return accessGroupService.findUserAccessRole(unitId);
    }

    public List<StaffWithSkillDTO> getStaffByExperties(Long unitId, List<Long> expertiesIds) {
        List<Staff> staffs = staffGraphRepository.getStaffByExperties(unitId, expertiesIds);
        List<Skill> skills = staffGraphRepository.getSkillByStaffIds(staffs.stream().map(Staff::getId).collect(Collectors.toList()));
        List<StaffWithSkillDTO> staffDTOS = new ArrayList<>(staffs.size());
        staffs.forEach(s -> {
            StaffWithSkillDTO staffDTO = new StaffWithSkillDTO(s.getId(), s.getFirstName(), getSkillSet(skills));
            EmploymentAndPositionDTO employmentAndPositionDTO = employmentService.getEmploymentsOfStaff(unitId, s.getId(), true);
            List<EmploymentQueryResult> employments = employmentAndPositionDTO.getEmployments();
            expertiesIds.forEach(expertiseId -> employments.forEach(employment -> {
                        if (employment.getExpertise().getId().equals(expertiseId)) {
                            staffDTO.setUnitEmploymentPositionId(employment.getId());
                            staffDTOS.add(staffDTO);
                        }
                    })
            );

        });
        return staffDTOS;
    }

    private Set<SkillDTO> getSkillSet(List<Skill> skills) {
        return skills.stream().map(skill -> new SkillDTO(skill.getId(), skill.getName(), skill.getDescription())).collect(Collectors.toSet());
    }


    public List<UnitStaffQueryResult> getUnitWiseStaffList() {
        return staffGraphRepository.getStaffListOfUnitWithBasicInfo();
    }


    public List<StaffEmploymentQueryResult> getStaffByStaffIncludeFilterForPriorityGroups(StaffIncludeFilterDTO staffIncludeFilterDTO, Long unitId) {
        return staffGraphRepository.getStaffByPriorityGroupStaffIncludeFilter(staffIncludeFilterDTO, unitId);
    }

    /**
     * @param startDate
     * @param employmentId
     * @param organizationId
     * @param type
     * @param reasonCodeIds
     * @return
     */
    public StaffAdditionalInfoDTO getStaffEmploymentDataByEmploymentId(LocalDate startDate, Long employmentId, long organizationId, String type, Set<Long> reasonCodeIds) {
        StaffAdditionalInfoQueryResult staffAdditionalInfoQueryResult = staffGraphRepository.getStaffInfoByUnitIdAndEmploymentId(employmentId);
        if (isNull(staffAdditionalInfoQueryResult)) {
            exceptionService.dataNotFoundByIdException(MESSAGE_STAFF_UNIT_PERMISSION_NOTFOUND);
        }
        return getStaffEmploymentData(startDate, staffAdditionalInfoQueryResult, employmentId, organizationId, type, reasonCodeIds);
    }

    /**
     * @param startDate
     * @param staffId
     * @param employmentId
     * @param organizationId
     * @param type
     * @param reasonCodeIds
     * @return
     */
    public StaffAdditionalInfoDTO getStaffEmploymentDataByEmploymentIdAndStaffId(LocalDate startDate, long staffId, Long employmentId, long organizationId, String type, Set<Long> reasonCodeIds) {
        StaffAdditionalInfoQueryResult staffAdditionalInfoQueryResult = staffGraphRepository.getStaffInfoByUnitIdAndStaffId(organizationId, staffId);
        if (isNull(staffAdditionalInfoQueryResult)) {
            exceptionService.dataNotFoundByIdException(MESSAGE_STAFF_UNIT_PERMISSION_NOTFOUND);
        }
        return getStaffEmploymentData(startDate, staffAdditionalInfoQueryResult, employmentId, organizationId, type, reasonCodeIds);
    }

    /**
     * @param shiftDate
     * @param staffAdditionalInfoQueryResult
     * @param employmentId
     * @param organizationId
     * @param type
     * @param reasonCodeIds
     * @return
     */
    private StaffAdditionalInfoDTO getStaffEmploymentData(LocalDate shiftDate, StaffAdditionalInfoQueryResult staffAdditionalInfoQueryResult, Long employmentId, long organizationId, String type, Set<Long> reasonCodeIds) {
        Organization organization = organizationService.getOrganizationDetail(organizationId, type);
        Long countryId = organization.isParentOrganization() ? organization.getCountry().getId() : organizationGraphRepository.getCountryByParentOrganization(organizationId).getId();
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = ObjectMapperUtils.copyPropertiesByMapper(staffAdditionalInfoQueryResult, StaffAdditionalInfoDTO.class);
        StaffEmploymentDetails employment = employmentService.getEmploymentDetails(employmentId);
        if (Optional.ofNullable(employment).isPresent()) {
            staffAdditionalInfoDTO.setStaffAge(CPRUtil.getAgeFromCPRNumber(staffAdditionalInfoDTO.getCprNumber()));
            Long functionId = null;
            if (Optional.ofNullable(shiftDate).isPresent()) {
                functionId = employmentFunctionRelationshipRepository.getApplicableFunction(employmentId, shiftDate.toString());
                staffAdditionalInfoDTO.setStaffAge(CPRUtil.getAgeByCPRNumberAndStartDate(staffAdditionalInfoDTO.getCprNumber(), shiftDate));
            }
            employment.setCountryId(countryId);
            employment.setUnitTimeZone(organization.getTimeZone());
            employment.setFunctionId(functionId);
            List<ReasonCode> reasonCodes;
            if (CollectionUtils.isNotEmpty(reasonCodeIds)) {
                reasonCodes = reasonCodeGraphRepository.findByIds(reasonCodeIds);
                staffAdditionalInfoDTO.setReasonCodes(ObjectMapperUtils.copyPropertiesOfListByMapper(reasonCodes, ReasonCodeDTO.class));
            }
            List<TimeSlotWrapper> timeSlotWrappers = timeSlotGraphRepository.getShiftPlanningTimeSlotsByUnitIds(Arrays.asList(organization.getId()), TimeSlotType.SHIFT_PLANNING);
            if (isCollectionEmpty(timeSlotWrappers)) {
                exceptionService.actionNotPermittedException("timeslot.not.found");
            }
            staffAdditionalInfoDTO.setTimeSlotSets(ObjectMapperUtils.copyPropertiesOfListByMapper(timeSlotWrappers, com.kairos.dto.user.country.time_slot.TimeSlotWrapper.class));
            List<Map<String, Object>> publicHolidaysResult = FormatUtil.formatNeoResponse(countryGraphRepository.getCountryAllHolidays(countryId));
            Map<Long, List<Map>> publicHolidayMap = publicHolidaysResult.stream().filter(d -> d.get("dayTypeId") != null).collect(Collectors.groupingBy(k -> ((Long) k.get("dayTypeId")), Collectors.toList()));
            List<DayType> dayTypes = dayTypeGraphRepository.findByCountryId(countryId);
            List<DayTypeDTO> dayTypeDTOS = dayTypes.stream().map(dayType ->
                    new DayTypeDTO(dayType.getId(), dayType.getName(), dayType.getValidDays(), ObjectMapperUtils.copyPropertiesOfListByMapper(publicHolidayMap.get(dayType.getId()), CountryHolidayCalenderDTO.class), dayType.isHolidayType(), dayType.isAllowTimeSettings())
            ).collect(Collectors.toList());
            staffAdditionalInfoDTO.setDayTypes(dayTypeDTOS);
            //UserAccessRoleDTO userAccessRole = accessGroupService.checkIfUserHasAccessByRoleInUnit(organization.getId());
            //staffAdditionalInfoDTO.setUser(userAccessRole);
            staffAdditionalInfoDTO.setUnitTimeZone(organization.getTimeZone());
            UserAccessRoleDTO userAccessRoleDTO = accessGroupService.findUserAccessRole(organization.getId());
            SeniorAndChildCareDaysDTO seniorAndChildCareDaysDTO = expertiseService.getSeniorAndChildCareDays(employment.getExpertise().getId());
            staffAdditionalInfoDTO.setSeniorAndChildCareDays(seniorAndChildCareDaysDTO);
            staffAdditionalInfoDTO.setUserAccessRoleDTO(userAccessRoleDTO);
            staffAdditionalInfoDTO.setUnitId(organization.getId());
            staffAdditionalInfoDTO.setOrganizationNightEndTimeTo(organization.getNightEndTimeTo());
            staffAdditionalInfoDTO.setOrganizationNightStartTimeFrom(organization.getNightStartTimeFrom());
            staffAdditionalInfoDTO.setEmployment(employment);
        }
        return staffAdditionalInfoDTO;
    }

    public void setRequiredDataForShiftCreationInWrapper(StaffEmploymentUnitDataWrapper staffEmploymentUnitDataWrapper, Organization organization, Long countryId, Long expertiseId) {
        List<TimeSlotWrapper> timeSlotWrappers = timeSlotGraphRepository.getShiftPlanningTimeSlotsByUnitIds(Arrays.asList(organization.getId()), TimeSlotType.SHIFT_PLANNING);
        staffEmploymentUnitDataWrapper.setTimeSlotWrappers(ObjectMapperUtils.copyPropertiesOfListByMapper(timeSlotWrappers, com.kairos.dto.user.country.time_slot.TimeSlotWrapper.class));
        List<Map<String, Object>> publicHolidaysResult = FormatUtil.formatNeoResponse(countryGraphRepository.getCountryAllHolidays(countryId));
        Map<Long, List<Map>> publicHolidayMap = publicHolidaysResult.stream().filter(d -> d.get("dayTypeId") != null).collect(Collectors.groupingBy(k -> ((Long) k.get("dayTypeId")), Collectors.toList()));
        List<DayType> dayTypes = dayTypeGraphRepository.findByCountryId(countryId);
        List<DayTypeDTO> dayTypeDTOS = dayTypes.stream().map(dayType ->
                new DayTypeDTO(dayType.getId(), dayType.getName(), dayType.getValidDays(), ObjectMapperUtils.copyPropertiesOfListByMapper(publicHolidayMap.get(dayType.getId()), CountryHolidayCalenderDTO.class), dayType.isHolidayType(), dayType.isAllowTimeSettings())
        ).collect(Collectors.toList());
        staffEmploymentUnitDataWrapper.setDayTypes(dayTypeDTOS);
        staffEmploymentUnitDataWrapper.setDayTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(dayTypes, DayTypeDTO.class));
        staffEmploymentUnitDataWrapper.setUnitTimeZone(organization.getTimeZone());
        UserAccessRoleDTO userAccessRoleDTO = accessGroupService.findUserAccessRole(organization.getId());
        SeniorAndChildCareDaysDTO seniorAndChildCareDaysDTO = expertiseService.getSeniorAndChildCareDays(expertiseId);
        staffEmploymentUnitDataWrapper.setSeniorAndChildCareDays(seniorAndChildCareDaysDTO);
        staffEmploymentUnitDataWrapper.setUser(userAccessRoleDTO);
        staffEmploymentUnitDataWrapper.setUnitId(organization.getId());
        staffEmploymentUnitDataWrapper.setOrganizationNightEndTimeTo(organization.getNightEndTimeTo());
        staffEmploymentUnitDataWrapper.setOrganizationNightStartTimeFrom(organization.getNightStartTimeFrom());
    }

    public StaffAdditionalInfoDTO getStaffEmploymentData(Long employmentId, Long unitId) {
        StaffEmploymentDetails employmentDetails = employmentService.getEmploymentDetails(employmentId);
        if (!Optional.ofNullable(employmentDetails).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_EMPLOYMENT_ID_NOTEXIST, employmentId);
        }
        UserAccessRoleDTO userAccessRole = accessGroupService.findUserAccessRole(unitId);
        List<FunctionDTO> appliedFunctionDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(employmentDetails.getAppliedFunctions(), FunctionDTO.class);
        employmentDetails.setAppliedFunctions(appliedFunctionDTOS);
        List<ReasonCodeResponseDTO> reasonCodeQueryResults = reasonCodeGraphRepository.findReasonCodesByUnitIdAndReasonCodeType(unitId, ReasonCodeType.TIME_TYPE);
        List<ReasonCodeDTO> reasonCodeDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(reasonCodeQueryResults, ReasonCodeDTO.class);
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = new StaffAdditionalInfoDTO(reasonCodeDTOS, employmentDetails);
        staffAdditionalInfoDTO.setUserAccessRoleDTO(userAccessRole);
        return staffAdditionalInfoDTO;
    }

    public List<StaffAdditionalInfoDTO> getStaffsEmploymentData(List<Long> staffIds, List<Long> employmentIds, long id, String type) {
        Organization organization = organizationService.getOrganizationDetail(id, type);
        Long countryId = organization.isParentOrganization() ? organization.getCountry().getId() : organizationGraphRepository.getCountryByParentOrganization(organization.getId()).getId();
        List<TimeSlotWrapper> timeSlotWrappers = timeSlotGraphRepository.getShiftPlanningTimeSlotsByUnitIds(Arrays.asList(organization.getId()), TimeSlotType.SHIFT_PLANNING);
        List<StaffAdditionalInfoQueryResult> staffAdditionalInfoQueryResult = staffGraphRepository.getStaffInfoByUnitIdAndStaffIds(organization.getId(), staffIds,envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
        List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(staffAdditionalInfoQueryResult, StaffAdditionalInfoDTO.class);
        List<StaffEmploymentDetails> employmentDetails = employmentService.getEmploymentDetails(employmentIds, organization, countryId);
        List<Map<String, Object>> publicHolidaysResult = FormatUtil.formatNeoResponse(countryGraphRepository.getCountryAllHolidays(countryId));
        Map<Long, List<Map>> publicHolidayMap = publicHolidaysResult.stream().filter(d -> d.get("dayTypeId") != null).collect(Collectors.groupingBy(k -> ((Long) k.get("dayTypeId")), Collectors.toList()));
        List<DayType> dayTypes = dayTypeGraphRepository.findByCountryId(countryId);
        List<DayTypeDTO> dayTypeDTOS = dayTypes.stream().map(dayType ->
                new DayTypeDTO(dayType.getId(), dayType.getName(), dayType.getValidDays(), ObjectMapperUtils.copyPropertiesOfListByMapper(publicHolidayMap.get(dayType.getId()), CountryHolidayCalenderDTO.class), dayType.isHolidayType(), dayType.isAllowTimeSettings())
        ).collect(Collectors.toList());
        UserAccessRoleDTO userAccessRoleDTO = accessGroupService.findUserAccessRole(organization.getId());
        // TODO incorrect we dont need to set in all staff
        staffAdditionalInfoDTOS.forEach(staffAdditionalInfoDTO -> {
            staffAdditionalInfoDTO.setDayTypes(dayTypeDTOS);

            staffAdditionalInfoDTO.setUnitId(organization.getId());
            staffAdditionalInfoDTO.setOrganizationNightEndTimeTo(organization.getNightEndTimeTo());
            staffAdditionalInfoDTO.setTimeSlotSets(ObjectMapperUtils.copyPropertiesOfListByMapper(timeSlotWrappers, com.kairos.dto.user.country.time_slot.TimeSlotWrapper.class));
            staffAdditionalInfoDTO.setOrganizationNightStartTimeFrom(organization.getNightStartTimeFrom());
            staffAdditionalInfoDTO.setUserAccessRoleDTO(userAccessRoleDTO);
        });
        Map<Long,StaffAdditionalInfoDTO> staffAdditionalInfoDTOMap = staffAdditionalInfoDTOS.stream().collect(Collectors.toMap(k->k.getId(),v->v));
        List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOList = new ArrayList<>();
        for (StaffEmploymentDetails employmentDetail : employmentDetails) {
            StaffAdditionalInfoDTO staffAdditionalInfoDTO = ObjectMapperUtils.copyPropertiesByMapper(staffAdditionalInfoDTOMap.get(employmentDetail.getStaffId()),StaffAdditionalInfoDTO.class);
            staffAdditionalInfoDTO.setEmployment(employmentDetail);
            staffAdditionalInfoDTOList.add(staffAdditionalInfoDTO);
        }
        return staffAdditionalInfoDTOList;
    }

    public List<SectorAndStaffExpertiseQueryResult> getSectorWiseStaffAndExpertise(List<SectorAndStaffExpertiseQueryResult> staffExpertiseQueryResults) {
        staffExpertiseQueryResults.forEach(staffExpertiseQueryResult -> {
            int maxExperience = staffExpertiseQueryResult.getExpertiseWithExperience().stream().max(Comparator.comparingInt(StaffExpertiseQueryResult::getRelevantExperienceInMonths)).get().getRelevantExperienceInMonths();
            staffExpertiseQueryResult.getExpertiseWithExperience().forEach(expertiseQueryResult -> {
                expertiseQueryResult.setRelevantExperienceInMonths((int) ChronoUnit.MONTHS.between(DateUtils.asLocalDate(expertiseQueryResult.getExpertiseStartDate()), LocalDate.now()));
                expertiseQueryResult.setNextSeniorityLevelInMonths(nextSeniorityLevelInMonths(expertiseQueryResult.getSeniorityLevels(), expertiseQueryResult.getRelevantExperienceInMonths()));
                expertiseQueryResult.setSeniorityLevel(calculateApplicableSeniorityLevel(expertiseQueryResult.getSeniorityLevels(), maxExperience));
                if (expertiseQueryResult.isEmploymentExists()) {
                    staffExpertiseQueryResult.setEmploymentExists(true);
                }
            });
        });
        return staffExpertiseQueryResults;
    }

    private List<Long> getExpertiseIds(List<SectorAndStaffExpertiseQueryResult> staffExpertiseQueryResults) {
        List<Long> expertiseIds = new ArrayList<>();
        for (SectorAndStaffExpertiseQueryResult currentSectorAndExpertise : staffExpertiseQueryResults) {
            currentSectorAndExpertise.getExpertiseWithExperience().forEach(current -> expertiseIds.add(current.getExpertiseId()));
        }
        return expertiseIds;
    }

    public List<StaffExperienceInExpertiseDTO> getExpertiseWithExperienceByStaffIdAndUnitId(Long staffId, long unitId) {
        List<StaffExperienceInExpertiseDTO> staffExperienceInExpertiseDTOList = new ArrayList<>();
        OrganizationServicesAndLevelQueryResult servicesAndLevel = organizationServiceRepository.getOrganizationServiceIdsByOrganizationId(unitId);
        if (Optional.ofNullable(servicesAndLevel).isPresent()) {
            if (CollectionUtils.isNotEmpty(servicesAndLevel.getServicesId()) && Optional.ofNullable(servicesAndLevel.getLevelId()).isPresent()) {
                staffExperienceInExpertiseDTOList = staffExpertiseRelationShipGraphRepository.getExpertiseWithExperienceByStaffIdAndServicesAndLevel(staffId, servicesAndLevel.getServicesId(), servicesAndLevel.getLevelId());
            } else if (CollectionUtils.isNotEmpty(servicesAndLevel.getServicesId())) {
                staffExperienceInExpertiseDTOList = staffExpertiseRelationShipGraphRepository.getExpertiseWithExperienceByStaffIdAndServices(staffId, servicesAndLevel.getServicesId());
            }
        }
        return staffExperienceInExpertiseDTOList;
    }
}
