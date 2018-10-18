package com.kairos.rest_client;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.counter.distribution.access_group.AccessGroupPermissionCounterDTO;
import com.kairos.dto.activity.counter.distribution.access_group.StaffIdsDTO;
import com.kairos.dto.activity.counter.distribution.org_type.OrgTypeDTO;
import com.kairos.dto.activity.cta.CTABasicDetailsDTO;
import com.kairos.dto.activity.cta.UnitPositionDTO;
import com.kairos.dto.activity.open_shift.PriorityGroupDefaultData;
import com.kairos.dto.activity.open_shift.priority_group.StaffIncludeFilterDTO;
import com.kairos.dto.activity.shift.StaffUnitPositionDetails;
import com.kairos.dto.activity.time_bank.UnitPositionWithCtaDetailsDTO;
import com.kairos.dto.activity.wta.basic_details.WTABasicDetailsDTO;
import com.kairos.dto.activity.wta.basic_details.WTADefaultDataInfoDTO;
import com.kairos.dto.user.access_permission.StaffAccessGroupDTO;
import com.kairos.dto.user.client.Client;
import com.kairos.dto.user.client.ClientOrganizationIds;
import com.kairos.dto.user.client.ClientTemporaryAddress;
import com.kairos.dto.user.country.basic_details.CountryDTO;
import com.kairos.dto.user.country.day_type.DayType;
import com.kairos.dto.user.country.time_slot.TimeSlotWrapper;
import com.kairos.dto.user.organization.OrganizationDTO;
import com.kairos.dto.user.organization.OrganizationTypeHierarchyQueryResult;
import com.kairos.dto.user.organization.TimeSlot;
import com.kairos.dto.user.organization.skill.OrganizationClientWrapper;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.dto.user.staff.ClientStaffInfoDTO;
import com.kairos.dto.user.staff.staff.UnitStaffResponseDTO;
import com.kairos.dto.user.staff.unit_position.StaffUnitPositionQueryResult;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.IntegrationOperation;
import com.kairos.dto.user.organization.UnitAndParentOrganizationAndCountryDTO;
import com.kairos.dto.user.staff.staff.StaffResultDTO;
import com.kairos.enums.rest_client.RestClientUrlType;
import com.kairos.persistence.model.client_exception.ClientExceptionDTO;
import com.kairos.persistence.model.counter.AccessGroupKPIEntry;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.service.exception.ExceptionService;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.access_page.KPIAccessPageDTO;
import com.kairos.dto.user.country.day_type.DayTypeEmploymentTypeWrapper;
import com.kairos.dto.user.staff.StaffDTO;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.wrapper.task_demand.TaskDemandRequestWrapper;
import com.kairos.wrapper.task_demand.TaskDemandVisitWrapper;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

import static com.kairos.constants.ApiConstants.*;

@Service
@Transactional
public class GenericIntegrationService {
    @Inject
    GenericRestClient genericRestClient;
    @Inject
    ExceptionService exceptionService;

    public Long getUnitPositionId(Long unitId, Long staffId, Long expertiseId, Long dateInMillis) {
        BasicNameValuePair basicNameValuePair = new BasicNameValuePair("dateInMillis", dateInMillis.toString());
        Long value = genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, STAFF_ID_EXPERTISE_ID_UNIT_POSITION_ID, Arrays.asList(basicNameValuePair), new ParameterizedTypeReference<RestTemplateResponseEnvelope<Long>>() {
        }, staffId, expertiseId);
        if (value == null) {
            exceptionService.dataNotFoundByIdException("message.unitPosition.notFound", expertiseId);
        }
        return value.longValue();
    }

    public PriorityGroupDefaultData getExpertiseAndEmployment(Long countryId) {
        return genericRestClient.publishRequest(null, countryId, RestClientUrlType.COUNTRY, HttpMethod.GET, EMPLOYEMENT_TYPE_AND_EXPERTISE, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<PriorityGroupDefaultData>>() {
        });
    }

    public PriorityGroupDefaultData getExpertiseAndEmploymentForUnit(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, EMPLOYEMENT_TYPE_AND_EXPERTISE, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<PriorityGroupDefaultData>>() {
        });
    }

    public List<StaffUnitPositionDetails> getStaffsUnitPosition(Long unitId, List<Long> staffIds, Long expertiseId) {
        return genericRestClient.publishRequest(staffIds, unitId, RestClientUrlType.UNIT, HttpMethod.POST, UNIT_POSITIONS_BY_EXPERTISE_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffUnitPositionDetails>>>() {
        }, expertiseId);

    }

    public List<String> getEmailsOfStaffByStaffIds(Long unitId, List<Long> staffIds) {
        return genericRestClient.publishRequest(staffIds, unitId, RestClientUrlType.UNIT, HttpMethod.POST, STAFF_EMAILS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<String>>>() {
        });
    }

    public List<StaffUnitPositionDetails> getStaffIdAndUnitPositionId(Long unitId, List<Long> staffIds, Long expertiseId) {

        return genericRestClient.publishRequest(staffIds, unitId, RestClientUrlType.UNIT, HttpMethod.POST, STAFF_AND_UNIT_POSITIONS_BY_EXPERTISE_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffUnitPositionDetails>>>() {
        }, expertiseId);

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

    public List<StaffDTO> getStaffDetailByIds(Long unitId, Set<Long> staffIds) {
        return genericRestClient.publishRequest(staffIds, unitId, RestClientUrlType.UNIT, HttpMethod.POST, STAFF_DETAILS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffDTO>>>() {
        });
    }

    public Long getStaffIdByUserId(Long unitId) {
        Long value = genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, USER_STAFF_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Long>>() {
        });

        if (value == null) {
            exceptionService.dataNotFoundByIdException("message.staff.notFound");
        }
        return value;
    }


    public AccessGroupPermissionCounterDTO getAccessGroupIdsAndCountryAdmin(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, STAFF_USER_ACCESS_GROUP, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<AccessGroupPermissionCounterDTO>>() {
        });

    }

    public Long removeFunctionFromUnitPositionByDate(Long unitId, Long unitPositionId, Date shiftDate) {
        BasicNameValuePair appliedDate = new BasicNameValuePair("appliedDate", DateUtils.asLocalDate(shiftDate).toString());
        Long functionId = genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.DELETE, APPLY_FUNCTIONS_BY_UNIT_POSITION_ID, Collections.singletonList(appliedDate), new ParameterizedTypeReference<RestTemplateResponseEnvelope<Long>>() {
        }, unitPositionId);
        return functionId;
    }

    public Boolean restoreFunctionFromUnitPositionByDate(Long unitId, Long unitPositionId, Map<Long, Set<LocalDate>> dateAndFunctionIdMap) {

        return genericRestClient.publishRequest(dateAndFunctionIdMap, unitId, RestClientUrlType.UNIT, HttpMethod.POST, RESTORE_FUNCTIONS_BY_UNIT_POSITION_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        }, unitPositionId);

    }

    public Map<LocalDate, Long> removeFunctionFromUnitPositionByDates(Long unitId, Long unitPositionId, Set<LocalDate> dates) {
        return genericRestClient.publishRequest(dates, unitId, RestClientUrlType.UNIT, HttpMethod.DELETE, REMOVE_FUNCTIONS_BY_UNIT_POSITION_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<LocalDate, Long>>>() {
        }, unitPositionId);
    }

    //~ Added by mohit TODO remove comment after verification

    public OrganizationDTO getOrganizationDTO(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, "", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationDTO>>() {
        });
    }

    public UnitPositionDTO getUnitPositionDTO(Long unitId, Long unitEmploymentPositionId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, GET_UNIT_POSITION, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<UnitPositionDTO>>() {
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
    public List<StaffUnitPositionQueryResult> getStaffIdsByPriorityGroupIncludeFilter(StaffIncludeFilterDTO staffIncludeFilterDTO, Long unitId) {
        return genericRestClient.publishRequest(staffIncludeFilterDTO, unitId, RestClientUrlType.UNIT, HttpMethod.POST, STAFF_PRIORTY_GROUP, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffUnitPositionQueryResult>>>() {
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
        return genericRestClient.publishRequest(null, countryId, RestClientUrlType.COUNTRY, HttpMethod.GET, "", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        });
    }

    public List<DayType> getDayTypes(List<Long> dayTypes) {
        return genericRestClient.publishRequest(dayTypes, null, RestClientUrlType.ORGANIZATION, HttpMethod.POST, DAY_TYPES, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<DayType>>>() {
        });
    }


    public Map<String, String> getFLS_Credentials(Long citizenUnitId) {
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

    //StaffRestClient
    public Map<String, Object> getTeamStaffAndStaffSkill(List<Long> staffIds) {
        return genericRestClient.publishRequest(staffIds, null, RestClientUrlType.UNIT, HttpMethod.POST, STAFF_AND_SKILL, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>>() {
        });
    }

    public StaffDTO getStaff(Long staffId) {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, STAFF_WITH_STAFF_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffDTO>>() {
        }, staffId);
    }

    public List<UnitStaffResponseDTO> getUnitWiseStaffList() {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, STAFF_UNIT_WISE, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<UnitStaffResponseDTO>>>() {
        });
    }

    //Previously this API didn't match with any API on user micro-service (corrected)
    public Map<Long, Long> getUnitPositionExpertiseMap(Long organizationId, Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, UNIT_POSITION_EXPERTISE, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<Long, Long>>>() {
        });
    }

    public ClientStaffInfoDTO getStaffInfo() {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, STAFF_GET_STAFF_INFO, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<ClientStaffInfoDTO>>() {
        });
    }

    public List<com.kairos.dto.user.staff.staff.StaffDTO> getStaffListByUnit() {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, STAFF_GET_STAFF_BY_UNIT, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<com.kairos.dto.user.staff.staff.StaffDTO>>>() {
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
    public StaffUnitPositionDetails verifyUnitEmploymentOfStaff(Long staffId, Long unitId, String type) {
        BasicNameValuePair basicNameValuePair = new BasicNameValuePair("type", type);
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, VERIFY_UNIT_EMPLOYEMNT_BY_STAFF_ID, Arrays.asList(basicNameValuePair), new ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffUnitPositionDetails>>() {
        }, staffId);
    }

    public StaffAdditionalInfoDTO verifyUnitEmploymentOfStaff(LocalDate shiftDate, Long staffId, String type, Long unitEmploymentId) {
        List<NameValuePair> queryParamList = new ArrayList<>();
        queryParamList.add(new BasicNameValuePair("type", type));
        queryParamList.add(new BasicNameValuePair("shiftDate", shiftDate.toString()));
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, VERIFY_UNIT_EMPLOYEMNT_BY_STAFF_ID_UNIT_EMPLOYEMENT_ID, queryParamList, new ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffAdditionalInfoDTO>>() {
        }, staffId, unitEmploymentId);
    }

    public StaffDTO getStaffByUser(Long userId) {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, STAFF_CURRENT_USER_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<StaffDTO>>() {
        }, userId);
    }

    public List<com.kairos.dto.user.staff.StaffDTO> getStaffInfo(Long unitId, List<Long> expertiesIdList) {
        return genericRestClient.publishRequest(expertiesIdList, unitId, RestClientUrlType.UNIT, HttpMethod.POST, STAFF_GET_STAFF_BY_EXPERTISES, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffDTO>>>() {
        });
    }

    public UserAccessRoleDTO getAccessOfCurrentLoggedInStaff() {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, CURRENT_USER_ACCESS_ROLE, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<UserAccessRoleDTO>>() {
        });
    }
    //shift service
    public List<StaffAdditionalInfoDTO> getStaffAditionalDTOS(Long unitId,List<NameValuePair> requestParam) {
       return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, "/staff/verifyUnitEmployments", requestParam, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffAdditionalInfoDTO>>>() {
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
    public UnitPositionWithCtaDetailsDTO getCTAbyUnitEmployementPosition(Long unitPositionId) {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, GET_CTA_BY_UNIT_POSITION_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<UnitPositionWithCtaDetailsDTO>>() {
        }, unitPositionId);
    }

    //WTADetailRestClient
    public WTABasicDetailsDTO getWtaRelatedInfo(Long expertiseId, Long organizationSubTypeId, Long countryId, Long organizationId, Long organizationTypeId) {
        List<NameValuePair> queryParamList = new ArrayList<>();
        queryParamList.add(new BasicNameValuePair("countryId", countryId.toString()));
        queryParamList.add(new BasicNameValuePair("organizationId", organizationId.toString()));
        queryParamList.add(new BasicNameValuePair("organizationTypeId", organizationTypeId.toString()));
        queryParamList.add(new BasicNameValuePair("organizationSubTypeId", organizationSubTypeId.toString()));
        queryParamList.add(new BasicNameValuePair("expertiseId", expertiseId.toString()));
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

    // ~ ========ClientRestClient===================publishRequestWithoutAuth=====
    public Client getClient(Long clientId) {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, CLIENT_ID_URL, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Client>>() {
        }, clientId);
    }

    public ClientTemporaryAddress updateClientTemporaryAddress(ClientExceptionDTO clientExceptionDto, Long unitId, Long clientId) {
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
    public OrganizationClientWrapper getOrganizationClients(Long organizationId) {
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

    public ClientStaffInfoDTO getClientStaffInfo(Long clientId){
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, GET_CLIENT_STAFF_INFO_BY_CLIENT_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<ClientStaffInfoDTO>>() {
        },clientId);
    }

    public Map<String, Object> getStaffAndCitizenHouseholds(Long citizenId,Long staffId) {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, GET_STAFF_CITIZEN_HOUSEHOLDS_BY_CITIZEN_ID_AND_STAFF_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>>() {
        },citizenId,staffId);
    }

    public List<Client> getCitizensByIdsInList(List<Long> citizenIds) {
    return genericRestClient.publishRequest(citizenIds, null, RestClientUrlType.UNIT, HttpMethod.POST, CLIENT_BY_IDS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Client>>>() {
    });
    }

    //Note here 2nd argument is taken just because of unitId(might not present in UserContext) not null in url
    public List<ClientOrganizationIds> getCitizenIdsByUnitIds(List<Long> unitIds) {
        return genericRestClient.publishRequestWithoutAuth(unitIds, unitIds.get(0), RestClientUrlType.UNIT, HttpMethod.POST, GET_UNIT_IDS_BY_CLIENT_IDS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<ClientOrganizationIds>>>() {
        });
    }
    }

