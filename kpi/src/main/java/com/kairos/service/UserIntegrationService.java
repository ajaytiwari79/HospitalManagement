package com.kairos.service;

import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.dto.activity.counter.distribution.access_group.AccessGroupPermissionCounterDTO;
import com.kairos.dto.activity.counter.distribution.access_group.StaffIdsDTO;
import com.kairos.dto.activity.counter.distribution.org_type.OrgTypeDTO;
import com.kairos.dto.activity.kpi.DefaultKpiDataDTO;
import com.kairos.dto.activity.kpi.StaffEmploymentTypeDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.shift.SelfRosteringFilterDTO;
import com.kairos.dto.activity.shift.ShiftFilterDefaultData;
import com.kairos.dto.user.access_page.KPIAccessPageDTO;
import com.kairos.dto.user.country.basic_details.CountryDTO;
import com.kairos.dto.user.organization.OrganizationDTO;
import com.kairos.dto.user.team.TeamDTO;
import com.kairos.enums.rest_client.RestClientUrlType;
import com.kairos.persistence.model.AccessGroupKPIEntry;
import com.kairos.persistence.model.ExceptionService;
import com.kairos.persistence.model.staff.personal_details.StaffDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static com.kairos.constants.ApiConstants.*;
import static com.kairos.constants.KPIMessagesConstants.MESSAGE_STAFF_NOT_FOUND_BY_USER;

@Service
public class UserIntegrationService {

    @Inject private GenericRestClient genericRestClient;
    @Inject private ExceptionService exceptionService;

    public DefaultKpiDataDTO getKpiFilterDefaultData(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT_WITHOUT_PARENT_ORG, HttpMethod.GET, KPI_FILTER_DEFAULT_DATA, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<DefaultKpiDataDTO>>() {
        });
    }

    public List<TeamDTO> getTeamByUnitId(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, TEAMS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<TeamDTO>>>() {
        });
    }

    public AccessGroupPermissionCounterDTO getAccessGroupIdsAndCountryAdmin(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, STAFF_USER_ACCESS_GROUP, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<AccessGroupPermissionCounterDTO>>() {
        });

    }

    public List<Long> getUnitIds(Long countryId) {
        return genericRestClient.publishRequest(null, countryId, RestClientUrlType.COUNTRY, HttpMethod.GET, "/get_all_units", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<Long>>>() {
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

    public Long getCountryId(Long refId) {
        return genericRestClient.publishRequest(null, refId, RestClientUrlType.UNIT, HttpMethod.GET, COUNTRY_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Long>>() {
        });
    }

    public List<KPIAccessPageDTO> getKPIEnabledTabsForModuleForUnit(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, KPI_DETAILS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<KPIAccessPageDTO>>>() {
        });
    }

    public List<StaffKpiFilterDTO> getStaffsByFilter(StaffEmploymentTypeDTO staffEmploymentTypeDTO) {
        return genericRestClient.publishRequest(staffEmploymentTypeDTO, null, RestClientUrlType.COUNTRY, HttpMethod.POST, STAFF_BY_KPI_FILTER, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffKpiFilterDTO>>>() {
        });
    }

    public DefaultKpiDataDTO getKpiAllDefaultData(Long countryId, StaffEmploymentTypeDTO staffEmploymentTypeDTO) {
        return genericRestClient.publishRequest(staffEmploymentTypeDTO, countryId, RestClientUrlType.COUNTRY, HttpMethod.POST, KPI_ALL_DEFAULT_DATA, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<DefaultKpiDataDTO>>() {
        });
    }

    public DefaultKpiDataDTO getKpiAllDefaultData(StaffEmploymentTypeDTO staffEmploymentTypeDTO) {
        return genericRestClient.publishRequest(staffEmploymentTypeDTO, null, RestClientUrlType.COUNTRY, HttpMethod.POST, KPI_DEFAULT_DATA, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<DefaultKpiDataDTO>>() {
        });
    }

    public List<StaffDTO> getStaffListByUnit() {
        return genericRestClient.publishRequest(null, null, RestClientUrlType.UNIT, HttpMethod.GET, STAFF_GET_STAFF_BY_UNIT, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<StaffDTO>>>() {
        });
    }

    public boolean isCountryExists(Long countryId) {
        return genericRestClient.publishRequest(null, countryId, RestClientUrlType.COUNTRY, HttpMethod.GET, "", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<CountryDTO>>() {
        }) != null;
    }

    public boolean isExistOrganization(Long orgId) {
        return getOrganizationDTO(orgId)!=null;
    }
    public OrganizationDTO getOrganizationDTO(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, "", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrganizationDTO>>() {
        });
    }


    public Long getStaffIdByUserId(Long unitId) {
        Long value = genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, USER_STAFF_ID, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Long>>() {
        });
        if (value == null) exceptionService.dataNotFoundByIdException(MESSAGE_STAFF_NOT_FOUND_BY_USER);
        return value;
    }

    public ShiftFilterDefaultData getShiftFilterDefaultData(SelfRosteringFilterDTO selfRosteringFilterDTO) {
        return genericRestClient.publishRequest(selfRosteringFilterDTO, selfRosteringFilterDTO.getUnitId(), RestClientUrlType.UNIT, HttpMethod.POST, "/get_filter_data", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<ShiftFilterDefaultData>>() {
        });
    }
    public List<AccessGroupPermissionCounterDTO> getStaffAndAccessGroups(AccessGroupKPIEntry accessGroupKPIEntry) {
        return genericRestClient.publishRequest(Arrays.asList(accessGroupKPIEntry.getAccessGroupId()), accessGroupKPIEntry.getUnitId(), RestClientUrlType.UNIT, HttpMethod.POST, STAFFS_ACCESS_GROUPS, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<AccessGroupPermissionCounterDTO>>>() {
        });
    }
}
