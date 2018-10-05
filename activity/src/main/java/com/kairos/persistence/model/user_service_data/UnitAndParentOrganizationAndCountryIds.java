package com.kairos.persistence.model.user_service_data;

import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.index.Indexed;

@Deprecated
public class UnitAndParentOrganizationAndCountryIds extends MongoBaseEntity {
    private Long unitId;
    private Long parentOrganizationId;
    private Long countryId;

    public UnitAndParentOrganizationAndCountryIds(){
        // default constructor
    }

    public UnitAndParentOrganizationAndCountryIds(Long unitId, Long parentOrganizationId, Long countryId){
        this.unitId = unitId;
        this.parentOrganizationId = parentOrganizationId;
        this.countryId = countryId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getParentOrganizationId() {
        return parentOrganizationId;
    }

    public void setParentOrganizationId(Long parentOrganizationId) {
        this.parentOrganizationId = parentOrganizationId;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }
}
