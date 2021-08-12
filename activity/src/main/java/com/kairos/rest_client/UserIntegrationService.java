package com.kairos.rest_client;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.RestClientUrlUtil;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.activity_tabs.OrganizationMappingDTO;
import com.kairos.dto.activity.counter.configuration.CounterDTO;
import com.kairos.dto.activity.counter.distribution.access_group.AccessGroupPermissionCounterDTO;
import com.kairos.dto.activity.counter.distribution.access_group.StaffIdsDTO;
import com.kairos.dto.activity.counter.distribution.org_type.OrgTypeDTO;
import com.kairos.dto.activity.counter.distribution.tab.TabKPIMappingDTO;
import com.kairos.dto.activity.counter.enums.ModuleType;
import com.kairos.dto.activity.cta.CTABasicDetailsDTO;
import com.kairos.dto.activity.cta.EmploymentDTO;
import com.kairos.dto.activity.kpi.DefaultKpiDataDTO;
import com.kairos.dto.activity.kpi.StaffEmploymentTypeDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.open_shift.PriorityGroupDefaultData;
import com.kairos.dto.activity.open_shift.priority_group.StaffIncludeFilterDTO;
import com.kairos.dto.activity.shift.*;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.dto.activity.wta.basic_details.WTABasicDetailsDTO;
import com.kairos.dto.kpermissions.ActionDTO;
import com.kairos.dto.kpermissions.FieldPermissionUserData;
import com.kairos.dto.scheduler.scheduler_panel.SchedulerPanelDTO;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.access_page.KPIAccessPageDTO;
import com.kairos.dto.user.access_permission.StaffAccessGroupDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO;
import com.kairos.dto.user.country.basic_details.CountryDTO;
import com.kairos.dto.user.country.day_type.DayTypeEmploymentTypeWrapper;
import com.kairos.dto.user.country.experties.ExpertiseDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotWrapper;
import com.kairos.dto.user.filter.FilteredStaffsAndRequiredDataFilterDTO;
import com.kairos.dto.user.filter.RequiredDataForFilterDTO;
import com.kairos.dto.user.organization.*;
import com.kairos.dto.user.organization.skill.Skill;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.dto.user.reason_code.ReasonCodeWrapper;
import com.kairos.dto.user.staff.ClientStaffInfoDTO;
import com.kairos.dto.user.staff.employment.StaffEmploymentQueryResult;
import com.kairos.dto.user.staff.employment.StaffEmploymentUnitDataWrapper;
import com.kairos.dto.user.staff.staff.StaffResultDTO;
import com.kairos.dto.user.staff.staff.UnitStaffResponseDTO;
import com.kairos.dto.user.team.TeamDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.dto.user_context.CurrentUserDetails;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.FilterType;
import com.kairos.enums.MasterDataTypeEnum;
import com.kairos.enums.kpermissions.FieldLevelPermission;
import com.kairos.enums.kpermissions.PermissionAction;
import com.kairos.enums.rest_client.MicroService;
import com.kairos.enums.rest_client.RestClientUrlType;
import com.kairos.persistence.model.staff.personal_details.StaffDTO;
import com.kairos.persistence.model.tag.Tag;
import com.kairos.service.exception.ExceptionService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.newArrayList;
import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_EMPLOYMENT_NOTFOUND;
import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_STAFF_NOT_FOUND_BY_USER;
import static com.kairos.constants.ApiConstants.*;

//import com.kairos.persistence.model.client_exception.ClientExceptionDTO;


@Service
@Transactional
public class UserIntegrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserIntegrationService.class);
    public static final String START_DATE = "startDate";
    @Inject
    private GenericRestClient genericRestClient;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private UserRestClientForScheduler userRestClientForScheduler;

    public Long getEmploymentId(Long unitId, Long staffId, Long expertiseId) {
        Long value = genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, STAFF_ID_EXPERTISE_ID_UNIT_EMPLOYMENT_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Long>>() {
        }, staffId, expertiseId);
        if (value == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_EMPLOYMENT_NOTFOUND, expertiseId);
        }
        return value;
    }

    public PriorityGroupDefaultData getExpertiseAndEmployment(Long countryId) {
        return genericRestClient.publishRequest(null, countryId, RestClientUrlType.COUNTRY, HttpMethod.GET, EMPLOYEMENT_TYPE_AND_EXPERTISE, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<PriorityGroupDefaultData>>() {
        });
    }

    public PriorityGroupDefaultData getExpertiseAndEmploymentForUnit(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, EMPLOYEMENT_TYPE_AND_EXPERTISE, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<PriorityGroupDefaultData>>() {
        });
    }

    public List<String> getEmailsOfStaffByStaffIds(Long unitId, List<Long> staffIds) {
        return genericRestClient.publishRequest(staffIds, unitId, RestClientUrlType.UNIT, HttpMethod.POST, STAFF_EMAILS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<String>>>() {
        });
    }

    public List<StaffEmploymentDetails> getStaffIdAndEmployment(Long unitId, List<Long> staffIds, Long expertiseId) {

        return genericRestClient.publishRequest(staffIds, unitId, RestClientUrlType.UNIT, HttpMethod.POST, STAFF_AND_EMPLOYMENTS_BY_EXPERTISE_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffEmploymentDetails>>>() {
        }, expertiseId);

    }

    public List<StaffEmploymentDetails> getStaffsMainEmployment() {

        return genericRestClient.publishRequest(null, null, RestClientUrlType.ORGANIZATION, HttpMethod.GET, STAFF_AND_MAIN_EMPLOYMENTS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffEmploymentDetails>>>() {
        });

    }


    public UserAccessRoleDTO getAccessRolesOfStaff(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, STAFF_ACCESS_ROLES, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<UserAccessRoleDTO>>() {
        });
    }

    public DayTypeEmploymentTypeWrapper getDayTypesAndEmploymentTypes(Long countryId) {
        return genericRestClient.publishRequest(null, countryId, RestClientUrlType.COUNTRY, HttpMethod.GET, DAY_TYPES_AND_EMPLOYEMENT_TYPES, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<DayTypeEmploymentTypeWrapper>>() {
        });
    }

    public DayTypeEmploymentTypeWrapper getDayTypesAndEmploymentTypesAtUnit(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, DAY_TYPES_AND_EMPLOYEMENT_TYPES, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<DayTypeEmploymentTypeWrapper>>() {
        });
    }

    public List<StaffResultDTO> getStaffIdsByUserId(Long userId) {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.ORGANIZATION, HttpMethod.GET, USER_USERID_STAFFS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffResultDTO>>>() {
        }, userId);
    }


    public List<UnitAndParentOrganizationAndCountryDTO> getParentOrganizationAndCountryOfUnits() {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.ORGANIZATION, HttpMethod.GET, UNIT_PARENT_ORGANIZATION_AND_COUNTRY, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<UnitAndParentOrganizationAndCountryDTO>>>() {
        });
    }

    public List<StaffDTO> getStaffDetailByIds(Long unitId, Set<Long> staffIds) {
        return genericRestClient.publishRequest(staffIds, unitId, RestClientUrlType.UNIT, HttpMethod.POST, STAFF_DETAILS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffDTO>>>() {
        });
    }

    public Long getStaffIdByUserId(Long unitId) {
        Long value = genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, USER_STAFF_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Long>>() {
        });

        if (value == null) exceptionService.dataNotFoundByIdException(MESSAGE_STAFF_NOT_FOUND_BY_USER);
        return value;
    }

    public Long removeFunctionFromEmploymentByDate(Long unitId, Long employmentId, Date shiftDate) {
        BasicNameValuePair appliedDate = new BasicNameValuePair("appliedDate", DateUtils.asLocalDate(shiftDate).toString());
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.DELETE, REMOVE_FUNCTION_FROM_EMPLOYMENT_ON_DELETE_SHIFT, Collections.singletonList(appliedDate), new ParameterizedTypeReference<RestTemplateResponseEnvelope<Long>>() {
        }, employmentId);
    }

    public Boolean applyFunction(Long unitId, Long employmentId, Map<LocalDate, Long> requestBody,HttpMethod httpMethod,List<NameValuePair>  queryParams) {
        return genericRestClient.publishRequest(requestBody, unitId, RestClientUrlType.UNIT, httpMethod, APPLY_FUNCTION, queryParams, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        }, employmentId);
    }

    //~ Added by mohit TODO remove comment after verification

    public OrganizationDTO getOrganizationDTO(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, "", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationDTO>>() {
        });
    }

    public EmploymentDTO getEmploymentDTO(Long unitId, Long unitEmploymentPositionId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, GET_EMPLOYMENT, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<EmploymentDTO>>() {
        }, unitEmploymentPositionId);
    }

    public StaffAccessGroupDTO getStaffAccessGroupDTO(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, STAFF_ACCESS_GROUPS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffAccessGroupDTO>>() {
        });
    }

    public List<ReasonCodeDTO> getReasonCodeDTOList(Long unitId, List<org.apache.http.NameValuePair> requestParam) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, GET_REASONCODE, requestParam, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<ReasonCodeDTO>>>() {
        });
    }

    public CTABasicDetailsDTO getCtaBasicDetailsDTO(Long countryId, List<NameValuePair> requestParam) {
        return genericRestClient.publishRequest(null, countryId, RestClientUrlType.COUNTRY, HttpMethod.GET, CTA_BASIC_INFO, requestParam, new ParameterizedTypeReference<RestTemplateResponseEnvelope<CTABasicDetailsDTO>>() {
        });
    }

    public Long getCountryId(Long refId) {
        return genericRestClient.publishRequest(null, refId, RestClientUrlType.UNIT, HttpMethod.GET, COUNTRY_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Long>>() {
        });
    }

    public Map<Long, Long> getAccessGroupForUnit(Long unitId, Set<Long> parentAccessGroupIds) {
        return genericRestClient.publishRequest(parentAccessGroupIds, unitId, RestClientUrlType.UNIT, HttpMethod.POST, ACCESS_GROUPS_BY_PARENT, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<Long, Long>>>() {
        });

    }

    // PriortyGroupRestClient
    public List<StaffEmploymentQueryResult> getStaffIdsByPriorityGroupIncludeFilter(StaffIncludeFilterDTO staffIncludeFilterDTO, Long unitId) {
        return genericRestClient.publishRequest(staffIncludeFilterDTO, unitId, RestClientUrlType.UNIT, HttpMethod.POST, STAFF_PRIORTY_GROUP, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffEmploymentQueryResult>>>() {
        });
    }

    public CountryDTO getCountryById(Long countryId) {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.ORGANIZATION, HttpMethod.GET, COUNTRY_COUNTRY_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<CountryDTO>>() {
        }, countryId);
    }

    public boolean isCountryExists(Long countryId) {
        return genericRestClient.publishRequest(null, countryId, RestClientUrlType.COUNTRY, HttpMethod.GET, "", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<CountryDTO>>() {
        }) != null;
    }

    public List<UnitStaffResponseDTO> getUnitWiseStaffList() {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, STAFF_UNIT_WISE, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<UnitStaffResponseDTO>>>() {
        });
    }

    //Previously this API didn't match with any API on user micro-service (corrected)
    public Map<Long, Long> getEmploymentExpertiseMap(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, EMPLOYMENT_EXPERTISE, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<Long, Long>>>() {
        });
    }

    public ClientStaffInfoDTO getStaffInfo() {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, STAFF_GET_STAFF_INFO, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<ClientStaffInfoDTO>>() {
        });
    }

    public List<StaffDTO> getStaffListByUnit() {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, STAFF_GET_STAFF_BY_UNIT, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffDTO>>>() {
        });
    }
    //Previously this API didn't match with any API on user micro-service (corrected)
    public List<Long> getUnitManagerIds(Long unitId) {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.ORGANIZATION, HttpMethod.GET, UNIT_MANAGER_IDS_UNIT_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Long>>>() {
        }, unitId);
    }

    public List<Long> getCountryAdminsIds(Long countryAdminsOfUnitId) {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.ORGANIZATION, HttpMethod.GET, COUNTRY_ADMINS_IDS_OF_UNIT, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Long>>>() {
        }, countryAdminsOfUnitId);
    }

    public StaffEmploymentDetails mainUnitEmploymentOfStaff(Long staffId, Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, MAIN_UNIT_EMPLOYEMNT_BY_STAFF_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffEmploymentDetails>>() {
        }, staffId);
    }


    public StaffAdditionalInfoDTO verifyUnitEmploymentOfStaff(LocalDate shiftDate, Long staffId, Long unitEmploymentId) {
        List<NameValuePair> queryParamList = new ArrayList<>();
        queryParamList.add(new BasicNameValuePair("shiftDate", shiftDate != null ? shiftDate.toString() : DateUtils.getCurrentLocalDate().toString()));
        StaffAdditionalInfoDTO staffAdditionalInfoDTO= genericRestClient.publishRequest(null, UserContext.getUserDetails().getLastSelectedOrganizationId(), RestClientUrlType.UNIT, HttpMethod.GET, VERIFY_UNIT_EMPLOYEMNT_BY_STAFF_ID_UNIT_EMPLOYEMENT_ID, queryParamList, new ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffAdditionalInfoDTO>>() {
        }, staffId, unitEmploymentId);
        UserContext.getUserDetails().setUnitWiseAccessRole(staffAdditionalInfoDTO.getUnitWiseAccessRole());
        return staffAdditionalInfoDTO;
    }


    public StaffAdditionalInfoDTO verifyEmploymentAndFindFunctionsAfterDate(Long staffId, Long employmentId) {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, GET_FUNCTIONS_OF_EMPLOYMENT, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffAdditionalInfoDTO>>() {
        }, staffId, employmentId);
    }

    public Map<LocalDate,List<FunctionDTO>> getFunctionsOfEmployment(Long employmentId, LocalDate startDate, LocalDate endDate) {
        List<NameValuePair> queryParamList = new ArrayList<>();
        queryParamList.add(new BasicNameValuePair(START_DATE, startDate.toString()));
        queryParamList.add(new BasicNameValuePair("endDate", endDate!=null? endDate.toString():null));
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, FUNCTIONS_OF_EMPLOYMENT, queryParamList, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<LocalDate,List<FunctionDTO>>>>() {
        }, employmentId);
    }

    public List<StaffDTO> getStaffInfo(Long unitId, Set<Long> expertiseIds) {
        return genericRestClient.publishRequest(expertiseIds, unitId, RestClientUrlType.UNIT, HttpMethod.POST, STAFF_GET_STAFF_BY_EXPERTISES, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffDTO>>>() {
        });
    }

    //shift service
    public List<StaffAdditionalInfoDTO> getStaffAditionalDTOS(Long unitId, List<NameValuePair> requestParam) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, "/staff/verifyUnitEmployments", requestParam, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffAdditionalInfoDTO>>>() {
        });
    }

    public List<StaffAdditionalInfoDTO> getStaffAndEmploymentDetails(Long unitId, List<NameValuePair> requestParam) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, "/staff/staffs_employments_details", requestParam, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffAdditionalInfoDTO>>>() {
        });
    }


    public ReasonCodeWrapper getUnitInfoAndReasonCodes(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT_WITHOUT_PARENT_ORG, HttpMethod.GET, UNIT_LOCATION, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<ReasonCodeWrapper>>() {
        });
    }

    //TimeBankRestClient
    public EmploymentWithCtaDetailsDTO getEmploymentDetails(Long employmentId) {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, GET_CTA_BY_EMPLOYMENT_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<EmploymentWithCtaDetailsDTO>>() {
        }, employmentId);
    }

    //WTADetailRestClient
    public WTABasicDetailsDTO getWtaRelatedInfo(Long expertiseId, Long organizationSubTypeId, Long countryId, Long organizationId, Long organizationTypeId, List<Long> unitIds) {
        List<NameValuePair> queryParamList = new ArrayList<>();
        queryParamList.add(new BasicNameValuePair("countryId", countryId.toString()));
        if (isNotNull(organizationId)) {
            queryParamList.add(new BasicNameValuePair("organizationId", organizationId.toString()));
        }
        queryParamList.add(new BasicNameValuePair("organizationTypeId", organizationTypeId.toString()));
        queryParamList.add(new BasicNameValuePair("organizationSubTypeId", organizationSubTypeId.toString()));
        queryParamList.add(new BasicNameValuePair("expertiseId", expertiseId.toString()));
        if (CollectionUtils.isNotEmpty(unitIds)) {
            queryParamList.add(new BasicNameValuePair("unitIds", unitIds.toString().replace("[", "").replace("]", "")));
        }
        return genericRestClient.publishRequest(null, null, RestClientUrlType.ORGANIZATION, HttpMethod.GET, WTA_RULE_INFO, queryParamList, new ParameterizedTypeReference<RestTemplateResponseEnvelope<WTABasicDetailsDTO>>() {
        });
    }


    //OrganizationRestClient
    public OrganizationDTO getOrganization() {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, "", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationDTO>>() {
        });
    }

    public OrganizationDTO getOrganizationWithCountryId(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, GET_ORGANIZATION_WITH_COUNTRY_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationDTO>>() {
        });
    }

    public OrganizationDTO getParentOfOrganization(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, GET_PARENT_OF_ORGANIZATION, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationDTO>>() {
        });
    }



    //TODO write implementation previously inside OrganizationRestClient this implementation was written only(here added so as not to break current functionality )
    public boolean isExistOrganization(Long orgId) {
        return getOrganizationDTO(orgId)!=null;
    }




    public boolean verifyOrganizationExpertizeAndRegions(OrganizationMappingDTO organizationMappingDTO) {
        return genericRestClient.publishRequest(organizationMappingDTO, null, RestClientUrlType.ORGANIZATION, HttpMethod.POST, VERIFY_ORGANIZATION_EXPERTISE, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        });
    }

    public OrganizationTypeAndSubTypeDTO getOrganizationTypeAndSubTypeByUnitId(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, ORGANIZATION_TYPE_AND_SUB_TYPES, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationTypeAndSubTypeDTO>>() {
        });
    }

    public OrganizationSkillAndOrganizationTypesDTO getOrganizationSkillOrganizationSubTypeByUnitId(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, SKILL_ORG_TYPES, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationSkillAndOrganizationTypesDTO>>() {
        });
    }

    public boolean showCountryTagForOrganization(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, SHOW_COUNTRY_TAGS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        });
    }

    public Long getCountryIdOfOrganization(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, COUNTRY_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Long>>() {
        });
    }

    public String getTimezone() {
        return userRestClientForScheduler.publishRequest(null, 2567L, RestClientUrlType.UNIT, HttpMethod.GET, MicroService.USER, "/time_zone", null, new ParameterizedTypeReference<com.kairos.commons.client.RestTemplateResponseEnvelope<String>>() {
        });
    }


    public StaffDTO getStaff(Long unitId, Long staffId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, STAFF_WITH_STAFF_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffDTO>>() {}, staffId);
    }

    public Expertise getExpertise(Long countryId, Long expertiseId) {
        return genericRestClient.publishRequest(null, countryId, RestClientUrlType.COUNTRY_WITHOUT_PARENT_ORG, HttpMethod.GET, API_EXPERTISE_URL, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Expertise>>() {
        }, expertiseId);
    }

    public void restoreFunctionsWithDatesByEmploymentIds(Map<Long, Map<LocalDate, Long>> employmentIdWithShiftDateFunctionIdMap, Long unitId) {
        genericRestClient.publishRequest(employmentIdWithShiftDateFunctionIdMap, unitId, RestClientUrlType.UNIT, HttpMethod.POST, RESTORE_FUNCTION_ON_PHASE_RESTORATION, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        });
    }

    public List<SchedulerPanelDTO> registerNextTrigger(Long unitId, List<SchedulerPanelDTO> schedulerPanelDTOS) {
        return userRestClientForScheduler.publishRequest(schedulerPanelDTOS, unitId, RestClientUrlType.UNIT_WITHOUT_PARENT_ORG, HttpMethod.POST, MicroService.SCHEDULER, "/scheduler_panel", null, new ParameterizedTypeReference<com.kairos.commons.client.RestTemplateResponseEnvelope<List<SchedulerPanelDTO>>>() {
        });
    }

    public String getTimeZoneByUnitId(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT_WITHOUT_PARENT_ORG, HttpMethod.GET, UNIT_TIMEZONE, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<String>>() {
        });
    }

    public Map<Long, String> getTimeZoneByUnitIds(Set<Long> unitIds) {
        return genericRestClient.publishRequest(unitIds, null, RestClientUrlType.ORGANIZATION, HttpMethod.POST, UNITS_TIMEZONE, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<Long, String>>>() {
        });
    }


    public List<StaffKpiFilterDTO> getStaffsByFilter(StaffEmploymentTypeDTO staffEmploymentTypeDTO) {
        return genericRestClient.publishRequest(staffEmploymentTypeDTO, null, RestClientUrlType.COUNTRY, HttpMethod.POST, STAFF_BY_KPI_FILTER, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffKpiFilterDTO>>>() {
        });
    }

    public StaffAdditionalInfoDTO verifyUnitEmploymentOfStaffByEmploymentId(Long unitId, LocalDate shiftDate, String type, Long employmentId, Set<Long> reasonCodeIds,LocalDate activityCutOffEndDate) {
        List<NameValuePair> queryParamList = new ArrayList<>();
        queryParamList.add(new BasicNameValuePair("type", type));
        queryParamList.add(new BasicNameValuePair(START_DATE, shiftDate != null ? shiftDate.toString() : DateUtils.getCurrentLocalDate().toString()));
        queryParamList.add(new BasicNameValuePair("activityCutOffEndDate", activityCutOffEndDate != null && shiftDate != null ? shiftDate.toString() : DateUtils.getCurrentLocalDate().toString()));
        if (CollectionUtils.isNotEmpty(reasonCodeIds)) {
            queryParamList.add(new BasicNameValuePair("reasonCodeIds", RestClientUrlUtil.arrayToDelimitedString(reasonCodeIds)));
        }
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, STAFF_EMPLOYMENT_BY_EMPLOYMENT_ID, queryParamList, new ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffAdditionalInfoDTO>>() {
        }, employmentId);
    }

    public Set<BigInteger> getTeamActivitiesOfStaff(Long unitId,Long staffId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, TEAM_ACTIVITIES, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Set<BigInteger>>>() {
        }, staffId);
    }

    public List<Long> getAllOrganizationIds(Long unitId) {
        List<NameValuePair> queryParamList = isNotNull(unitId) ?  newArrayList(new BasicNameValuePair("unitId", unitId.toString())) : null;
        return genericRestClient.publishRequest(null, null, RestClientUrlType.ORGANIZATION, HttpMethod.GET, GET_ORGANIZATION_IDS, queryParamList, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Long>>>() {});
    }


    public SelfRosteringMetaData getPublicHolidaysDayTypeAndReasonCodeByUnitId(Long unitId){
        return genericRestClient.publishRequest(null,unitId,RestClientUrlType.UNIT,HttpMethod.GET, GET_PUBLIC_HOLIDAY_DAY_TYPE_REASON_CODE,null,new ParameterizedTypeReference<RestTemplateResponseEnvelope<SelfRosteringMetaData>>() {});
    }

    public boolean verifyingIsActivityAlreadyAssigned(BigInteger activityId, Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET,
                IS_ACTIVITY_ASSIGNED, Arrays.asList(new BasicNameValuePair("activityId", activityId.toString())),
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
                }, activityId);
    }

    public ShiftFilterDefaultData getShiftFilterDefaultData(SelfRosteringFilterDTO selfRosteringFilterDTO) {
        return genericRestClient.publishRequest(selfRosteringFilterDTO, selfRosteringFilterDTO.getUnitId(), RestClientUrlType.UNIT, HttpMethod.POST, "/get_filter_data", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<ShiftFilterDefaultData>>() {
        });
    }

    public Boolean isUnit(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, "/is_unit", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        });
    }

    public List<Long> getUnitIds(Long countryId) {
        return genericRestClient.publishRequest(null, countryId, RestClientUrlType.COUNTRY, HttpMethod.GET, "/get_all_units", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Long>>>() {
        });
    }

    public List<EmploymentTypeDTO> getEmploymentTypeByCountry(Long countryId) {
        return genericRestClient.publishRequest(null, countryId, RestClientUrlType.COUNTRY, HttpMethod.GET, "/employment_type", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<EmploymentTypeDTO>>>() {});
    }


    public List<EmploymentTypeDTO> getEmploymentTypeByUnitId(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, "/employment_type", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<EmploymentTypeDTO>>>() {
        });
    }
    public List<EmploymentTypeDTO> getEmploymentTypeList(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, "/employment_types", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<EmploymentTypeDTO>>>() {
        });
    }


    public Set<LocalDate> getAllDateByFunctionIds(Long unitId, List<Long> functionIds) {
        Set<Object> data= genericRestClient.publishRequest(functionIds, unitId, RestClientUrlType.UNIT, HttpMethod.POST, "/get_functions_date", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Set<Object>>>() {});
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(data,LocalDate.class);
    }

    public Set<BigInteger> getSickSettingsOfUnit(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, "/sick_settings/default", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Set<BigInteger>>>() {});

    }

    public List<EmploymentWithCtaDetailsDTO> getAllEmploymentByUnitId(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, GET_ALL_EMPLOYMENT_BY_UNIT_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<EmploymentWithCtaDetailsDTO>>>() {
        });
    }

    public List<StaffDTO> getStaffByUnitId(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, GET_STAFF_BY_UNITID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffDTO>>>() {
        });
    }

    public List<Tag> getAllStaffTagsByCountryIdOrOrganizationId(Long id, String filterText, boolean iscountryId) {
        List<NameValuePair> queryParamList = new ArrayList<>();
        queryParamList.add(new BasicNameValuePair("masterDataType", MasterDataTypeEnum.STAFF.toString()));
        queryParamList.add(new BasicNameValuePair("filterText", filterText));
        Map<String, Object> staffTags = genericRestClient.publishRequest(null, id, iscountryId ? RestClientUrlType.COUNTRY : RestClientUrlType.UNIT, HttpMethod.GET, "/tag", queryParamList, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>>() { });
        List<Tag> tags = new ArrayList<>();
        if(staffTags.containsKey("tags")){
            for (Map<String, Object> staffTag : (List<Map<String,Object>>) staffTags.get("tags")) {
                tags.add(new Tag(new BigInteger(staffTag.get("id").toString()), staffTag.get("name").toString(), MasterDataTypeEnum.valueOf(staffTag.get("masterDataType").toString()), (Boolean) staffTag.get("countryTag"), id));
            }
        }
        return tags;
    }

    public List<Skill> getSkillsByName(Set<String> skillsOfAllTimeCareActivity, Long countryId) {
        return genericRestClient.publishRequest(skillsOfAllTimeCareActivity, countryId, RestClientUrlType.COUNTRY, HttpMethod.POST, "/skills_by_name", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Skill>>>() {});
    }

    public List<Skill> getSkillsByIds(List<Long> skillIds, Long countryId) {
        return genericRestClient.publishRequest(skillIds, countryId, RestClientUrlType.COUNTRY, HttpMethod.POST, "/skills_by_id", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Skill>>>() {});
    }

    public ActivityDTO getAllSkillsByUnit(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, "/skills_and_expertise_by_unit", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<ActivityDTO>>() {});
    }

    public <T> FieldPermissionUserData getPermissionData(Set<String> objects){
       return genericRestClient.publishRequest(objects, null, RestClientUrlType.UNIT, HttpMethod.POST, "/fetch_permissions", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<FieldPermissionUserData>>() {
        });
    }
    public FilteredStaffsAndRequiredDataFilterDTO getAllPlanningStaffForUnit(Long unitId, ShiftSearchDTO shiftSearchDTO, boolean showAllStaffs){
        LOGGER.debug("filter selections being sent {}",shiftSearchDTO);
        List<NameValuePair> queryParamList = newArrayList(new BasicNameValuePair("showAllStaffs", ""+showAllStaffs));
        return genericRestClient.publishRequest(shiftSearchDTO, unitId, RestClientUrlType.UNIT, HttpMethod.POST, "/staff/get_all_planning_staff", queryParamList, new ParameterizedTypeReference<RestTemplateResponseEnvelope<FilteredStaffsAndRequiredDataFilterDTO>>() {});
    }

    public void createPermissionModels(List<Map<String, Object>> permissionSchema){
        genericRestClient.publishRequest(permissionSchema, null, RestClientUrlType.ORGANIZATION,HttpMethod.POST, "/create_permission_schema", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {});
    }

    public Set<String> getAccessRolesByAccessGroupIds(Long unitId, Set<Long> accessGroupIds) {
        return genericRestClient.publishRequest(accessGroupIds, unitId, RestClientUrlType.UNIT, HttpMethod.POST, "/get_access_roles", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Set<String>>>() {});
    }

    public void createActions(List<ActionDTO> permissionActions){
        genericRestClient.publishRequest(permissionActions, null, RestClientUrlType.ORGANIZATION,HttpMethod.PUT, "/create_action_permission", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {});
    }

    public boolean validPermissionAction(String modelName, PermissionAction action, Long lastSelectedOrganizationId) {
        Map<String,Object> permissionActions=new HashMap<>();
        permissionActions.put("modelName",modelName);
        permissionActions.put("action",action);
        return genericRestClient.publishRequest(permissionActions, null, RestClientUrlType.ORGANIZATION,HttpMethod.PUT, "/validate_action_permission", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {});
    }

    public <T> RequiredDataForFilterDTO getRequiredDataForFilter(Long unitId, Map<FilterType, Set<T>> filterTypeMap) {
        return genericRestClient.publishRequest(filterTypeMap, unitId, RestClientUrlType.UNIT, HttpMethod.POST, "/get_required_data_for_filter", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<RequiredDataForFilterDTO>>() {});
    }

    public Set<Long> getAllExpertiseByCountryId(Long countryId) {
        return genericRestClient.publishRequest(null, countryId, RestClientUrlType.COUNTRY, HttpMethod.GET, "/all_expertise_by_country", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Set<Long>>>() {});
    }

    public List<ExpertiseDTO> getAllExpertiseInfoByCountryId(Long countryId) {
        return genericRestClient.publishRequest(null, countryId, RestClientUrlType.COUNTRY, HttpMethod.GET, "/expertise_of_country", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<ExpertiseDTO>>>() {});
    }

    public List<StaffAdditionalInfoDTO> getEligibleStaffsForCoverShifts(NotEligibleStaffDataDTO notEligibleStaffDataDTO,Long unitId) {
        return genericRestClient.publishRequest(notEligibleStaffDataDTO, unitId, RestClientUrlType.UNIT, HttpMethod.POST, "/staff/get_eligible_staffs_for_cover_shifts", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffAdditionalInfoDTO>>>() {});
    }

    public FunctionsWithUserAccessRoleDTO getFunctionsWithUserAccessRoleDTO(Long employmentId, LocalDate startDate, LocalDate endDate) {
        List<NameValuePair> queryParamList = new ArrayList<>();
        queryParamList.add(new BasicNameValuePair(START_DATE, startDate.toString()));
        queryParamList.add(new BasicNameValuePair("endDate", endDate!=null? endDate.toString():null));
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, FUNCTIONS_WITH_ACCESS_ROLE, queryParamList, new ParameterizedTypeReference<RestTemplateResponseEnvelope<FunctionsWithUserAccessRoleDTO>>() {
        }, employmentId);
    }

    public StaffEmploymentDetails getEmploymentDetailsOfStaffByEmploymentId(Long unitId, Long employmentId) {
        List<NameValuePair> queryParamList = new ArrayList<>();
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, STAFF_EMPLOYMENT_DETAILS_BY_EMPLOYMENT_ID, queryParamList, new ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffEmploymentDetails>>() {
        }, employmentId);
    }

    public StaffAdditionalInfoDTO getStaffDetailsForBalances(Long unitId, LocalDate localDate,Long employmentId) {
        List<NameValuePair> queryParamList = new ArrayList<>();
        queryParamList.add(new BasicNameValuePair(START_DATE, localDate != null ? localDate.toString() : DateUtils.getCurrentLocalDate().toString()));
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, STAFF_DETAILS_FOR_BALANCES_BY_EMPLOYMENT_ID, queryParamList, new ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffAdditionalInfoDTO>>() {
        }, employmentId);
    }

}


