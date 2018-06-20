package com.kairos.activity.service.user_service_data;

import com.kairos.activity.persistence.model.user_service_data.UnitAndParentOrganizationAndCountryIds;
import com.kairos.activity.service.MongoBaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UnitDataService extends MongoBaseService {

    public void addParentOrganizationAndCountryIdForUnit(Long unitId, Long parentOrganizationId, Long countryId){
        UnitAndParentOrganizationAndCountryIds unitAndParentOrganizationAndCountryIds = new UnitAndParentOrganizationAndCountryIds(unitId, parentOrganizationId, countryId);
        save(unitAndParentOrganizationAndCountryIds);
    }

}
