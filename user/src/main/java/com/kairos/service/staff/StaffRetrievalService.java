package com.kairos.service.staff;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.CommonsExceptionUtil;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.config.env.EnvConfig;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.open_shift.priority_group.StaffIncludeFilterDTO;
import com.kairos.dto.activity.shift.FunctionDTO;
import com.kairos.dto.activity.shift.StaffEmploymentDetails;
import com.kairos.dto.activity.tags.TagDTO;
import com.kairos.dto.planner.shift_planning.ShiftPlanningProblemSubmitDTO;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.skill.SkillDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.expertise.SeniorAndChildCareDaysDTO;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.dto.user.staff.StaffFilterDTO;
import com.kairos.dto.user.staff.StaffWithSkillDTO;
import com.kairos.dto.user.staff.employment.EmploymentDTO;
import com.kairos.dto.user.staff.employment.StaffEmploymentUnitDataWrapper;
import com.kairos.dto.user.staff.staff.StaffChildDetailDTO;
import com.kairos.dto.user.staff.staff.StaffResultDTO;
import com.kairos.dto.user.team.TeamDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.FilterType;
import com.kairos.enums.TimeSlotType;
import com.kairos.enums.reason_code.ReasonCodeType;
import com.kairos.enums.time_slot.TimeSlotMode;
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
import com.kairos.persistence.model.organization.OrganizationBaseEntity;
import com.kairos.persistence.model.organization.Unit;
import com.kairos.persistence.model.organization.services.OrganizationServicesAndLevelQueryResult;
import com.kairos.persistence.model.organization.time_slot.TimeSlotWrapper;
import com.kairos.persistence.model.query_wrapper.CountryHolidayCalendarQueryResult;
import com.kairos.persistence.model.staff.*;
import com.kairos.persistence.model.staff.permission.UnitStaffQueryResult;
import com.kairos.persistence.model.staff.personal_details.*;
import com.kairos.persistence.model.staff.position.EmploymentAndPositionDTO;
import com.kairos.persistence.model.staff.position.StaffPositionDTO;
import com.kairos.persistence.model.user.employment.Employment;
import com.kairos.persistence.model.user.employment.query_result.EmploymentQueryResult;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import com.kairos.persistence.model.user.expertise.response.ExpertiseLineQueryResult;
import com.kairos.persistence.model.user.expertise.response.ExpertiseQueryResult;
import com.kairos.persistence.model.user.filter.FilterSelection;
import com.kairos.persistence.model.user.language.Language;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.repository.organization.OrganizationBaseRepository;
import com.kairos.persistence.repository.organization.OrganizationServiceRepository;
import com.kairos.persistence.repository.organization.TeamGraphRepository;
import com.kairos.persistence.repository.organization.UnitGraphRepository;
import com.kairos.persistence.repository.organization.time_slot.TimeSlotGraphRepository;
import com.kairos.persistence.repository.repository_impl.StaffGraphRepositoryImpl;
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
import com.kairos.service.country.CountryService;
import com.kairos.service.employment.EmploymentService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.expertise.ExpertiseService;
import com.kairos.service.integration.ActivityIntegrationService;
import com.kairos.service.organization.GroupService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.utils.CPRUtil;
import com.kairos.utils.FormatUtil;
import com.kairos.wrapper.staff.StaffEmploymentTypeWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.getLocalDate;
import static com.kairos.commons.utils.ObjectMapperUtils.copyCollectionPropertiesByMapper;
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
    public static final String DAY_TYPE_ID = "dayTypeId";
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(StaffRetrievalService.class);
    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private UnitGraphRepository unitGraphRepository;
    @Inject
    private CountryService countryService;
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
    private OrganizationBaseRepository organizationBaseRepository;
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
    @Inject private ActivityIntegrationService activityIntegrationService;
    @Inject private StaffGraphRepositoryImpl staffGraphRepositoryImpl;
    @Inject private GroupService groupService;


    public Map<String, Object> getDefaultDataOfStaff(long staffId, long unitId) {
        StaffPositionDTO staffPositionDTO = staffGraphRepository.getStaffAndEmploymentByStaffId(staffId);
        Map<String, Object> personalInfo = new HashMap<>(4);
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
        personalInfo.put("expertise", getExpertisesOfUnitByCountryId(countryId, unitId));
        personalInfo.put("languages", languages);
        personalInfo.put("engineerTypes", engineerTypes);
        personalInfo.putAll(staffAddressService.getAddress(staffId));
        return personalInfo;
    }

    public StaffDTO getPersonalInfo(long staffId, long unitId) {
        Staff staff = staffGraphRepository.findById(staffId,2).orElseThrow(() -> new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_STAFF_IDANDUNITID_NOTFOUND, staffId, unitId)));
        List<SectorAndStaffExpertiseQueryResult> staffExpertiseQueryResults = copyCollectionPropertiesByMapper(staffExpertiseRelationShipGraphRepository.getSectorWiseExpertiseWithExperience(staff.getId()), SectorAndStaffExpertiseQueryResult.class);
        StaffDTO staffDTO = ObjectMapperUtils.copyPropertiesByMapper(staff, StaffDTO.class);
        staffDTO.setProfilePic((isNotNull(staff.getProfilePic())) ? envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath() + staff.getProfilePic() : staff.getProfilePic());
        staffDTO.setSectorWiseExpertise(copyCollectionPropertiesByMapper(getSectorWiseStaffAndExpertise(staffExpertiseQueryResults), SectorAndStaffExpertiseDTO.class));
        staffDTO.setTeams(copyCollectionPropertiesByMapper(teamGraphRepository.getTeamDetailsOfStaff(staff.getId(), unitId), TeamDTO.class));
        staffDTO.setLanguageId(staffGraphRepository.getLanguageId(staff.getId()));
        staffDTO.setUserName(staff.getUser().getUserName());
        staffDTO.setExpertiseIds(getExpertiseIds(staffExpertiseQueryResults));
        staffDTO.setCprNumber(staff.getUser().getCprNumber());
        staffDTO.setDateOfBirth(CPRUtil.getDateOfBirthFromCPR(staff.getUser().getCprNumber()));
        staffDTO.setGender(CPRUtil.getGenderFromCPRNumber(staff.getUser().getCprNumber()));
        return staffDTO;
    }

    public List<ExpertiseQueryResult> getExpertisesOfUnitByCountryId(Long countryId, long unitId) {
        List<Long> allUnitIds = organizationBaseRepository.fetchAllUnitIds(unitId);
        OrganizationServicesAndLevelQueryResult servicesAndLevel = organizationServiceRepository.getOrganizationServiceIdsByOrganizationId(allUnitIds);
        List<ExpertiseQueryResult> expertises = new ArrayList<>();
        if (ObjectUtils.isNotNull(servicesAndLevel)) {
            expertises = expertiseGraphRepository.findExpertiseByOrganizationServicesForUnit(countryId, servicesAndLevel.getServicesId());
            List<Long> allExpertiseIds = expertises.stream().map(ExpertiseQueryResult::getId).collect(Collectors.toList());
            List<ExpertiseLineQueryResult> expertiseLineQueryResults = expertiseGraphRepository.findAllExpertiseLines(allExpertiseIds);
            Map<Long, List<ExpertiseLineQueryResult>> expertiseLineQueryResultMap = expertiseLineQueryResults.stream().collect(Collectors.groupingBy(ExpertiseLineQueryResult::getExpertiseId));
            expertises.forEach(expertiseQueryResult -> {
                expertiseQueryResult.setExpertiseLines(expertiseLineQueryResultMap.get(expertiseQueryResult.getId()));
                ExpertiseLineQueryResult expertiseLine=expertiseQueryResult.getCurrentlyActiveLine();
                expertiseQueryResult.setSeniorityLevels(expertiseLine.getSeniorityLevels());
            });
        }
        return expertises;
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
        return copyCollectionPropertiesByMapper(staffs, StaffDTO.class);
    }

    public List<StaffResultDTO> getStaffIdsAndReasonCodeByUserId(Long userId) {
        List<StaffInformationQueryResult> staffUnitWrappers = staffGraphRepository.getStaffAndUnitTimezoneByUserIdAndReasonCode(userId, ReasonCodeType.ATTENDANCE);
        return copyCollectionPropertiesByMapper(staffUnitWrappers, StaffResultDTO.class);

    }

    public List<StaffResultDTO> getStaffIdsUnitByUserId(Long userId) {
        List<StaffInformationQueryResult> staffUnitWrappers = staffGraphRepository.getStaffIdsAndUnitByUserId(userId);
        return copyCollectionPropertiesByMapper(staffUnitWrappers, StaffResultDTO.class);

    }


    public List<StaffDTO> getStaffDetailByIds(Set<Long> staffIds) {
        List<StaffPersonalDetailQueryResult> staffPersonalDetailQueryResults = employmentGraphRepository.getStaffDetailByIds(staffIds, DateUtils.getCurrentLocalDate());
        return copyCollectionPropertiesByMapper(staffPersonalDetailQueryResults, StaffDTO.class);
    }

    public Long getStaffIdOfLoggedInUser(Long unitId) {
        Organization parentUnit = organizationService.fetchParentOrganization(unitId);
        return staffGraphRepository.findStaffIdByUserId(UserContext.getUserDetails().getId(), parentUnit.getId());
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
            Organization parentUnit = organizationService.fetchParentOrganization(unitId);
            staffAccessGroupQueryResult.setCountryId(UserContext.getUserDetails().getCountryId());
            staffId = staffGraphRepository.findHubStaffIdByUserId(UserContext.getUserDetails().getId(), parentUnit.getId());
        }
        UserAccessRoleDTO userAccessRoleDTO = accessGroupService.findUserAccessRole(unitId);
        staffAccessGroupQueryResult.setManagement(userAccessRoleDTO.getManagement());
        staffAccessGroupQueryResult.setCountryAdmin(isSuperAdmin || isNotNull(staffId));
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


    public Map<String, Object> getStaffWithFilter(Long unitId, long id, StaffFilterDTO staffFilterDTO, String moduleId) {
        List<AccessGroup> roles = null;
        Map<String, Object> map = new HashMap<>();
        map.put("staffList", staffFilterService.getAllStaffByUnitId(unitId, staffFilterDTO, moduleId, null, null,false,null).getStaffList());
        roles = accessGroupService.getAccessGroups(unitId);
        map.put("roles", roles);
        List<Map<String, Object>> teams = teamGraphRepository.getTeams(unitId);
        Organization organization=organizationService.fetchParentOrganization(unitId);
        map.put("loggedInStaffId",staffGraphRepository.findStaffIdByUserId(UserContext.getUserDetails().getId(), organization.getId()));
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
                        if (validDays.contains(loginDay.toString()) || validDays.contains(EVERYDAY.toString())) {
                            staffRole = accessGroupDayTypes.getAccessGroup().getRole().name();
                            if (AccessGroupRole.MANAGEMENT.name().equals(staffRole)) {
                                STAFF_CURRENT_ROLE = staffRole;
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

        List<StaffPersonalDetailQueryResult> staff = null;
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
            Unit unit = unitGraphRepository.getOrganizationByTeamId(id);
            roles = accessGroupService.getAccessGroups(unit.getId());
            countryId = countryGraphRepository.getCountryIdByUnitId(unit.getId());
        }

        Map<String, Object> map = new HashMap<>();
        map.put("staffList", staff);
        map.put("engineerTypes", engineerTypes);
        map.put("engineerList", engineerTypeGraphRepository.findEngineerTypeByCountry(countryId));
        map.put("roles", roles);
        return map;
    }

    // TODO NEED TO FIX map
    public List<StaffPersonalDetailQueryResult> getStaffWithBasicInfo(long unitId) {
        Unit unit = unitGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(unit).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_UNIT_ID_NOTFOUND, unitId);
        }
        return staffGraphRepository.getAllStaffHavingEmploymentByUnitIdMap(unitId, envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
    }

    public List<StaffPersonalDetailQueryResult> getStaffWithBasicInfo(long unitId, Boolean allStaffRequired) {
        List<StaffPersonalDetailQueryResult> staffPersonalDetailQueryResults;
        if (allStaffRequired) {
            Organization parentUnit = organizationService.fetchParentOrganization(unitId);
            staffPersonalDetailQueryResults = staffGraphRepository.getAllStaffByUnitId(parentUnit.getId(), envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
        } else {
            staffPersonalDetailQueryResults = staffGraphRepository.getAllStaffHavingEmploymentByUnitId(unitId, envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
        }
        return staffPersonalDetailQueryResults;
    }

    public List<StaffAdditionalInfoQueryResult> getStaffWithAdditionalInfo(long unitId) {
        Unit unit = unitGraphRepository.findOne(unitId, 0);
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
     * @param reasonCodeIds
     * @return
     */
    public StaffAdditionalInfoDTO getStaffEmploymentDataByEmploymentId(LocalDate startDate, Long employmentId, long organizationId, Set<Long> reasonCodeIds,LocalDate activityCutOffEndDate) {
        StaffAdditionalInfoQueryResult staffAdditionalInfoQueryResult = staffGraphRepository.getStaffInfoByUnitIdAndEmploymentId(employmentId);
        if (isNull(staffAdditionalInfoQueryResult)) {
            exceptionService.dataNotFoundByIdException(MESSAGE_STAFF_UNIT_PERMISSION_NOTFOUND);
        }
        return getStaffEmploymentData(startDate, staffAdditionalInfoQueryResult, employmentId, organizationId, reasonCodeIds,activityCutOffEndDate);
    }

    /**
     * @param startDate
     * @param staffId
     * @param employmentId
     * @param organizationId
     * @param reasonCodeIds
     * @return
     */
    public StaffAdditionalInfoDTO getStaffEmploymentDataByEmploymentIdAndStaffId(LocalDate startDate, long staffId, Long employmentId, long organizationId, Set<Long> reasonCodeIds) {
        StaffAdditionalInfoQueryResult staffAdditionalInfoQueryResult = staffGraphRepository.getStaffInfoByUnitIdAndStaffId(organizationId, staffId, employmentId, startDate.toString());
        if (isNull(staffAdditionalInfoQueryResult)) {
            exceptionService.dataNotFoundByIdException(MESSAGE_STAFF_UNIT_EMPLOYMENT_NOTFOUND);
        }
        User user=userGraphRepository.findOne(UserContext.getUserDetails().getId());
        staffAdditionalInfoQueryResult.setUnitWiseAccessRole(user.getUnitWiseAccessRole());
        return getStaffEmploymentData(startDate, staffAdditionalInfoQueryResult, employmentId, organizationId, reasonCodeIds,null);
    }

    /**
     * @param shiftDate
     * @param staffAdditionalInfoQueryResult
     * @param employmentId
     * @param organizationId
     * @param reasonCodeIds
     * @return
     */
    private StaffAdditionalInfoDTO getStaffEmploymentData(LocalDate shiftDate, StaffAdditionalInfoQueryResult staffAdditionalInfoQueryResult, Long employmentId, long organizationId, Set<Long> reasonCodeIds,LocalDate activityCutOffEndDate) {
        Unit unit = unitGraphRepository.findOne(organizationId);
        Long countryId = countryService.getCountryIdByUnitId(organizationId);
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = ObjectMapperUtils.copyPropertiesByMapper(staffAdditionalInfoQueryResult, StaffAdditionalInfoDTO.class);
        StaffEmploymentDetails employment = employmentService.getEmploymentDetails(employmentId);
        if (Optional.ofNullable(employment).isPresent()) {
            staffAdditionalInfoDTO.setStaffAge(CPRUtil.getAgeFromCPRNumber(staffAdditionalInfoDTO.getCprNumber()));
            setFunction(shiftDate, employmentId, staffAdditionalInfoDTO, employment);
            employment.setCountryId(countryId);
            employment.setUnitTimeZone(unit.getTimeZone());
            setReasonCode(reasonCodeIds, staffAdditionalInfoDTO);
            List<TimeSlotWrapper> timeSlotWrappers = timeSlotGraphRepository.getShiftPlanningTimeSlotsByUnitIds(Arrays.asList(unit.getId()), TimeSlotType.SHIFT_PLANNING);
            if (isCollectionEmpty(timeSlotWrappers)) {
                exceptionService.actionNotPermittedException("timeslot.not.found");
            }
            staffAdditionalInfoDTO.setTimeSlotSets(copyCollectionPropertiesByMapper(timeSlotWrappers, com.kairos.dto.user.country.time_slot.TimeSlotWrapper.class));
            List<DayTypeDTO> dayTypeDTOS = getDayTypeDTOS(countryId);
            staffAdditionalInfoDTO.setDayTypes(dayTypeDTOS);
            UserAccessRoleDTO userAccessRoleDTO = accessGroupService.findUserAccessRole(unit.getId());
            Organization parent = organizationService.fetchParentOrganization(unit.getId());
            Staff staffAtHub = staffGraphRepository.getStaffByOrganizationHub(parent.getId(), UserContext.getUserDetails().getId());
            if(staffAtHub!=null){
                staffAdditionalInfoDTO.setCountryAdmin(true);
            }
            SeniorAndChildCareDaysDTO seniorAndChildCareDaysDTO = expertiseService.getSeniorAndChildCareDays(employment.getExpertise().getId(),activityCutOffEndDate);
            staffAdditionalInfoDTO.setSeniorAndChildCareDays(seniorAndChildCareDaysDTO);
            staffAdditionalInfoDTO.setUserAccessRoleDTO(userAccessRoleDTO);
            staffAdditionalInfoDTO.setUnitId(unit.getId());
            staffAdditionalInfoDTO.setEmployment(employment);
            staffAdditionalInfoDTO.setStaffChildDetails(ObjectMapperUtils.copyCollectionPropertiesByMapper(staffAdditionalInfoQueryResult.getStaffChildDetails(), StaffChildDetailDTO.class));
        }
        return staffAdditionalInfoDTO;
    }

    private void setFunction(LocalDate shiftDate, Long employmentId, StaffAdditionalInfoDTO staffAdditionalInfoDTO, StaffEmploymentDetails employment) {
        if (Optional.ofNullable(shiftDate).isPresent()) {
            Long functionId = employmentFunctionRelationshipRepository.getApplicableFunction(employmentId, shiftDate.toString());
            employment.setFunctionId(functionId);
            staffAdditionalInfoDTO.setStaffAge(CPRUtil.getAgeByCPRNumberAndStartDate(staffAdditionalInfoDTO.getCprNumber(), shiftDate));
        }
    }

    private void setReasonCode(Set<Long> reasonCodeIds, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        if (CollectionUtils.isNotEmpty(reasonCodeIds)) {
            List<ReasonCode> reasonCodes = reasonCodeGraphRepository.findByIds(reasonCodeIds);
            staffAdditionalInfoDTO.setReasonCodes(copyCollectionPropertiesByMapper(reasonCodes, ReasonCodeDTO.class));
        }
    }

    private List<DayTypeDTO> getDayTypeDTOS(Long countryId) {
        List<Map<String, Object>> publicHolidaysResult = FormatUtil.formatNeoResponse(countryGraphRepository.getCountryAllHolidays(countryId));
        Map<Long, List<Map>> publicHolidayMap = publicHolidaysResult.stream().filter(d -> d.get(DAY_TYPE_ID) != null).collect(Collectors.groupingBy(k -> ((Long) k.get(DAY_TYPE_ID)), Collectors.toList()));
        List<DayType> dayTypes = dayTypeGraphRepository.findByCountryId(countryId);
        return dayTypes.stream().map(dayType ->
                new DayTypeDTO(dayType.getId(), dayType.getName(), dayType.getValidDays(), copyCollectionPropertiesByMapper(publicHolidayMap.get(dayType.getId()), CountryHolidayCalenderDTO.class), dayType.isHolidayType(), dayType.isAllowTimeSettings())
        ).collect(Collectors.toList());
    }

    public void setRequiredDataForShiftCreationInWrapper(StaffEmploymentUnitDataWrapper staffEmploymentUnitDataWrapper, Unit unit, Long countryId, Long expertiseId) {
        List<TimeSlotWrapper> timeSlotWrappers = timeSlotGraphRepository.getShiftPlanningTimeSlotsByUnitIds(Arrays.asList(unit.getId()), TimeSlotType.SHIFT_PLANNING);
        staffEmploymentUnitDataWrapper.setTimeSlotWrappers(copyCollectionPropertiesByMapper(timeSlotWrappers, com.kairos.dto.user.country.time_slot.TimeSlotWrapper.class));
        List<Map<String, Object>> publicHolidaysResult = FormatUtil.formatNeoResponse(countryGraphRepository.getCountryAllHolidays(countryId));
        Map<Long, List<Map>> publicHolidayMap = publicHolidaysResult.stream().filter(d -> d.get(DAY_TYPE_ID) != null).collect(Collectors.groupingBy(k -> ((Long) k.get(DAY_TYPE_ID)), Collectors.toList()));
        List<DayType> dayTypes = dayTypeGraphRepository.findByCountryId(countryId);
        List<DayTypeDTO> dayTypeDTOS = dayTypes.stream().map(dayType ->
                new DayTypeDTO(dayType.getId(), dayType.getName(), dayType.getValidDays(), copyCollectionPropertiesByMapper(publicHolidayMap.get(dayType.getId()), CountryHolidayCalenderDTO.class), dayType.isHolidayType(), dayType.isAllowTimeSettings())
        ).collect(Collectors.toList());
        staffEmploymentUnitDataWrapper.setDayTypes(dayTypeDTOS);
        staffEmploymentUnitDataWrapper.setDayTypes(copyCollectionPropertiesByMapper(dayTypes, DayTypeDTO.class));
        staffEmploymentUnitDataWrapper.setUnitTimeZone(unit.getTimeZone());
        UserAccessRoleDTO userAccessRoleDTO = accessGroupService.findUserAccessRole(unit.getId());
        SeniorAndChildCareDaysDTO seniorAndChildCareDaysDTO = expertiseService.getSeniorAndChildCareDays(expertiseId,getLocalDate());
        staffEmploymentUnitDataWrapper.setSeniorAndChildCareDays(seniorAndChildCareDaysDTO);
        staffEmploymentUnitDataWrapper.setUser(userAccessRoleDTO);
        staffEmploymentUnitDataWrapper.setUnitId(unit.getId());
    }

    public StaffAdditionalInfoDTO getStaffEmploymentData(Long employmentId, Long unitId) {
        StaffEmploymentDetails employmentDetails = employmentService.getEmploymentDetails(employmentId);
        if (!Optional.ofNullable(employmentDetails).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_EMPLOYMENT_ID_NOTEXIST, employmentId);
        }
        UserAccessRoleDTO userAccessRole = accessGroupService.findUserAccessRole(unitId);
        List<FunctionDTO> appliedFunctionDTOS = copyCollectionPropertiesByMapper(employmentDetails.getAppliedFunctions(), FunctionDTO.class);
        employmentDetails.setAppliedFunctions(appliedFunctionDTOS);
        List<ReasonCodeResponseDTO> reasonCodeQueryResults = reasonCodeGraphRepository.findReasonCodeByUnitId(unitId);
        List<ReasonCodeDTO> reasonCodeDTOS = copyCollectionPropertiesByMapper(reasonCodeQueryResults, ReasonCodeDTO.class);
        StaffAdditionalInfoDTO staffAdditionalInfoDTO = new StaffAdditionalInfoDTO(reasonCodeDTOS, employmentDetails);
        staffAdditionalInfoDTO.setUserAccessRoleDTO(userAccessRole);
        return staffAdditionalInfoDTO;
    }

    public List<StaffAdditionalInfoDTO> getStaffsEmploymentData(List<Long> staffIds, List<Long> employmentIds, long id, String type) {
        OrganizationBaseEntity organizationBaseEntity = organizationBaseRepository.findOne(id);
        Long countryId = countryService.getCountryIdByUnitId(id);
        List<TimeSlotWrapper> timeSlotWrappers = timeSlotGraphRepository.getShiftPlanningTimeSlotsByUnitIds(Arrays.asList(organizationBaseEntity.getId()), TimeSlotType.SHIFT_PLANNING);
        if (isCollectionEmpty(staffIds)) {
            Organization parentOrganization = organizationService.getParentOfOrganization(id);
            staffIds = staffGraphRepository.getAllStaffIdsByOrganisationId(isNotNull(parentOrganization) ? parentOrganization.getId() : id);
        }
        List<StaffAdditionalInfoQueryResult> staffAdditionalInfoQueryResult = staffGraphRepository.getStaffInfoByUnitIdAndStaffIds(organizationBaseEntity.getId(), staffIds, envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
        List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOS = copyCollectionPropertiesByMapper(staffAdditionalInfoQueryResult, StaffAdditionalInfoDTO.class);
        if (isCollectionEmpty(employmentIds)) {
            employmentIds = employmentGraphRepository.getEmploymentIdsByStaffIds(staffIds);
        }
        List<StaffEmploymentDetails> employmentDetails = employmentService.getEmploymentDetails(employmentIds, organizationBaseEntity, countryId);
        List<DayTypeDTO> dayTypeDTOS = getDayTypeDTOS(countryId);
        UserAccessRoleDTO userAccessRoleDTO = accessGroupService.findUserAccessRole(organizationBaseEntity.getId());
        setStaffAdditionalDetails(organizationBaseEntity, timeSlotWrappers, staffAdditionalInfoDTOS, dayTypeDTOS, userAccessRoleDTO);
        Map<Long, StaffAdditionalInfoDTO> staffAdditionalInfoDTOMap = staffAdditionalInfoDTOS.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
        List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOList = new ArrayList<>();
        for (StaffEmploymentDetails employmentDetail : employmentDetails) {
            StaffAdditionalInfoDTO staffAdditionalInfoDTO = ObjectMapperUtils.copyPropertiesByMapper(staffAdditionalInfoDTOMap.get(employmentDetail.getStaffId()), StaffAdditionalInfoDTO.class);
            if (isNotNull(staffAdditionalInfoDTO)) {
                staffAdditionalInfoDTO.setEmployment(employmentDetail);
                staffAdditionalInfoDTOList.add(staffAdditionalInfoDTO);
            }
        }
        return staffAdditionalInfoDTOList;
    }

    private void setStaffAdditionalDetails(OrganizationBaseEntity organizationBaseEntity, List<TimeSlotWrapper> timeSlotWrappers, List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOS, List<DayTypeDTO> dayTypeDTOS, UserAccessRoleDTO userAccessRoleDTO) {
        staffAdditionalInfoDTOS.forEach(staffAdditionalInfoDTO -> {
            staffAdditionalInfoDTO.setDayTypes(dayTypeDTOS);
            staffAdditionalInfoDTO.setUnitId(organizationBaseEntity.getId());
            staffAdditionalInfoDTO.setTimeSlotSets(copyCollectionPropertiesByMapper(timeSlotWrappers, com.kairos.dto.user.country.time_slot.TimeSlotWrapper.class));
            staffAdditionalInfoDTO.setUserAccessRoleDTO(userAccessRoleDTO);
        });
    }


    public List<StaffAdditionalInfoDTO> getStaffsAndEmploymentData(List<Long> staffIds, List<Long> employmentIds, long id) {
        OrganizationBaseEntity organizationBaseEntity = organizationBaseRepository.findOne(id);
        Long countryId = countryService.getCountryIdByUnitId(id);
        staffIds = getStaffIds(staffIds, id);
        List<StaffAdditionalInfoQueryResult> staffAdditionalInfoQueryResult = staffGraphRepository.getAllStaffInfoByUnitIdAndStaffIds(organizationBaseEntity.getId(), staffIds);
        List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOS = copyCollectionPropertiesByMapper(staffAdditionalInfoQueryResult, StaffAdditionalInfoDTO.class);
        if (isCollectionEmpty(employmentIds)) {
            employmentIds = employmentGraphRepository.getEmploymentIdsByStaffIds(staffIds);
        }
        List<StaffEmploymentDetails> employmentDetails = employmentService.getEmploymentDetails(employmentIds, organizationBaseEntity, countryId);
        staffAdditionalInfoDTOS.forEach(staffAdditionalInfoDTO -> {
            staffAdditionalInfoDTO.setStaffAge(CPRUtil.getAgeByCPRNumberAndStartDate(staffAdditionalInfoDTO.getCprNumber(), getLocalDate()));
            staffAdditionalInfoDTO.setUnitId(organizationBaseEntity.getId());
        });
        Map<Long, StaffAdditionalInfoDTO> staffAdditionalInfoDTOMap = staffAdditionalInfoDTOS.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
        List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOList = new ArrayList<>();
        for (StaffEmploymentDetails employmentDetail : employmentDetails) {
            StaffAdditionalInfoDTO staffAdditionalInfoDTO = ObjectMapperUtils.copyPropertiesByMapper(staffAdditionalInfoDTOMap.get(employmentDetail.getStaffId()), StaffAdditionalInfoDTO.class);
            if (isNotNull(staffAdditionalInfoDTO)) {
                staffAdditionalInfoDTO.setEmployment(employmentDetail);
                staffAdditionalInfoDTOList.add(staffAdditionalInfoDTO);
            }
        }
        return staffAdditionalInfoDTOList;
    }

    private List<Long> getStaffIds(List<Long> staffIds, long id) {
        if (isCollectionEmpty(staffIds)) {
            Organization parentOrganization = organizationService.getParentOfOrganization(id);
            staffIds = staffGraphRepository.getAllStaffIdsByOrganisationId(isNotNull(parentOrganization) ? parentOrganization.getId() : id);
        }
        return staffIds;
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
        List<Long> allUnitIds = organizationBaseRepository.fetchAllUnitIds(unitId);
        OrganizationServicesAndLevelQueryResult servicesAndLevel = organizationServiceRepository.getOrganizationServiceIdsByOrganizationId(allUnitIds);
        if (Optional.ofNullable(servicesAndLevel).isPresent()) {
            staffExperienceInExpertiseDTOList = staffExpertiseRelationShipGraphRepository.getExpertiseWithExperienceByStaffIdAndServices(staffId, servicesAndLevel.getServicesId());
        }
        return staffExperienceInExpertiseDTOList;
    }

    public StaffEmploymentTypeWrapper getStaffListAndLoginUserStaffIdByUnitId(Long unitId) {
        Organization organization = organizationService.fetchParentOrganization(unitId);
        Long loggedInStaffId = staffGraphRepository.findStaffIdByUserId(UserContext.getUserDetails().getId(), organization.getId());
        StaffEmploymentTypeWrapper staffEmploymentTypeWrapper = new StaffEmploymentTypeWrapper();
        staffEmploymentTypeWrapper.setLoggedInStaffId(loggedInStaffId);
        staffEmploymentTypeWrapper.setStaffList(staffGraphRepository.findAllStaffBasicDetailsByOrgIdAndUnitId(organization.getId(), unitId, envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath()));
        return staffEmploymentTypeWrapper;
    }

    public List<StaffPersonalDetailQueryResult> getStaffInfoById(long staffId, long unitId) {
        List<StaffPersonalDetailQueryResult> staffPersonalDetailList = staffGraphRepository.getStaffInfoById(unitId, staffId);
        if (!Optional.ofNullable(staffPersonalDetailList).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_STAFFANDUNIT_ID_NOTFOUND, staffId, unitId);
        }
        return staffPersonalDetailList;
    }

    public List<StaffPersonalDetailQueryResult> getAllStaffByUnitId(Long unitId, Boolean allStaffRequired) {
        List<StaffPersonalDetailQueryResult> staffPersonalDetailQueryResults;
        if (allStaffRequired) {
            Organization parentUnit = organizationService.fetchParentOrganization(unitId);
            // unit is parent so fetching all staff from itself
            staffPersonalDetailQueryResults = staffGraphRepository.getAllStaffByUnitId(parentUnit.getId(), envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
        } else {
            staffPersonalDetailQueryResults = staffGraphRepository.getAllStaffHavingEmploymentByUnitId(unitId, envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath());
        }
        return staffPersonalDetailQueryResults;
    }

    public Map<String, Object> getUnitManager(long unitId) {
        Organization organization = organizationService.fetchParentOrganization(unitId);
        List<Map<String, Object>> unitManagers;
        unitManagers = staffGraphRepository.getUnitManagers(organization.getId(), unitId);
        List<Map<String, Object>> unitManagerList = new ArrayList<>();
        for (Map<String, Object> unitManager : unitManagers) {
            unitManagerList.add((Map<String, Object>) unitManager.get("data"));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("unitManager", unitManagerList);
        map.put("accessGroups", accessGroupRepository.getAccessGroups(unitId));
        return map;
    }

    public ShiftPlanningProblemSubmitDTO getStaffsByIds(Long unitId, List<Long> staffIds){
        List<Employment> employments = employmentGraphRepository.getEmploymentByStaffIds(staffIds);
        Set<Long> expertiseIds = employments.stream().map(employment -> employment.getExpertise().getId()).collect(Collectors.toSet());
        ShiftPlanningProblemSubmitDTO shiftPlanningProblemSubmitDTO = activityIntegrationService.getNightWorkerDetails(staffIds,expertiseIds);
        Map<Long,StaffDTO> staffDTOMap = new HashMap<>();
        for (Employment employment : employments) {
            StaffDTO staffDTO = staffDTOMap.getOrDefault(employment.getStaff().getId(), getStaffDTO(employment));
            EmploymentDTO employmentDTO = ObjectMapperUtils.copyPropertiesByMapper(employment, EmploymentDTO.class);
            employmentDTO.setExpertiseNightWorkerSetting(shiftPlanningProblemSubmitDTO.getExpertiseNightWorkerSettingMap().get(employmentDTO.getExpertise().getId()));
            employmentDTO.setBreakSettings(shiftPlanningProblemSubmitDTO.getBreakSettingMap().get(employmentDTO.getExpertise().getId()));
            staffDTO.setNightWorker(shiftPlanningProblemSubmitDTO.getNightWorkerDetails().getOrDefault(staffDTO.getId(),false));
            staffDTO.getEmployments().add(employmentDTO);
            staffDTOMap.put(employment.getStaff().getId(), staffDTO);
        }
        List<DayTypeCountryHolidayCalenderQueryResult> dayTypes = dayTypeGraphRepository.getDayTypesWithCountryHolidayCalender();
        List<TimeSlotWrapper> timeSlotWrappers = timeSlotGraphRepository.getTimeSlots(unitId, TimeSlotMode.STANDARD);
        Collection<TimeSlotDTO> timelots = copyCollectionPropertiesByMapper(timeSlotWrappers, TimeSlotDTO.class);
        Map<String, TimeSlotDTO> timeSlotMap = timelots.stream().collect(Collectors.toMap(k->k.getName(), v->v));
        Collection<com.kairos.dto.user.country.day_type.DayType> dayTypeList = copyCollectionPropertiesByMapper(dayTypes, com.kairos.dto.user.country.day_type.DayType.class);
        Map<Long, com.kairos.dto.user.country.day_type.DayType> dayTypeMap = dayTypeList.stream().collect(Collectors.toMap(k->k.getId(), v->v));
        return ShiftPlanningProblemSubmitDTO.builder().staffs(new ArrayList(staffDTOMap.values())).expertiseNightWorkerSettingMap(shiftPlanningProblemSubmitDTO.getExpertiseNightWorkerSettingMap()).breakSettingMap(shiftPlanningProblemSubmitDTO.getBreakSettingMap()).dayTypeMap(dayTypeMap).timeSlotMap(timeSlotMap).build();
    }

    private StaffDTO getStaffDTO(Employment employment) {
        List<TagDTO>tagDTOS = copyCollectionPropertiesByMapper(employment.getStaff().getTags(), TagDTO.class);
        List<StaffChildDetailDTO> staffChildDetails = copyCollectionPropertiesByMapper(employment.getStaff().getStaffChildDetails(), StaffChildDetailDTO.class);
        return StaffDTO.builder()
                .id(employment.getStaff().getId())
                .firstName(employment.getStaff().getFirstName())
                .lastName(employment.getStaff().getLastName())
                .tags(tagDTOS)
                .cprNumber(employment.getStaff().getUser().getCprNumber())
                .staffChildDetails(staffChildDetails)
                .build();
    }

    public <T> List<StaffEmploymentWithTag> getAllStaffForUnitWithEmploymentStatus(final Long loggedInUserId, long unitId, StaffFilterDTO staffFilterDetails) {

        LOGGER.info("filters received are {} ", staffFilterDetails.getFiltersData());
        LocalDate dateToday = LocalDate.now();
        final Map<FilterType, Set<T>> filterTypeSetMap = staffFilterService.getMapOfFiltersToBeAppliedWithValue(staffFilterDetails.getModuleId(), staffFilterDetails.getFiltersData());

        if (Optional.ofNullable(filterTypeSetMap.get(FilterType.GROUPS)).isPresent() && filterTypeSetMap.get(FilterType.GROUPS).size() != 0) {
            updateFilterTypeCriteriaListByGroups(unitId, filterTypeSetMap);
        }

        List<StaffEmploymentWithTag> staffEmploymentWithTags = staffGraphRepositoryImpl.getStaffWithFilterCriteria(filterTypeSetMap, unitId, dateToday, staffFilterDetails.getSearchText(), loggedInUserId);
        int i = -1;
        StaffEmploymentWithTag matchedStaff = null;
        for (StaffEmploymentWithTag staffDetails : staffEmploymentWithTags) {
            i++;
            if (loggedInUserId.equals(staffDetails.getUserId())) {
                matchedStaff = staffDetails;
                break;
            }
        }

        if (matchedStaff != null && i != 0) {
            staffEmploymentWithTags.remove(i);
            staffEmploymentWithTags.add(0, matchedStaff);
        } else if (matchedStaff == null) {
            matchedStaff = staffGraphRepository.getLoggedInStaffDetails(unitId, loggedInUserId);
            if (matchedStaff != null) {
                staffEmploymentWithTags.add(0, matchedStaff);
            }
        }

        return staffEmploymentWithTags;
    }

    private <T>  Map<FilterType,T> updateFilterTypeCriteriaListByGroups(final Long unitId,final  Map<FilterType,T> filterTypeSetMap){
        Set<Long> groupIds = (Set<Long>) filterTypeSetMap.get(FilterType.GROUPS);
        Set<FilterSelection> filterSelections = groupService.getSelectedFilterGroupsOfUnit(unitId,groupIds,false);
        Set<Map<String,Number>> age =null ,organizationExperience=null,timeBankBalance=null,seniorityLevel=null,payGradeLevel=null ;
        Set<Map<String,String>> employedSince=null,birthday=null;

        for(FilterSelection filterSelection:filterSelections){
            if(filterSelection.getName().equals(FilterType.AGE)){
                if(age==null){
                    age = new HashSet<>();
                }
                age.add(dateCompareBuilder(filterSelection));
                filterTypeSetMap.put(filterSelection.getName(),(T) age);
            }
            else if(filterSelection.getName().equals(FilterType.EMPLOYED_SINCE)){
                if(employedSince==null){
                    employedSince = new HashSet<>();
                }
                employedSince.add(dateCompareBuilder(filterSelection));
                filterTypeSetMap.put(filterSelection.getName(),(T) employedSince);
            }
            else if(filterSelection.getName().equals(FilterType.ORGANIZATION_EXPERIENCE)){
                if(organizationExperience==null){
                    organizationExperience = new HashSet<>();
                }
                organizationExperience.add(dateCompareBuilder(filterSelection));
                filterTypeSetMap.put(filterSelection.getName(),(T) organizationExperience);
            }
            //fixme should be filtered in activity microservice
            /*else if(filterSelection.getName().equals(FilterType.TIME_BANK_BALANCE)){
                if(timeBankBalance==null){
                    timeBankBalance = new HashSet<>();
                }
                timeBankBalance.add(compareBuilder(filterSelection));
                filterTypeSetMap.put(filterSelection.getName(), (T) timeBankBalance);
            }*/
            else if(filterSelection.getName().equals(FilterType.SENIORITY)){
                if(seniorityLevel==null){
                    seniorityLevel = new HashSet<>();
                }
                seniorityLevel.add(compareBuilder(filterSelection));
                filterTypeSetMap.put(filterSelection.getName(), (T) seniorityLevel);
            }
            else if(filterSelection.getName().equals(FilterType.PAY_GRADE_LEVEL)){

                if(payGradeLevel==null){
                    payGradeLevel = new HashSet<>();
                }
                payGradeLevel.add(compareBuilder(filterSelection));
                filterTypeSetMap.put(filterSelection.getName(), (T) payGradeLevel);
            }
            else if(filterSelection.getName().equals(FilterType.BIRTHDAY)){
                if(birthday==null){
                    birthday = new HashSet<>();
                }
                birthday.add(dateCompareBuilder(filterSelection));
                filterTypeSetMap.put(filterSelection.getName(), (T) birthday);
            }
            else if(Optional.ofNullable(filterTypeSetMap.get(filterSelection.getName())).isPresent() ){
                ((Set<T>) filterTypeSetMap.get(filterSelection.getName())).add((T) filterSelection.getId());
            }
        }

        return filterTypeSetMap;
    }

    private  <T> Map<String,T> compareBuilder(final FilterSelection filterSelection){

        Map<String,T> customQueryMap = new HashMap<>();
        String valueWithoutNextLine = String.valueOf(filterSelection.getValue()).replace("\n"," ");
        JSONArray jsonArray = new JSONArray(valueWithoutNextLine);

        JSONObject comparisonData =jsonArray.getJSONObject(0);

        Long moreThan = 0L;
        if(!comparisonData.isNull("from")) {
            moreThan = Long.parseLong(comparisonData.getString("from"));
        }
        Long lessThan = 0L;
        LOGGER.info(" to data {}",comparisonData.isNull("to"));
        if(!comparisonData.isNull("to")){
            lessThan = Long.parseLong(comparisonData.getString("to"));
        }
        if(comparisonData.getString("type").equals("BETWEEN")){
            customQueryMap.put(">", (T) moreThan);
            if(lessThan!=0) {
                customQueryMap.put("<", (T) lessThan);
            }
        }else if( comparisonData.getString("type").equals("MORE_THAN")){
            customQueryMap.put(">", (T)  comparisonData.get("from"));
        }else if (comparisonData.getString("type").equals("LESS_THAN")){
            customQueryMap.put("<", (T) comparisonData.get("to"));
        }else if (comparisonData.getString("type").equals("EQUALS")){
            customQueryMap.put("=", (T) comparisonData.get("from"));
        }
        LOGGER.info(" custom query map prepared is {}",customQueryMap);
        return  customQueryMap;
    }

    private  <T> Map<String,T> dateCompareBuilder(final FilterSelection filterSelection){
        Map<String,T> customQueryMap = new HashMap<>();
        String valueWithoutNextLine = String.valueOf(filterSelection.getValue()).replace("\n"," ");
        JSONArray jsonArray = new JSONArray(valueWithoutNextLine);
        JSONObject comparisonData =jsonArray.getJSONObject(0);
        Long moreThanDays = 0L;
        if(!comparisonData.isNull("from")) {
            moreThanDays = staffFilterService.getDataInDays(Long.parseLong(comparisonData.getString("from")), filterSelection.getDurationType());
        }
        Long lessThanDays = 0L;
        if(!comparisonData.isNull("to")){
            lessThanDays = staffFilterService.getDataInDays(Long.parseLong(comparisonData.getString("to")),filterSelection.getDurationType());
        }
        LocalDate dateGreaterThan = getLocalDate().minusDays(moreThanDays);
        LocalDate dateLessThan = getLocalDate().plusDays(lessThanDays);
        if(comparisonData.getString("type").equals("BETWEEN")){
            customQueryMap.put(">",(T) ("DATE('"+dateGreaterThan+"')"));
            if(lessThanDays!=0) {
                customQueryMap.put("<",(T) comparisonData.get("from"));
            }
        }else if( comparisonData.getString("type").equals("MORE_THAN")){
            customQueryMap.put(">", (T) ("DATE('"+dateGreaterThan+"')"));
        }else if (comparisonData.getString("type").equals("LESS_THAN")){
            customQueryMap.put("<", (T)("DATE('"+ dateLessThan+"')"));
        }else if (comparisonData.getString("type").equals("EQUALS")){
            customQueryMap.put("=", (T) comparisonData.get("from"));
        }
        LOGGER.info(" custom query map prepared is {}",customQueryMap);
        return  customQueryMap;
    }
}
