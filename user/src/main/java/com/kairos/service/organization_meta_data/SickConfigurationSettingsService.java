package com.kairos.service.organization_meta_data;

import com.kairos.activity.time_type.TimeTypeDTO;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.default_data.SickConfigurationRepository;
import com.kairos.service.country.TimeTypeRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * CreatedBy vipulpandey on 29/8/18
 **/
@Service
@Transactional
public class SickConfigurationSettingsService {

    private static  final Logger  logger=LoggerFactory.getLogger(SickConfigurationSettingsService.class);
    @Inject private SickConfigurationRepository sickConfigurationRepository;
    @Inject private TimeTypeRestClient timeTypeRestClient;
    @Inject private
    OrganizationGraphRepository organizationGraphRepository;
    public boolean saveSickSettingsOfUnit(Long unitId, Set<BigInteger> allowedTimeTypes){
        sickConfigurationRepository.updateSickConfigurationOfUnit(unitId,allowedTimeTypes);
        return true;
    }
    public Map<String,Object> getSickSettingsOfUnit(Long unitId){

        List<TimeTypeDTO> timeTypes= timeTypeRestClient.getAllTimeTypes(organizationGraphRepository.getCountryId(unitId));
        List<BigInteger> selectedTimeTypeIds=sickConfigurationRepository.findAllSickTimeTypesOfUnit(unitId);
        Map<String,Object> response= new HashMap<>();
        response.put("timeTypes",timeTypes);
        response.put("selectedTimeTypeIds",selectedTimeTypeIds);
        return new HashMap<>();
    }

}
