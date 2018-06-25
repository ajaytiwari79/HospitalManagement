package com.kairos.activity.service.user_service_data;

import com.kairos.activity.client.GenericIntegrationService;
import com.kairos.activity.persistence.model.user_service_data.UnitAndParentOrganizationAndCountryIds;
import com.kairos.activity.persistence.repository.user_service_data.UnitAndParentOrganizationAndCountryIdsMongoRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.response.dto.web.organization.UnitAndParentOrganizationAndCountryDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UnitDataService extends MongoBaseService {

    @Inject
    private GenericIntegrationService genericIntegrationService;

    @Inject
    private UnitAndParentOrganizationAndCountryIdsMongoRepository unitDataRepository;


    public void addParentOrganizationAndCountryIdForUnit(Long unitId, Long parentOrganizationId, Long countryId){
        UnitAndParentOrganizationAndCountryIds unitAndParentOrganizationAndCountryIds = new UnitAndParentOrganizationAndCountryIds(unitId, parentOrganizationId, countryId);
        save(unitAndParentOrganizationAndCountryIds);
    }

    public boolean addParentOrganizationAndCountryIdForAllUnits(){
        List<UnitAndParentOrganizationAndCountryDTO>  unitAndParentOrganizationAndCountryData = genericIntegrationService.getParentOrganizationAndCountryOfUnits();
        List<UnitAndParentOrganizationAndCountryIds> unitAndParentOrganizationAndCountryIds = new ArrayList<>();
        unitAndParentOrganizationAndCountryData.forEach(unitDetailsDTO -> {
            unitAndParentOrganizationAndCountryIds.add(new UnitAndParentOrganizationAndCountryIds(unitDetailsDTO.getUnitId(),
                    unitDetailsDTO.getParentOrganizationId(), unitDetailsDTO.getCountryId()));
        });
        save(unitAndParentOrganizationAndCountryIds);
        return true;
    }

    public Long getParentOrganizationId(Long unitId){
        UnitAndParentOrganizationAndCountryIds unitAndParentOrganizationAndCountryIds = unitDataRepository.findByUnitId(unitId);
        return unitAndParentOrganizationAndCountryIds.getParentOrganizationId();
    }

    public Long getCountryId(Long unitId){
        UnitAndParentOrganizationAndCountryIds unitAndParentOrganizationAndCountryIds = unitDataRepository.findByUnitId(unitId);
        return unitAndParentOrganizationAndCountryIds.getCountryId();
    }
}
