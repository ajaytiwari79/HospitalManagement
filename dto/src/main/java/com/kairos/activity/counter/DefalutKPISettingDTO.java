package com.kairos.activity.counter;

import java.util.List;
import java.util.Map;

public class DefalutKPISettingDTO {

    private List<Long> orgTypeIds;
    private Long countryId;
    private Map<Long, Long> countryAndOrgAccessGroupIdsMap;

    public DefalutKPISettingDTO() {
    }

    public DefalutKPISettingDTO(List<Long> orgTypeIds, Map<Long, Long> countryAndOrgAccessGroupIdsMap,Long countryId) {
        this.orgTypeIds = orgTypeIds;
        this.countryAndOrgAccessGroupIdsMap = countryAndOrgAccessGroupIdsMap;
        this.countryId=countryId;
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
}
