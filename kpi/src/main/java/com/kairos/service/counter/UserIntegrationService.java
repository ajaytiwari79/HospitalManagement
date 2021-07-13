package com.kairos.service.counter;

import com.kairos.dto.activity.counter.distribution.access_group.AccessGroupPermissionCounterDTO;
import com.kairos.dto.activity.counter.distribution.access_group.StaffIdsDTO;
import com.kairos.dto.activity.counter.distribution.org_type.OrgTypeDTO;
import com.kairos.dto.activity.kpi.DefaultKpiDataDTO;
import com.kairos.dto.activity.kpi.StaffEmploymentTypeDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.user.access_page.KPIAccessPageDTO;
import com.kairos.dto.user.team.TeamDTO;
import com.kairos.persistence.model.staff.personal_details.StaffDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserIntegrationService {
    public DefaultKpiDataDTO getKpiFilterDefaultData(Long unitId) {
        return null;
    }
    public List<TeamDTO> getTeamByUnitId(Long unitId) {
    }
    public AccessGroupPermissionCounterDTO getAccessGroupIdsAndCountryAdmin(Long lastSelectedOrganizationId) {
        return null;
    }
    public List<Long> getUnitIds(Long countryId) {
        return null;
    }
    public List<OrgTypeDTO> getOrganizationIdsBySubOrgId(List<Long> orgTypeIds) {
        return null;
    }
    public List<StaffIdsDTO> getStaffIdsByunitAndAccessGroupId(Long refId, List<Long> accessGroupIds) {
        return null;
    }
    public Long getCountryId(Long refId) {
        return null;
    }
    public List<KPIAccessPageDTO> getKPIEnabledTabsForModuleForUnit(Long refId) {
        return null;
    }
    public List<StaffKpiFilterDTO> getStaffsByFilter(StaffEmploymentTypeDTO staffEmploymentTypeDTO) {
        return null;
    }
    public DefaultKpiDataDTO getKpiAllDefaultData(Long countryId, StaffEmploymentTypeDTO staffEmploymentTypeDTO) {
        return null;
    }
    public DefaultKpiDataDTO getKpiAllDefaultData(StaffEmploymentTypeDTO staffEmploymentTypeDTO) {
        return null;
    }
    public List<StaffDTO> getStaffListByUnit() {
        return null;
    }
    public boolean isCountryExists(Long referenceId) {
        return false;
    }
    public boolean isExistOrganization(Long referenceId) {
        return false;
    }
}
