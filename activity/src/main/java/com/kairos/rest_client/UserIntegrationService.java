package com.kairos.rest_client;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.RestClientUrlUtil;
import com.kairos.dto.activity.activity.activity_tabs.OrganizationMappingDTO;
import com.kairos.dto.activity.counter.distribution.access_group.AccessGroupPermissionCounterDTO;
import com.kairos.dto.activity.counter.distribution.access_group.StaffIdsDTO;
import com.kairos.dto.activity.counter.distribution.org_type.OrgTypeDTO;
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
import com.kairos.dto.activity.wta.basic_details.WTADefaultDataInfoDTO;
import com.kairos.dto.scheduler.scheduler_panel.SchedulerPanelDTO;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.access_page.KPIAccessPageDTO;
import com.kairos.dto.user.access_permission.StaffAccessGroupDTO;
import com.kairos.dto.user.client.Client;
import com.kairos.dto.user.client.ClientOrganizationIds;
import com.kairos.dto.user.client.ClientTemporaryAddress;
import com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO;
import com.kairos.dto.user.country.basic_details.CountryDTO;
import com.kairos.dto.user.country.day_type.DayType;
import com.kairos.dto.user.country.day_type.DayTypeEmploymentTypeWrapper;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotWrapper;
import com.kairos.dto.user.organization.*;
import com.kairos.dto.user.organization.skill.OrganizationClientWrapper;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.dto.user.reason_code.ReasonCodeWrapper;
import com.kairos.dto.user.staff.ClientStaffInfoDTO;
import com.kairos.dto.user.staff.OrganizationStaffWrapper;
import com.kairos.dto.user.staff.employment.StaffEmploymentQueryResult;
import com.kairos.dto.user.staff.employment.StaffEmploymentUnitDataWrapper;
import com.kairos.dto.user.staff.staff.StaffResultDTO;
import com.kairos.dto.user.staff.staff.UnitStaffResponseDTO;
import com.kairos.dto.user.team.TeamDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.dto.user_context.CurrentUserDetails;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.MasterDataTypeEnum;
import com.kairos.enums.rest_client.MicroService;
import com.kairos.enums.rest_client.RestClientUrlType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.client_exception.ClientExceptionDTO;
import com.kairos.persistence.model.counter.AccessGroupKPIEntry;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetail;
import com.kairos.persistence.model.tag.Tag;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.user_context.User;
import com.kairos.wrapper.task_demand.TaskDemandRequestWrapper;
import com.kairos.wrapper.task_demand.TaskDemandVisitWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_EMPLOYMENT_NOTFOUND;
import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_STAFF_NOT_FOUND_BY_USER;
import static com.kairos.constants.ApiConstants.*;


@Service
@Transactional
public class UserIntegrationService {

    private static GenericRestClient genericRestClient;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private UserRestClientForScheduler userRestClientForScheduler;

    @Inject
    public void setGenericRestClient(GenericRestClient genericRestClient) {
        this.genericRestClient = genericRestClient;
    }

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

    public StaffEmploymentUnitDataWrapper getStaffsEmployment(Long unitId, List<Long> staffIds, Long expertiseId) {
        StaffEmploymentUnitDataWrapper staffEmploymentUnitDataWrapper= genericRestClient.publishRequest(staffIds, unitId, RestClientUrlType.UNIT, HttpMethod.POST, EMPLOYMENTS_BY_EXPERTISE_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffEmploymentUnitDataWrapper>>() {
        }, expertiseId);
        UserContext.getUserDetails().setUnitWiseAccessRole(staffEmploymentUnitDataWrapper.getUnitWiseAccessRole());
        return staffEmploymentUnitDataWrapper;
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

    public List<KPIAccessPageDTO> getKPIEnabledTabsForModuleForCountry(Long countryId) {
        return genericRestClient.publishRequest(null, countryId, RestClientUrlType.COUNTRY, HttpMethod.GET, KPI_DETAILS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<KPIAccessPageDTO>>>() {
        });
    }

    public List<KPIAccessPageDTO> getKPIEnabledTabsForModuleForUnit(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, KPI_DETAILS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<KPIAccessPageDTO>>>() {
        });
    }

    public List<OrgTypeDTO> getOrganizationIdsBySubOrgId(List<Long> orgTypeId) {
        return genericRestClient.publishRequest(orgTypeId, null, RestClientUrlType.ORGANIZATION, HttpMethod.POST, ORGANIZATION_TYPE_GET_ORGANIZATION_IDS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<OrgTypeDTO>>>() {
        });
    }

    public List<StaffIdsDTO> getStaffIdsByunitAndAccessGroupId(Long unitId, List<Long> accessGroupId) {
        return genericRestClient.publishRequest(accessGroupId, unitId, RestClientUrlType.UNIT, HttpMethod.POST, ACCESS_GROUP_STAFFS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffIdsDTO>>>() {
        });
    }

    public List<StaffPersonalDetail> getStaffDetailByIds(Long unitId, Set<Long> staffIds) {
        return genericRestClient.publishRequest(staffIds, unitId, RestClientUrlType.UNIT, HttpMethod.POST, STAFF_DETAILS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffPersonalDetail>>>() {
        });
    }

    public Long getStaffIdByUserId(Long unitId) {
        Long value = genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, USER_STAFF_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Long>>() {
        });

        if (value == null) exceptionService.dataNotFoundByIdException(MESSAGE_STAFF_NOT_FOUND_BY_USER);
        return value;
    }


    public AccessGroupPermissionCounterDTO getAccessGroupIdsAndCountryAdmin(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, STAFF_USER_ACCESS_GROUP, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<AccessGroupPermissionCounterDTO>>() {
        });

    }

    public Long removeFunctionFromEmploymentByDate(Long unitId, Long employmentId, Date shiftDate) {
        BasicNameValuePair appliedDate = new BasicNameValuePair("appliedDate", DateUtils.asLocalDate(shiftDate).toString());
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.DELETE, REMOVE_FUNCTION_FROM_EMPLOYMENT_ON_DELETE_SHIFT, Collections.singletonList(appliedDate), new ParameterizedTypeReference<RestTemplateResponseEnvelope<Long>>() {
        }, employmentId);
    }

    public Boolean restoreFunctionFromEmploymentByDate(Long unitId, Long employmentId, Map<Long, Set<LocalDate>> dateAndFunctionIdMap) {

        return genericRestClient.publishRequest(dateAndFunctionIdMap, unitId, RestClientUrlType.UNIT, HttpMethod.POST, RESTORE_FUNCTIONS_BY_EMPLOYMENT_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        }, employmentId);

    }

    public Map<LocalDate, Long> removeFunctionFromEmploymentByDates(Long unitId, Long employmentId, Set<LocalDate> dates) {
        return genericRestClient.publishRequest(dates, unitId, RestClientUrlType.UNIT, HttpMethod.DELETE, REMOVE_FUNCTIONS_BY_EMPLOYMENT_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<LocalDate, Long>>>() {
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

    public List<StaffResultDTO> getStaffAndOrganizationDetails(Long userId, BasicNameValuePair sickSettingsRequired) {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.ORGANIZATION, HttpMethod.GET, USER_WITH_ID_UNIT_SICK_SETTINGS, Collections.singletonList(sickSettingsRequired), new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffResultDTO>>>() {
        }, userId);
    }

    public Set<BigInteger> getSickTimeTypeIds(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, SICK_SETTINGS_DEFAULT, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Set<BigInteger>>>() {
        });
    }


    public CTABasicDetailsDTO getCtaBasicDetailsDTO(Long countryId, List<NameValuePair> requestParam) {
        return genericRestClient.publishRequest(null, countryId, RestClientUrlType.COUNTRY, HttpMethod.GET, CTA_BASIC_INFO, requestParam, new ParameterizedTypeReference<RestTemplateResponseEnvelope<CTABasicDetailsDTO>>() {
        });
    }

    public List<AccessGroupPermissionCounterDTO> getStaffAndAccessGroups(AccessGroupKPIEntry accessGroupKPIEntry) {
        return genericRestClient.publishRequest(Arrays.asList(accessGroupKPIEntry.getAccessGroupId()), accessGroupKPIEntry.getUnitId(), RestClientUrlType.UNIT, HttpMethod.POST, STAFFS_ACCESS_GROUPS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<AccessGroupPermissionCounterDTO>>>() {
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

    //AbsenceTypeRestClient
    //TODO Remove in future because On User MicroService this API is not currently Active
    public Map<String, Object> getAbsenceTypeByName(String title) {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.ORGANIZATION, HttpMethod.GET, ABSENCE_TYPES_TITLE, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>>() {
        }, title);
    }

    //CountryRestClient
    public Object getAllContractType(Long countryId) {
        return genericRestClient.publishRequest(null, countryId, RestClientUrlType.COUNTRY, HttpMethod.GET, CONTRACT_TYPE, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Object>>() {
        });
    }

    public CountryDTO getCountryByOrganizationService(Long organizationServiceId) {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.ORGANIZATION, HttpMethod.GET, COUNTRY_ORGANIZATION_SERVICE_URL, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<CountryDTO>>() {
        }, organizationServiceId);
    }

    public OrganizationTypeHierarchyQueryResult getOrgTypesHierarchy(Long countryId, Set<Long> organizationSubTypes) {
        return genericRestClient.publishRequest(organizationSubTypes, countryId, RestClientUrlType.COUNTRY, HttpMethod.POST, ORGANIZATION_TYPES_HIERARCHY, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationTypeHierarchyQueryResult>>() {
        });
    }

    public List<Map<String, Object>> getSkillsByCountryForTaskType(Long countryId) {
        return genericRestClient.publishRequest(null, countryId, RestClientUrlType.COUNTRY, HttpMethod.GET, TASK_TYPES_SKILLS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Map<String, Object>>>>() {
        });
    }

    public List<TimeSlot> getTimeSlotSetsOfCountry(Long countryId) {
        return genericRestClient.publishRequest(null, countryId, RestClientUrlType.COUNTRY, HttpMethod.GET, TIME_SLOTS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<TimeSlot>>>() {
        });
    }

    public CountryDTO getCountryById(long countryId) {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.ORGANIZATION, HttpMethod.GET, COUNTRY_COUNTRY_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<CountryDTO>>() {
        }, countryId);
    }

    public boolean isCountryExists(long countryId) {
        return genericRestClient.publishRequest(null, countryId, RestClientUrlType.COUNTRY, HttpMethod.GET, "", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<CountryDTO>>() {
        }) != null;
    }

    public List<DayType> getDayTypes(List<Long> dayTypes) {
        return genericRestClient.publishRequest(dayTypes, null, RestClientUrlType.ORGANIZATION, HttpMethod.POST, DAY_TYPES, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<DayType>>>() {
        });
    }


    public Map<String, String> getFLSCredentials(Long citizenUnitId) {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, INTEGRATION_UNIT_CITIZEN_UNIT_ID_FLSCRED, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, String>>>() {
        }, citizenUnitId);
    }

    //OrganizationServiceRestClient
    public Map<String, Object> getOrganizationServices(Long unitId, String organizationType) {
        BasicNameValuePair basicNameValuePair = new BasicNameValuePair("type", organizationType);
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, SERVICE_DATA, Arrays.asList(basicNameValuePair), new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>>() {
        });
    }

    //SkillRestClient
    public List<Map<String, Object>> getSkillsOfOrganization(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, SKILLS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Map<String, Object>>>>() {
        });
    }

    public StaffPersonalDetail getStaff(Long staffId) {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, STAFF_WITH_STAFF_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffPersonalDetail>>() {
        }, staffId);
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

    public List<StaffPersonalDetail> getStaffListByUnit() {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, STAFF_GET_STAFF_BY_UNIT, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffPersonalDetail>>>() {
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

    //On user micro-service query Param is not applicable
    public StaffEmploymentDetails verifyUnitEmploymentOfStaff(Long staffId, Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, VERIFY_UNIT_EMPLOYEMNT_BY_STAFF_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffEmploymentDetails>>() {
        }, staffId);
    }
    public StaffEmploymentDetails mainUnitEmploymentOfStaff(Long staffId, Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, MAIN_UNIT_EMPLOYEMNT_BY_STAFF_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffEmploymentDetails>>() {
        }, staffId);
    }


    public StaffAdditionalInfoDTO verifyUnitEmploymentOfStaff(LocalDate shiftDate, Long staffId, Long unitEmploymentId, Set<Long> reasonCodeIds) {
        List<NameValuePair> queryParamList = new ArrayList<>();
        queryParamList.add(new BasicNameValuePair("shiftDate", shiftDate != null ? shiftDate.toString() : DateUtils.getCurrentLocalDate().toString()));
        if (CollectionUtils.isNotEmpty(reasonCodeIds)) {
            queryParamList.add(new BasicNameValuePair("reasonCodeIds", RestClientUrlUtil.arrayToDelimitedString(reasonCodeIds)));
        }
        StaffAdditionalInfoDTO staffAdditionalInfoDTO= genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, VERIFY_UNIT_EMPLOYEMNT_BY_STAFF_ID_UNIT_EMPLOYEMENT_ID, queryParamList, new ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffAdditionalInfoDTO>>() {
        }, staffId, unitEmploymentId);
        UserContext.getUserDetails().setUnitWiseAccessRole(staffAdditionalInfoDTO.getUnitWiseAccessRole());
        return staffAdditionalInfoDTO;
    }

    public StaffAdditionalInfoDTO verifyUnitEmploymentOfStaffWithUnitId(Long unitId, LocalDate shiftDate, Long staffId, String type, Long unitEmploymentId) {
        List<NameValuePair> queryParamList = new ArrayList<>();
        queryParamList.add(new BasicNameValuePair("type", type));
        queryParamList.add(new BasicNameValuePair("shiftDate", shiftDate != null ? shiftDate.toString() : DateUtils.getCurrentLocalDate().toString()));
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, VERIFY_UNIT_EMPLOYEMNT_BY_STAFF_ID_UNIT_EMPLOYEMENT_ID, queryParamList, new ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffAdditionalInfoDTO>>() {
        }, staffId, unitEmploymentId);
    }

    public StaffAdditionalInfoDTO verifyEmploymentAndFindFunctionsAfterDate(Long staffId, Long employmentId) {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, GET_FUNCTIONS_OF_EMPLOYMENT, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffAdditionalInfoDTO>>() {
        }, staffId, employmentId);
    }

    public Map<LocalDate,List<FunctionDTO>> getFunctionsOfEmployment(Long employmentId, LocalDate startDate, LocalDate endDate) {
        List<NameValuePair> queryParamList = new ArrayList<>();
        queryParamList.add(new BasicNameValuePair("startDate", startDate.toString()));
        queryParamList.add(new BasicNameValuePair("endDate", endDate!=null? endDate.toString():null));
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, FUNCTIONS_OF_EMPLOYMENT, queryParamList, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<LocalDate,List<FunctionDTO>>>>() {
        }, employmentId);
    }

    public StaffPersonalDetail getStaffByUser(Long userId) {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, STAFF_CURRENT_USER_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffPersonalDetail>>() {
        }, userId);
    }

    public List<StaffPersonalDetail> getStaffInfo(Long unitId, Set<Long> expertiseIds) {
        return genericRestClient.publishRequest(expertiseIds, unitId, RestClientUrlType.UNIT, HttpMethod.POST, STAFF_GET_STAFF_BY_EXPERTISES, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffPersonalDetail>>>() {
        });
    }

    public UserAccessRoleDTO getAccessOfCurrentLoggedInStaff() {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, CURRENT_USER_ACCESS_ROLE, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<UserAccessRoleDTO>>() {
        });
    }

    public ReasonCodeWrapper getAccessRoleAndReasonCodes() {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, ACCESS_ROLE_AND_REASON_CODE, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<ReasonCodeWrapper>>() {
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


    public ReasonCodeWrapper getUnitInfoAndReasonCodes(Long unitId, List<NameValuePair> requestParam) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT_WITHOUT_PARENT_ORG, HttpMethod.GET, UNIT_LOCATION_AND_REASON_CODE, requestParam, new ParameterizedTypeReference<RestTemplateResponseEnvelope<ReasonCodeWrapper>>() {
        });
    }


    //TimeSlotRestClient
    public Map<String, Object> getTimeSlotByUnitIdAndTimeSlotId(Long timeSlotId) {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, TIME_SLOT_URL, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>>() {
        }, timeSlotId);
    }

    public List<TimeSlotWrapper> getCurrentTimeSlot() {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, CURRENT_TIME_SLOTS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<TimeSlotWrapper>>>() {
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

    public WTADefaultDataInfoDTO getWtaTemplateDefaultDataInfo(Long countryId) {
        return genericRestClient.publishRequest(null, countryId, RestClientUrlType.COUNTRY, HttpMethod.GET, GET_WTA_TEMPLATE_DEFAULT_DATA_INFO, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<WTADefaultDataInfoDTO>>() {
        });
    }

    public WTADefaultDataInfoDTO getWtaTemplateDefaultDataInfoByUnitId() {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, GET_WTA_TEMPLATE_DEFAULT_DATA_INFO_BY_UNIT_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<WTADefaultDataInfoDTO>>() {
        });
    }

    public List<TimeSlotDTO> getDefaultTimeSlot(Long countryId) {
        return genericRestClient.publishRequest(null, countryId, RestClientUrlType.COUNTRY, HttpMethod.GET, "/get_default_timeSlot", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<TimeSlotDTO>>>() {
        });
    }

    // ~ ========ClientRestClient===================publishRequestWithoutAuth=====
    public Client getClient(Long clientId) {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, CLIENT_ID_URL, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Client>>() {
        }, clientId);
    }

    public ClientTemporaryAddress updateClientTemporaryAddress(ClientExceptionDTO clientExceptionDto, Long clientId) {
        return genericRestClient.publishRequest(clientExceptionDto, null, RestClientUrlType.UNIT, HttpMethod.POST, UPDATE_CLIENT_TEMP_ADDRESS_BY_CLIENT_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<ClientTemporaryAddress>>() {
        }, clientId);
    }

    public Map<String, Object> getClientDetails(Long citizenId) {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, CLIENT_CITIZEN_ID_INFO, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>>() {
        }, citizenId);
    }

    public Map<String, Object> getClientAddressInfo(Long citizenId) {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, CLIENT_CITIZEN_ID_ADDRESS_INFO, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>>() {
        }, citizenId);
    }

    public TaskDemandVisitWrapper getClientDetailsForTaskDemandVisit(TaskDemandRequestWrapper taskDemandRequestWrapper) {
        return genericRestClient.publishRequest(taskDemandRequestWrapper, null, RestClientUrlType.UNIT, HttpMethod.POST, GET_CLIENT_INFO, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<TaskDemandVisitWrapper>>() {
        });
    }

    public TaskDemandVisitWrapper getPrerequisitesForTaskCreation(Long citizenId, Long unitId) {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, CLIENT_CITIZEN_ID_UNIT_ID_TASK_PREREQUISITES, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<TaskDemandVisitWrapper>>() {
        }, citizenId, unitId);
    }

    //On user-microservive ClientController organizationId is not actually this function organizationId
    //TODO verify
    public OrganizationClientWrapper getOrganizationClients() {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, CLIENT_ORGANIZATION_CLIENTS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationClientWrapper>>() {
        });
    }

    public OrganizationClientWrapper getClientsByIds(List<Long> clientIds) {
        return genericRestClient.publishRequest(clientIds, null, RestClientUrlType.UNIT, HttpMethod.POST, ORGANIZATION_CLIENTS_IDS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationClientWrapper>>() {
        });
    }

    public List<Long> getCitizenIds() {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, CLIENT_CLIENT_IDS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Long>>>() {
        });
    }

    public ClientStaffInfoDTO getClientStaffInfo(Long clientId) {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, GET_CLIENT_STAFF_INFO_BY_CLIENT_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<ClientStaffInfoDTO>>() {
        }, clientId);
    }

    public Map<String, Object> getStaffAndCitizenHouseholds(Long citizenId, Long staffId) {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, GET_STAFF_CITIZEN_HOUSEHOLDS_BY_CITIZEN_ID_AND_STAFF_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>>() {
        }, citizenId, staffId);
    }

    public List<Client> getCitizensByIdsInList(List<Long> citizenIds) {
        return genericRestClient.publishRequest(citizenIds, null, RestClientUrlType.UNIT, HttpMethod.POST, CLIENT_BY_IDS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Client>>>() {
        });
    }

    //Note here 2nd argument is taken just because of unitId(might not present in UserContext) not null in url
    public List<ClientOrganizationIds> getCitizenIdsByUnitIds(List<Long> unitIds) {
        return genericRestClient.publishRequestWithoutAuth(unitIds, null, RestClientUrlType.UNIT, HttpMethod.POST, GET_UNIT_IDS_BY_CLIENT_IDS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<ClientOrganizationIds>>>() {
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

    public Map<String, Object> getCommonDataOfOrganization(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, COMMON_DATA, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>>() {
        });
    }

    //TODO Testing
    public boolean setOneTimeSyncPerformed(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.PUT, ONE_TIME_SYNC, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        });
    }

    public boolean updateAutoGenerateTaskSettings(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.PUT, AUTO_GENERATE_TASK_SETTINGS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        });
    }

    public Map<String, Object> getTimeSlotByUnitIdAndTimeSlotName(Long unitId, Long timeSlotExternalId) {
        return genericRestClient.publishRequestWithoutAuth(timeSlotExternalId, unitId, RestClientUrlType.UNIT, HttpMethod.POST, TIME_SLOT_NAME, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>>() {
        });
    }

    public OrganizationDTO getOrganizationByTeamId(Long teamId) {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.ORGANIZATION, HttpMethod.GET, GET_ORGANIZATION_BY_TEAM_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationDTO>>() {
        }, teamId);
    }
    public OrganizationDTO getParentOrganizationOfCityLevel(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, GET_PARENT_ORGANIZATION_OF_CITY_LEVEL, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationDTO>>() {
        });
    }

    public OrganizationDTO getParentOfOrganization(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, GET_PARENT_OF_ORGANIZATION, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationDTO>>() {
        });
    }


    public OrganizationStaffWrapper getOrganizationAndStaffByExternalId(String externalId, String staffTimeCare, String staffTimeCareEmploymentId) {
        List<NameValuePair> queryParamList = new ArrayList<>();
        queryParamList.add(new BasicNameValuePair("staffTimeCareId", staffTimeCare));
        queryParamList.add(new BasicNameValuePair("staffTimeCareEmploymentId", staffTimeCareEmploymentId));
        return genericRestClient.publishRequest(null, null, RestClientUrlType.ORGANIZATION, HttpMethod.GET, EXTERNAL_ID_URL, queryParamList, new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationStaffWrapper>>() {
        }, externalId);
    }

    //TODO write implementation previously inside OrganizationRestClient this implementation was written only(here added so as not to break current functionality )
    public boolean isExistOrganization(Long orgId) {
        return getOrganizationDTO(orgId)!=null;
    }

    public Map<String, Object> getTaskDemandSupplierInfo() {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, GET_TASK_DEMAND_SUPPLIER_INFO, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>>() {
        });
    }

    public Map<String, Object> getUnitVisitationInfo() {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, UNIT_VISITATION, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>>() {
        });
    }

    public boolean verifyOrganizationExpertizeAndRegions(OrganizationMappingDTO organizationMappingDTO) {
        return genericRestClient.publishRequest(organizationMappingDTO, null, RestClientUrlType.ORGANIZATION, HttpMethod.POST, VERIFY_ORGANIZATION_EXPERTISE, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        });
    }

    public List<OrganizationDTO> getOrganizationsByOrganizationType(Long organizationTypeId) {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.ORGANIZATION, HttpMethod.GET, ORGANIZATION_TYPE_URL_ORGANIZATIONS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<OrganizationDTO>>>() {
        }, organizationTypeId);
    }

    public OrganizationTypeAndSubTypeDTO getOrganizationTypeAndSubTypeByUnitId(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, ORGANIZATION_TYPE_AND_SUB_TYPES, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationTypeAndSubTypeDTO>>() {
        });
    }

    public Boolean linkOrganizationTypeWithService(Set<Long> organizationTypes, Long organizationServiceId) {
        return genericRestClient.publishRequest(organizationTypes, null, RestClientUrlType.ORGANIZATION, HttpMethod.POST, ORGANIZATION_SERVICE_ASSIGN_ORGANIZATION_TYPES, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        }, organizationServiceId);
    }

    public Boolean deleteLinkingOfOrganizationTypeAndService(Set<Long> organizationTypes, Long organizationServiceId) {
        return genericRestClient.publishRequest(organizationTypes, null, RestClientUrlType.ORGANIZATION, HttpMethod.DELETE, ORGANIZATION_SERVICE_DETACH_ORGANIZATION_TYPES, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        }, organizationServiceId);
    }

    //TODO  Add custom Auth to fix this
    public OrganizationDTO getOrganizationWithoutAuth(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, WITHOUT_AUTH, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationDTO>>() {
        });
    }


    public OrganizationSkillAndOrganizationTypesDTO getOrganizationSkillOrganizationSubTypeByUnitId(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, SKILL_ORG_TYPES, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationSkillAndOrganizationTypesDTO>>() {
        });
    }

    public List<DayType> getDayType(Date date) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormatter.format(date);
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, GET_DATE_TYPE_BY_DATES, Arrays.asList(new BasicNameValuePair("date", dateString)), new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<DayType>>>() {
        });
    }

    public List<DayType> getDayTypes(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, DAY_TYPE, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<DayType>>>() {
        });
    }

    public List<DayType> getDayTypesByCountryId(Long countryId) {
        return genericRestClient.publishRequest(null, countryId, RestClientUrlType.COUNTRY, HttpMethod.GET, DAY_TYPE, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<DayType>>>() {
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

    public Long getOrganizationIdByTeam(Long teamId) {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, TEAM_ORGANIZATION_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Long>>() {
        }, teamId);
    }

    public String getTimezone() {
        return userRestClientForScheduler.publishRequest(null, 2567L, RestClientUrlType.UNIT, HttpMethod.GET, MicroService.USER, "/time_zone", null, new ParameterizedTypeReference<com.kairos.commons.client.RestTemplateResponseEnvelope<String>>() {
        });
    }


    public StaffPersonalDetail getStaff(Long unitId,Long staffId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, STAFF_WITH_STAFF_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffPersonalDetail>>() {}, staffId);
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

    public DefaultKpiDataDTO getKpiDefaultData(StaffEmploymentTypeDTO staffEmploymentTypeDTO) {
        return genericRestClient.publishRequest(staffEmploymentTypeDTO, null, RestClientUrlType.COUNTRY, HttpMethod.POST, KPI_DEFAULT_DATA, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<DefaultKpiDataDTO>>() {
        });
    }

    public DefaultKpiDataDTO getKpiFilterDefaultData(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT_WITHOUT_PARENT_ORG, HttpMethod.GET, KPI_FILTER_DEFAULT_DATA, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<DefaultKpiDataDTO>>() {
        });
    }

    public StaffAdditionalInfoDTO verifyUnitEmploymentOfStaffByEmploymentId(Long unitId, LocalDate shiftDate, String type, Long employmentId, Set<Long> reasonCodeIds) {
        List<NameValuePair> queryParamList = new ArrayList<>();
        queryParamList.add(new BasicNameValuePair("type", type));
        queryParamList.add(new BasicNameValuePair("startDate", shiftDate != null ? shiftDate.toString() : DateUtils.getCurrentLocalDate().toString()));
        if (CollectionUtils.isNotEmpty(reasonCodeIds)) {
            queryParamList.add(new BasicNameValuePair("reasonCodeIds", RestClientUrlUtil.arrayToDelimitedString(reasonCodeIds)));
        }
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, STAFF_EMPLOYMENT_BY_EMPLOYMENT_ID, queryParamList, new ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffAdditionalInfoDTO>>() {
        }, employmentId);
    }

    public boolean isReasonCodeLinkedToTimeType(Long countryId, BigInteger timeTypeId) {
        return genericRestClient.publishRequest(null, countryId, RestClientUrlType.COUNTRY, HttpMethod.GET, REASON_CODE_LINK_WITH_TIME_TYPE, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        }, timeTypeId);
    }

    public Long getUnitByEmploymentId(Long employmentId) {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, GET_UNIT_BY_EMPLOYMENT, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Long>>() {
        },employmentId);
    }


    public Set<BigInteger> getTeamActivitiesOfStaff(Long unitId,Long staffId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, TEAM_ACTIVITIES, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Set<BigInteger>>>() {
        }, staffId);
    }

    public List<Long> getAllOrganizationIds(Long unitId) {
        List<NameValuePair> queryParamList = isNotNull(unitId) ?  newArrayList(new BasicNameValuePair("unitId", unitId.toString())) : null;
        return genericRestClient.publishRequest(null, null, RestClientUrlType.ORGANIZATION, HttpMethod.GET, GET_ORGANIZATION_IDS, queryParamList, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Long>>>() {});
    }


    public SelfRosteringMetaData getPublicHolidaysDayTypeAndReasonCodeByUnitId(long unitId){
        return genericRestClient.publishRequest(null,unitId,RestClientUrlType.UNIT,HttpMethod.GET, GET_PUBLIC_HOLIDAY_DAY_TYPE_REASON_CODE,null,new ParameterizedTypeReference<RestTemplateResponseEnvelope<SelfRosteringMetaData>>() {});
    }

    public boolean verifyingIsActivityAlreadyAssigned(BigInteger activityId,long unitId){
        return genericRestClient.publishRequest(null,unitId,RestClientUrlType.UNIT,HttpMethod.GET,
                IS_ACTIVITY_ASSIGNED,Arrays.asList(new BasicNameValuePair("activityId", activityId.toString())),
                new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
                },activityId);
    }

    public List<TimeSlotDTO> getUnitTimeSlot(long unitId){
        return genericRestClient.publishRequest(null,unitId,RestClientUrlType.UNIT,HttpMethod.GET, "/get_time_slots",null,new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<TimeSlotDTO>>>() {});
    }

    public ShiftFilterDefaultData getShiftFilterDefaultData(SelfRosteringFilterDTO selfRosteringFilterDTO){
        return genericRestClient.publishRequest(selfRosteringFilterDTO,selfRosteringFilterDTO.getUnitId(),RestClientUrlType.UNIT,HttpMethod.POST, "/get_filter_data",null,new ParameterizedTypeReference<RestTemplateResponseEnvelope<ShiftFilterDefaultData>>() {});
    }

    public Boolean isUnit(long unitId){
        return genericRestClient.publishRequest(null,unitId,RestClientUrlType.UNIT,HttpMethod.GET, "/is_unit",null,new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {});
    }

    public List<Long> getUnitIds(Long countryId) {
       return genericRestClient.publishRequest(null, countryId, RestClientUrlType.COUNTRY, HttpMethod.GET, "/get_all_units", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Long>>>() {});
    }

    public List<EmploymentTypeDTO> getEmploymentTypeByCountry(Long countryId) {
        return genericRestClient.publishRequest(null, countryId, RestClientUrlType.COUNTRY, HttpMethod.GET, "/employment_type", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<EmploymentTypeDTO>>>() {});
    }


    public List<EmploymentTypeDTO> getEmploymentTypeByUnitId(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, "/employment_type", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<EmploymentTypeDTO>>>() {
        });
    }

    public Set<LocalDate> getAllDateByFunctionIds(Long unitId, List<Long> functionIds) {
        Set<Object> data= genericRestClient.publishRequest(functionIds, unitId, RestClientUrlType.UNIT, HttpMethod.POST, "/get_functions_date", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Set<Object>>>() {});
        return ObjectMapperUtils.copyPropertiesOfCollectionByMapper(data,LocalDate.class);
    }

    public Set<BigInteger> getSickSettingsOfUnit(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, "/sick_settings/default", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Set<BigInteger>>>() {});

    }

    public Map<String, List<StaffPersonalDetail>> getSkillIdAndLevelByStaffIds(Long countryId, List<Long> staffIds,LocalDate selectedFromDate, LocalDate selectedToDate) {
        BasicNameValuePair appliedFromDate = new BasicNameValuePair("selectedFromDate", selectedFromDate.toString());
        BasicNameValuePair appliedToDate = new BasicNameValuePair("selectedToDate", selectedToDate.toString());
        return genericRestClient.publishRequest(staffIds, countryId, RestClientUrlType.COUNTRY, HttpMethod.POST, "/get_Skill_and_level_by_staff_ids", newArrayList(appliedFromDate, appliedToDate), new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, List<StaffPersonalDetail>>>>() {});
    }

    public List<EmploymentWithCtaDetailsDTO> getAllEmploymentByUnitId(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, GET_ALL_EMPLOYMENT_BY_UNIT_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<EmploymentWithCtaDetailsDTO>>>() {
        });
    }

    public List<TeamDTO> getTeamByUnitId(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, TEAMS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<TeamDTO>>>() {
        });
    }

    public List<StaffPersonalDetail> getStaffByUnitId(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, GET_STAFF_BY_UNITID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffPersonalDetail>>>() {
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

    public Set<Long> getAllStaffIdsByGroupIds(Long unitId, List<Long> groupIds) {
        return genericRestClient.publishRequest(groupIds, unitId, RestClientUrlType.UNIT, HttpMethod.POST, "/groups", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Set<Long>>>() {});
    }

    public static CurrentUserDetails getCurrentUser(){
        return genericRestClient.publishRequest(null, null, RestClientUrlType.ORGANIZATION, HttpMethod.GET, "/get_current_user", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<CurrentUserDetails>>() {});
    }
}


