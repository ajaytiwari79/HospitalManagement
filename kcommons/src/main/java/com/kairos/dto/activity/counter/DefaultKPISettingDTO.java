package com.kairos.dto.activity.counter;

import java.util.List;
import java.util.Map;

public class DefaultKPISettingDTO {

    private List<Long> orgTypeIds;
    private Long countryId;
    private Long parentUnitId;
    private Map<Long, Long> countryAndOrgAccessGroupIdsMap;
    private List<Long> staffIds;

    public DefaultKPISettingDTO() {
    }


    public DefaultKPISettingDTO(List<Long> orgTypeIds, Long countryId, Long parentUnitId, Map<Long, Long> countryAndOrgAccessGroupIdsMap) {
        this.orgTypeIds = orgTypeIds;
        this.countryId = countryId;
        this.parentUnitId = parentUnitId;
        this.countryAndOrgAccessGroupIdsMap = countryAndOrgAccessGroupIdsMap;
    }

    public DefaultKPISettingDTO(List<Long> staffIds) {
        this.staffIds = staffIds;
    }

    public List<Long> getOrgTypeIds() {
        return orgTypeIds;
    }

    public void setOrgTypeIds(List<Long> orgTypeIds) {
        this.orgTypeIds = orgTypeIds;
    }

    public Map<Long, Long> getCountryAndOrgAccessGroupIdsMap() {
        return countryAndOrgAccessGroupIdsMap;
    }

    public void setCountryAndOrgAccessGroupIdsMap(Map<Long, Long> countryAndOrgAccessGroupIdsMap) {
        this.countryAndOrgAccessGroupIdsMap = countryAndOrgAccessGroupIdsMap;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Long getParentUnitId() {
        return parentUnitId;
    }

    public void setParentUnitId(Long parentUnitId) {
        this.parentUnitId = parentUnitId;
    }

    public List<Long> getStaffIds() {
        return staffIds;
    }

    public void setStaffIds(List<Long> staffIds) {
        this.staffIds = staffIds;
    }
}
