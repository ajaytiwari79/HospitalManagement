package com.kairos.service.staff;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.ObjectUtils;
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
import com.kairos.dto.user.staff.StaffFilterDTO;
import com.kairos.dto.user.staff.StaffWithSkillDTO;
import com.kairos.dto.user.staff.employment.StaffEmploymentUnitDataWrapper;
import com.kairos.dto.user.staff.staff.StaffChildDetailDTO;
import com.kairos.dto.user.staff.staff.StaffResultDTO;
import com.kairos.dto.user.team.TeamDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.dto.user_context.UserContext;
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
import com.kairos.persistence.model.user.employment.query_result.EmploymentQueryResult;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import com.kairos.persistence.model.user.expertise.response.ExpertiseLineQueryResult;
import com.kairos.persistence.model.user.expertise.response.ExpertiseQueryResult;
import com.kairos.persistence.model.user.language.Language;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.repository.organization.OrganizationBaseRepository;
import com.kairos.persistence.repository.organization.OrganizationServiceRepository;
import com.kairos.persistence.repository.organization.TeamGraphRepository;
import com.kairos.persistence.repository.organization.UnitGraphRepository;
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
import com.kairos.service.country.CountryService;
import com.kairos.service.employment.EmploymentService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.expertise.ExpertiseService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.utils.CPRUtil;
import com.kairos.utils.FormatUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.getLocalDate;
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


    public StaffPersonalDetail getPersonalInfo(long staffId, long unitId) {
        Staff staff = staffGraphRepository.findById(staffId,2).orElseThrow(() -> new DataNotFoundByIdException(exceptionService.convertMessage(MESSAGE_STAFF_IDANDUNITID_NOTFOUND, staffId, unitId)));
        List<SectorAndStaffExpertiseQueryResult> staffExpertiseQueryResults = ObjectMapperUtils.copyPropertiesOfCollectionByMapper(staffExpertiseRelationShipGraphRepository.getSectorWiseExpertiseWithExperience(staff.getId()), SectorAndStaffExpertiseQueryResult.class);
        StaffPersonalDetail staffPersonalDetail = ObjectMapperUtils.copyPropertiesByMapper(staff,StaffPersonalDetail.class);
        staffPersonalDetail.setProfilePic((isNotNull(staff.getProfilePic())) ? envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath() + staff.getProfilePic() : staff.getProfilePic());
        staffPersonalDetail.setSectorWiseExpertise(ObjectMapperUtils.copyPropertiesOfCollectionByMapper(getSectorWiseStaffAndExpertise(staffExpertiseQueryResults), SectorAndStaffExpertiseDTO.class));
        staffPersonalDetail.setTeams(ObjectMapperUtils.copyPropertiesOfCollectionByMapper(teamGraphRepository.getTeamDetailsOfStaff(staff.getId(), unitId), TeamDTO.class));
        staffPersonalDetail.setLanguageId(staffGraphRepository.getLanguageId(staff.getId()));
        staffPersonalDetail.setUserName(staff.getUser().getUserName());
        staffPersonalDetail.setExpertiseIds(getExpertiseIds(staffExpertiseQueryResults));
        staffPersonalDetail.setCprNumber(staff.getUser().getCprNumber());
        staffPersonalDetail.setDateOfBirth(CPRUtil.getDateOfBirthFromCPR(staff.getUser().getCprNumber()));
        staffPersonalDetail.setGender(CPRUtil.getGenderFromCPRNumber(staff.getUser().getCprNumber()));
        return staffPersonalDetail;
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
            return expertises;
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

    public List<StaffPersonalDetail> getStaffByUnit(Long unitId) {
        Organization organization = organizationService.fetchParentOrganization(unitId);
        List<Staff> staffs = staffGraphRepository.getAllStaffByUnitId(organization.getId());
        return ObjectMapperUtils.copyPropertiesOfCollectionByMapper(staffs, StaffPersonalDetail.class);
    }

    public List<StaffResultDTO> getStaffIdsAndReasonCodeByUserId(Long userId) {
        List<StaffInformationQueryResult> staffUnitWrappers = staffGraphRepository.getStaffAndUnitTimezoneByUserIdAndReasonCode(userId, ReasonCodeType.ATTENDANCE);
        return ObjectMapperUtils.copyPropertiesOfCollectionByMapper(staffUnitWrappers, StaffResultDTO.class);

    }

    public List<StaffResultDTO> getStaffIdsUnitByUserId(Long userId) {
        List<StaffInformationQueryResult> staffUnitWrappers = staffGraphRepository.getStaffIdsAndUnitByUserId(userId);
        return ObjectMapperUtils.copyPropertiesOfCollectionByMapper(staffUnitWrappers, StaffResultDTO.class);

    }


    public List<StaffPersonalDetail> getStaffDetailByIds(Set<Long> staffIds) {
        List<StaffPersonalDetailQueryResult> staffPersonalDetailQueryResults = employmentGraphRepository.getStaffDetailByIds(staffIds, DateUtils.getCurrentLocalDate());
        return ObjectMapperUtils.copyPropertiesOfCollectionByMapper(staffPersonalDetailQueryResults,StaffPersonalDetail.class);
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

    /* @Modified by VIPUL
     * */

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
    public StaffAdditionalInfoDTO getStaffEmploymentDataByEmploymentId(LocalDate startDate, Long employmentId, long organizationId, Set<Long> reasonCodeIds) {
        StaffAdditionalInfoQueryResult staffAdditionalInfoQueryResult = staffGraphRepository.getStaffInfoByUnitIdAndEmploymentId(employmentId);
        if (isNull(staffAdditionalInfoQueryResult)) {
            exceptionService.dataNotFoundByIdException(MESSAGE_STAFF_UNIT_PERMISSION_NOTFOUND);
        }
        return getStaffEmploymentData(startDate, staffAdditionalInfoQueryResult, employmentId, organizationId, reasonCodeIds);
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
        return getStaffEmploymentData(startDate, staffAdditionalInfoQueryResult, employmentId, organizationId, reasonCodeIds);
    }

    /**
     * @param shiftDate
     * @param staffAdditionalInfoQueryResult
     * @param employmentId
     * @param organizationId
     * @param reasonCodeIds
     * @return
     */
    private StaffAdditionalInfoDTO getStaffEmploymentData(LocalDate shiftDate, StaffAdditionalInfoQueryResult staffAdditionalInfoQueryResult, Long employmentId, long organizationId, Set<Long> reasonCodeIds) {
        Unit unit = unitGraphRepository.findOne(organizationId);
        Long countryId = countryService.getCountryIdByUnitId(organizationId);
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
            employment.setUnitTimeZone(unit.getTimeZone());
            employment.setFunctionId(functionId);
            List<ReasonCode> reasonCodes;
            if (CollectionUtils.isNotEmpty(reasonCodeIds)) {
                reasonCodes = reasonCodeGraphRepository.findByIds(reasonCodeIds);
                staffAdditionalInfoDTO.setReasonCodes(ObjectMapperUtils.copyPropertiesOfCollectionByMapper(reasonCodes, ReasonCodeDTO.class));
            }
            List<TimeSlotWrapper> timeSlotWrappers = timeSlotGraphRepository.getShiftPlanningTimeSlotsByUnitIds(Arrays.asList(unit.getId()), TimeSlotType.SHIFT_PLANNING);
            if (isCollectionEmpty(timeSlotWrappers)) {
                exceptionService.actionNotPermittedException("timeslot.not.found");
            }
            staffAdditionalInfoDTO.setTimeSlotSets(ObjectMapperUtils.copyPropertiesOfCollectionByMapper(timeSlotWrappers, com.kairos.dto.user.country.time_slot.TimeSlotWrapper.class));
            List<DayTypeDTO> dayTypeDTOS = getDayTypeDTOS(countryId);
            staffAdditionalInfoDTO.setDayTypes(dayTypeDTOS);
            staffAdditionalInfoDTO.setUnitTimeZone(unit.getTimeZone());
            UserAccessRoleDTO userAccessRoleDTO = accessGroupService.findUserAccessRole(unit.getId());
            Organization parent = organizationService.fetchParentOrganization(unit.getId());
            Staff staffAtHub = staffGraphRepository.getStaffByOrganizationHub(parent.getId(), UserContext.getUserDetails().getId());
            if(staffAtHub!=null){
                staffAdditionalInfoDTO.setCountryAdmin(true);
            }
            SeniorAndChildCareDaysDTO seniorAndChildCareDaysDTO = expertiseService.getSeniorAndChildCareDays(employment.getExpertise().getId());
            staffAdditionalInfoDTO.setSeniorAndChildCareDays(seniorAndChildCareDaysDTO);
            staffAdditionalInfoDTO.setUserAccessRoleDTO(userAccessRoleDTO);
            staffAdditionalInfoDTO.setUnitId(unit.getId());
            staffAdditionalInfoDTO.setEmployment(employment);
            staffAdditionalInfoDTO.setStaffChildDetails(ObjectMapperUtils.copyPropertiesOfCollectionByMapper(staffAdditionalInfoQueryResult.getStaffChildDetails(), StaffChildDetailDTO.class));
        }
        return staffAdditionalInfoDTO;
    }

    private List<DayTypeDTO> getDayTypeDTOS(Long countryId) {
        List<Map<String, Object>> publicHolidaysResult = FormatUtil.formatNeoResponse(countryGraphRepository.getCountryAllHolidays(countryId));
        Map<Long, List<Map>> publicHolidayMap = publicHolidaysResult.stream().filter(d -> d.get(DAY_TYPE_ID) != null).collect(Collectors.groupingBy(k -> ((Long) k.get(DAY_TYPE_ID)), Collectors.toList()));
        List<DayType> dayTypes = dayTypeGraphRepository.findByCountryId(countryId);
        return dayTypes.stream().map(dayType ->
                new DayTypeDTO(dayType.getId(), dayType.getName(), dayType.getValidDays(), ObjectMapperUtils.copyPropertiesOfCollectionByMapper(publicHolidayMap.get(dayType.getId()), CountryHolidayCalenderDTO.class), dayType.isHolidayType(), dayType.isAllowTimeSettings())
        ).collect(Collectors.toList());
    }

    public void setRequiredDataForShiftCreationInWrapper(StaffEmploymentUnitDataWrapper staffEmploymentUnitDataWrapper, Unit unit, Long countryId, Long expertiseId) {
        List<TimeSlotWrapper> timeSlotWrappers = timeSlotGraphRepository.getShiftPlanningTimeSlotsByUnitIds(Arrays.asList(unit.getId()), TimeSlotType.SHIFT_PLANNING);
        staffEmploymentUnitDataWrapper.setTimeSlotWrappers(ObjectMapperUtils.copyPropertiesOfCollectionByMapper(timeSlotWrappers, com.kairos.dto.user.country.time_slot.TimeSlotWrapper.class));
        List<Map<String, Object>> publicHolidaysResult = FormatUtil.formatNeoResponse(countryGraphRepository.getCountryAllHolidays(countryId));
        Map<Long, List<Map>> publicHolidayMap = publicHolidaysResult.stream().filter(d -> d.get(DAY_TYPE_ID) != null).collect(Collectors.groupingBy(k -> ((Long) k.get(DAY_TYPE_ID)), Collectors.toList()));
        List<DayType> dayTypes = dayTypeGraphRepository.findByCountryId(countryId);
        List<DayTypeDTO> dayTypeDTOS = dayTypes.stream().map(dayType ->
                new DayTypeDTO(dayType.getId(), dayType.getName(), dayType.getValidDays(), ObjectMapperUtils.copyPropertiesOfCollectionByMapper(publicHolidayMap.get(dayType.getId()), CountryHolidayCalenderDTO.class), dayType.isHolidayType(), dayType.isAllowTimeSettings())
        ).collect(Collectors.toList());
        staffEmploymentUnitDataWrapper.setDayTypes(dayTypeDTOS);
        staffEmploymentUnitDataWrapper.setDayTypes(ObjectMapperUtils.copyPropertiesOfCollectionByMapper(dayTypes, DayTypeDTO.class));
        staffEmploymentUnitDataWrapper.setUnitTimeZone(unit.getTimeZone());
        UserAccessRoleDTO userAccessRoleDTO = accessGroupService.findUserAccessRole(unit.getId());
        SeniorAndChildCareDaysDTO seniorAndChildCareDaysDTO = expertiseService.getSeniorAndChildCareDays(expertiseId);
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
        List<FunctionDTO> appliedFunctionDTOS = ObjectMapperUtils.copyPropertiesOfCollectionByMapper(employmentDetails.getAppliedFunctions(), FunctionDTO.class);
        employmentDetails.setAppliedFunctions(appliedFunctionDTOS);
        List<ReasonCodeResponseDTO> reasonCodeQueryResults = reasonCodeGraphRepository.findReasonCodeByUnitId(unitId);
        List<ReasonCodeDTO> reasonCodeDTOS = ObjectMapperUtils.copyPropertiesOfCollectionByMapper(reasonCodeQueryResults, ReasonCodeDTO.class);
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
        List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOS = ObjectMapperUtils.copyPropertiesOfCollectionByMapper(staffAdditionalInfoQueryResult, StaffAdditionalInfoDTO.class);
        if (isCollectionEmpty(employmentIds)) {
            employmentIds = employmentGraphRepository.getEmploymentIdsByStaffIds(staffIds);
        }
        List<StaffEmploymentDetails> employmentDetails = employmentService.getEmploymentDetails(employmentIds, organizationBaseEntity, countryId);
        List<DayTypeDTO> dayTypeDTOS = getDayTypeDTOS(countryId);
        UserAccessRoleDTO userAccessRoleDTO = accessGroupService.findUserAccessRole(organizationBaseEntity.getId());
        // TODO incorrect we dont need to set in all staff
        staffAdditionalInfoDTOS.forEach(staffAdditionalInfoDTO -> {
            staffAdditionalInfoDTO.setDayTypes(dayTypeDTOS);
            staffAdditionalInfoDTO.setUnitId(organizationBaseEntity.getId());
            staffAdditionalInfoDTO.setTimeSlotSets(ObjectMapperUtils.copyPropertiesOfCollectionByMapper(timeSlotWrappers, com.kairos.dto.user.country.time_slot.TimeSlotWrapper.class));
            staffAdditionalInfoDTO.setUserAccessRoleDTO(userAccessRoleDTO);
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


    public List<StaffAdditionalInfoDTO> getStaffsAndEmploymentData(List<Long> staffIds, List<Long> employmentIds, long id) {
        OrganizationBaseEntity organizationBaseEntity = organizationBaseRepository.findOne(id);
        Long countryId = countryService.getCountryIdByUnitId(id);
        if (isCollectionEmpty(staffIds)) {
            Organization parentOrganization = organizationService.getParentOfOrganization(id);
            staffIds = staffGraphRepository.getAllStaffIdsByOrganisationId(isNotNull(parentOrganization) ? parentOrganization.getId() : id);
        }
        List<StaffAdditionalInfoQueryResult> staffAdditionalInfoQueryResult = staffGraphRepository.getAllStaffInfoByUnitIdAndStaffIds(organizationBaseEntity.getId(), staffIds);
        List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOS = ObjectMapperUtils.copyPropertiesOfCollectionByMapper(staffAdditionalInfoQueryResult, StaffAdditionalInfoDTO.class);
        if (isCollectionEmpty(employmentIds)) {
            employmentIds = employmentGraphRepository.getEmploymentIdsByStaffIds(staffIds);
        }
        List<StaffEmploymentDetails> employmentDetails = employmentService.getEmploymentDetails(employmentIds, organizationBaseEntity, countryId);
        // TODO incorrect we dont need to set in all staff
        staffAdditionalInfoDTOS.forEach(staffAdditionalInfoDTO -> {
            staffAdditionalInfoDTO.setStaffAge(CPRUtil.getAgeByCPRNumberAndStartDate(staffAdditionalInfoDTO.getCprNumber(), getLocalDate()));
            staffAdditionalInfoDTO.setUnitId(organizationBaseEntity.getId());
        });
        Map<Long, StaffAdditionalInfoDTO> staffAdditionalInfoDTOMap = staffAdditionalInfoDTOS.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
        Map<Long,SeniorAndChildCareDaysDTO> expertiseIdsAndSeniorAndChildCareDaysMap=expertiseService.getSeniorAndChildCareDaysMapByExpertiseIds(employmentDetails.stream().map(staffEmploymentDetails -> staffEmploymentDetails.getExpertise().getId()).collect(Collectors.toList()));
        List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOList = new ArrayList<>();
        for (StaffEmploymentDetails employmentDetail : employmentDetails) {
            StaffAdditionalInfoDTO staffAdditionalInfoDTO = ObjectMapperUtils.copyPropertiesByMapper(staffAdditionalInfoDTOMap.get(employmentDetail.getStaffId()), StaffAdditionalInfoDTO.class);
            if (isNotNull(staffAdditionalInfoDTO)) {
                if(expertiseIdsAndSeniorAndChildCareDaysMap.containsKey(employmentDetail.getExpertise().getId())) {
                    staffAdditionalInfoDTO.setSeniorAndChildCareDays(expertiseIdsAndSeniorAndChildCareDaysMap.get(employmentDetail.getExpertise().getId()));
                }
                staffAdditionalInfoDTO.setEmployment(employmentDetail);
                staffAdditionalInfoDTOList.add(staffAdditionalInfoDTO);
            }
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
        List<Long> allUnitIds = organizationBaseRepository.fetchAllUnitIds(unitId);
        OrganizationServicesAndLevelQueryResult servicesAndLevel = organizationServiceRepository.getOrganizationServiceIdsByOrganizationId(allUnitIds);
        if (Optional.ofNullable(servicesAndLevel).isPresent()) {
            staffExperienceInExpertiseDTOList = staffExpertiseRelationShipGraphRepository.getExpertiseWithExperienceByStaffIdAndServices(staffId, servicesAndLevel.getServicesId());
        }
        return staffExperienceInExpertiseDTOList;
    }
}
