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
import com.kairos.dto.user.access_permission.StaffAccessGroupDTO;
import com.kairos.dto.user.country.basic_details.CountryDTO;
import com.kairos.dto.user.country.day_type.DayType;
import com.kairos.dto.user.organization.OrganizationDTO;
import com.kairos.dto.user.organization.OrganizationTypeHierarchyQueryResult;
import com.kairos.dto.user.organization.TimeSlot;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.dto.user.staff.unit_position.StaffUnitPositionQueryResult;
import com.kairos.enums.IntegrationOperation;
import com.kairos.dto.user.organization.UnitAndParentOrganizationAndCountryDTO;
import com.kairos.dto.user.staff.staff.StaffResultDTO;
import com.kairos.enums.rest_client.RestClientUrlType;
import com.kairos.persistence.model.counter.AccessGroupKPIEntry;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.service.exception.ExceptionService;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.access_page.KPIAccessPageDTO;
import com.kairos.dto.user.country.day_type.DayTypeEmploymentTypeWrapper;
import com.kairos.dto.user.staff.StaffDTO;
import com.kairos.commons.utils.ObjectMapperUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

import static com.kairos.constants.ApiConstants.*;

@Service
@Transactional

public class GenericIntegrationService {
    @Autowired
    GenericRestClient genericRestClient;
    @Autowired
    ExceptionService exceptionService;

    public Long getUnitPositionId(Long unitId, Long staffId, Long expertiseId, Long dateInMillis) {
        BasicNameValuePair basicNameValuePair=new BasicNameValuePair("dateInMillis",dateInMillis.toString());
        Long value=genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, STAFF_ID_EXPERTISE_ID_UNIT_POSITION_ID, Arrays.asList(basicNameValuePair), new ParameterizedTypeReference<RestTemplateResponseEnvelope<Long>>() {
        },staffId,expertiseId);
        if (value == null) {
            exceptionService.dataNotFoundByIdException("message.unitPosition.notFound", expertiseId);
        }
        return value.longValue();
    }

    public PriorityGroupDefaultData getExpertiseAndEmployment(Long countryId) {
        return genericRestClient.publishRequest(null, countryId, RestClientUrlType.COUNTRY, HttpMethod.GET,  EMPLOYEMENT_TYPE_AND_EXPERTISE, null,new ParameterizedTypeReference<RestTemplateResponseEnvelope<PriorityGroupDefaultData>>(){});
    }

    public PriorityGroupDefaultData getExpertiseAndEmploymentForUnit(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, EMPLOYEMENT_TYPE_AND_EXPERTISE, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<PriorityGroupDefaultData>>(){});
    }

    public List<StaffUnitPositionDetails> getStaffsUnitPosition(Long unitId, List<Long> staffIds, Long expertiseId) {
        return genericRestClient.publishRequest(staffIds, unitId, RestClientUrlType.UNIT, HttpMethod.POST, UNIT_POSITIONS_BY_EXPERTISE_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffUnitPositionDetails>>>() {
        },expertiseId);

    }

    public List<String> getEmailsOfStaffByStaffIds(Long unitId, List<Long> staffIds) {
        return genericRestClient.publishRequest(staffIds, unitId, RestClientUrlType.UNIT, HttpMethod.POST, STAFF_EMAILS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<String>>>() {
        });
    }

    public List<StaffUnitPositionDetails> getStaffIdAndUnitPositionId(Long unitId, List<Long> staffIds, Long expertiseId) {

        return genericRestClient.publishRequest(staffIds, unitId, RestClientUrlType.UNIT, HttpMethod.POST, STAFF_AND_UNIT_POSITIONS_BY_EXPERTISE_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffUnitPositionDetails>>>() {
        },expertiseId);

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
        return genericRestClient.publishRequest(orgTypeId, null, RestClientUrlType.ORGANIZATION, HttpMethod.POST, ORGANIZATION_TYPE_GET_ORGANIZATION_IDS, null,new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<OrgTypeDTO>>>(){} );
    }

    public List<StaffIdsDTO> getStaffIdsByunitAndAccessGroupId(Long unitId, List<Long> accessGroupId) {
        return genericRestClient.publishRequest(accessGroupId, unitId, RestClientUrlType.UNIT, HttpMethod.POST, ACCESS_GROUP_STAFFS, null,new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffIdsDTO>>>(){});
    }

    public List<StaffDTO> getStaffDetailByIds(Long unitId, Set<Long> staffIds) {
        return genericRestClient.publishRequest(staffIds, unitId, RestClientUrlType.UNIT, HttpMethod.POST, STAFF_DETAILS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffDTO>>>(){});
    }

    public Long getStaffIdByUserId(Long unitId) {
        Long value = genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, USER_STAFF_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Long>>(){});

        if (value == null) {
            exceptionService.dataNotFoundByIdException("message.staff.notFound");
        }
        return value;
    }


    public AccessGroupPermissionCounterDTO getAccessGroupIdsAndCountryAdmin(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET,  STAFF_USER_ACCESS_GROUP,null,new ParameterizedTypeReference<RestTemplateResponseEnvelope<AccessGroupPermissionCounterDTO>>(){});

    }

    public Long removeFunctionFromUnitPositionByDate(Long unitId, Long unitPositionId, Date shiftDate) {
        BasicNameValuePair appliedDate = new BasicNameValuePair("appliedDate", DateUtils.asLocalDate(shiftDate).toString());
        Long functionId = genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.DELETE, APPLY_FUNCTIONS_BY_UNIT_POSITION_ID, Collections.singletonList(appliedDate), new ParameterizedTypeReference<RestTemplateResponseEnvelope<Long>>() {
        }, unitPositionId);
        return functionId;
    }

    public Boolean restoreFunctionFromUnitPositionByDate(Long unitId, Long unitPositionId, Map<Long, Set<LocalDate>> dateAndFunctionIdMap) {

        return genericRestClient.publishRequest(dateAndFunctionIdMap, unitId, RestClientUrlType.UNIT, HttpMethod.POST, REMOVE_FUNCTIONS_BY_UNIT_POSITION_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
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

    public  List<AccessGroupPermissionCounterDTO> getStaffAndAccessGroups(AccessGroupKPIEntry accessGroupKPIEntry) {
        return genericRestClient.publishRequest(Arrays.asList(accessGroupKPIEntry.getAccessGroupId()), accessGroupKPIEntry.getUnitId(), RestClientUrlType.UNIT, HttpMethod.POST, STAFFS_ACCESS_GROUPS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<AccessGroupPermissionCounterDTO>>>() {
        });
    }

    public Long getCountryId(Long refId) {
        return genericRestClient.publishRequest(null, refId, RestClientUrlType.UNIT, HttpMethod.GET, COUNTRY_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Long>>() {
        });
    }
    public Map<Long,Long> getAccessGroupForUnit(Long unitId,  Set<Long> parentAccessGroupIds) {
        return genericRestClient.publishRequest(parentAccessGroupIds, unitId, RestClientUrlType.UNIT, HttpMethod.POST, ACCESS_GROUPS_BY_PARENT, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<Long,Long>>>() {});

    }

    // PriortyGroupRestClient
    public List<StaffUnitPositionQueryResult> getStaffIdsByPriorityGroupIncludeFilter(StaffIncludeFilterDTO staffIncludeFilterDTO, Long unitId) {
        return genericRestClient.publishRequest(staffIncludeFilterDTO, unitId, RestClientUrlType.UNIT, HttpMethod.POST, STAFF_PRIORTY_GROUP, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffUnitPositionQueryResult>>>() {
        });
    }

    //AbsenceTypeRestClient
    //TODO Remove in future because On User MicroService this API is not currently Active
    public Map<String,Object> getAbsenceTypeByName(String title){
        return genericRestClient.publishRequest(null, null, RestClientUrlType.ORGANIZATION, HttpMethod.GET, ABSENCE_TYPES_TITLE, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Map<String, Object>>>() {
        },title);
    }

    //CountryRestClient
    public Object getAllContractType(Long countryId) {
        return genericRestClient.publishRequest(null,countryId,RestClientUrlType.COUNTRY,HttpMethod.GET,CONTRACT_TYPE,null,new ParameterizedTypeReference<RestTemplateResponseEnvelope<Object>>(){});
    }
    public CountryDTO getCountryByOrganizationService(Long organizationServiceId) {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.ORGANIZATION, HttpMethod.GET, COUNTRY_ORGANIZATION_SERVICE_URL, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<CountryDTO>>() {
        },organizationServiceId);
    }
    public OrganizationTypeHierarchyQueryResult getOrgTypesHierarchy(Long countryId, Set<Long> organizationSubTypes) {
        return genericRestClient.publishRequest(organizationSubTypes, countryId, RestClientUrlType.COUNTRY, HttpMethod.POST, ORGANIZATION_TYPES_HIERARCHY, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationTypeHierarchyQueryResult>>() {
        });
    }
    public List<Map<String,Object>> getSkillsByCountryForTaskType(Long countryId) {
        return genericRestClient.publishRequest(null, countryId, RestClientUrlType.COUNTRY, HttpMethod.GET, TASK_TYPES_SKILLS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Map<String, Object>>>>() {
        });
    }
    public List<TimeSlot> getTimeSlotSetsOfCountry(Long countryId) {
        return genericRestClient.publishRequest(null, countryId, RestClientUrlType.COUNTRY, HttpMethod.GET, TIME_SLOTS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<TimeSlot>>>() {
        });
    }

    public CountryDTO getCountryById(long countryId) {
        return genericRestClient.publishRequest(null, countryId, RestClientUrlType.COUNTRY, HttpMethod.GET, null, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<CountryDTO>>() {
        });
    }
    public boolean isCountryExists(long countryId) {
        return genericRestClient.publishRequest(null, countryId, RestClientUrlType.COUNTRY, HttpMethod.GET, null, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        });
    }

    public List<DayType> getDayTypes(List<Long> dayTypes) {
        return genericRestClient.publishRequest(dayTypes, null, RestClientUrlType.ORGANIZATION, HttpMethod.POST, DAY_TYPES, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<DayType>>>() {
        });
    }
}