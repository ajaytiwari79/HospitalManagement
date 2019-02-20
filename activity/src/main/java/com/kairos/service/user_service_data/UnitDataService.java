package com.kairos.service.user_service_data;

import com.kairos.persistence.model.user_service_data.UnitAndParentOrganizationAndCountryIds;
import com.kairos.persistence.repository.user_service_data.UnitAndParentOrganizationAndCountryIdsMongoRepository;
import com.kairos.dto.user.organization.UnitAndParentOrganizationAndCountryDTO;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.MongoBaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UnitDataService extends MongoBaseService {

    @Inject
    private UserIntegrationService userIntegrationService;

    @Inject
    private UnitAndParentOrganizationAndCountryIdsMongoRepository unitAndParentOrganizationAndCountryIdsMongoRepository;


    public void addParentOrganizationAndCountryIdForUnit(Long unitId, Long parentOrganizationId, Long countryId){
        UnitAndParentOrganizationAndCountryIds unitAndParentOrganizationAndCountryIds = new UnitAndParentOrganizationAndCountryIds(unitId, parentOrganizationId, countryId);
        save(unitAndParentOrganizationAndCountryIds);
    }

    public boolean addParentOrganizationAndCountryIdForAllUnits(){
        List<UnitAndParentOrganizationAndCountryDTO> unitAndParentOrganizationAndCountryData = userIntegrationService.getParentOrganizationAndCountryOfUnits();
        List<UnitAndParentOrganizationAndCountryIds> unitAndParentOrganizationAndCountryIds = new ArrayList<>();
        unitAndParentOrganizationAndCountryData.forEach(unitDetailsDTO -> {
            unitAndParentOrganizationAndCountryIds.add(new UnitAndParentOrganizationAndCountryIds(unitDetailsDTO.getUnitId(),
                    unitDetailsDTO.getParentOrganizationId(), unitDetailsDTO.getCountryId()));
        });
        save(unitAndParentOrganizationAndCountryIds);
        return true;
    }

    public Long getParentOrganizationId(Long unitId){
        UnitAndParentOrganizationAndCountryIds unitAndParentOrganizationAndCountryIds = unitAndParentOrganizationAndCountryIdsMongoRepository.findByUnitId(unitId);
        return unitAndParentOrganizationAndCountryIds.getParentOrganizationId();
    }

    public Long getCountryId(Long unitId){
        UnitAndParentOrganizationAndCountryIds unitAndParentOrganizationAndCountryIds = unitAndParentOrganizationAndCountryIdsMongoRepository.findByUnitId(unitId);
        return unitAndParentOrganizationAndCountryIds.getCountryId();
    }
}