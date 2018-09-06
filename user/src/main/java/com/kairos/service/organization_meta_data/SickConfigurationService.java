package com.kairos.service.organization_meta_data;

import com.kairos.activity.time_type.TimeTypeDTO;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.default_data.SickConfiguration;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.default_data.SickConfigurationRepository;
import com.kairos.service.country.TimeTypeRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

/**
 * CreatedBy vipulpandey on 29/8/18
 **/
@Service
@Transactional
public class SickConfigurationService {

    private static final Logger logger = LoggerFactory.getLogger(SickConfigurationService.class);

    @Inject
    private SickConfigurationRepository sickConfigurationRepository;
    @Inject
    private TimeTypeRestClient timeTypeRestClient;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;

    public boolean saveSickSettingsOfUnit(Long unitId, Set<BigInteger> allowedTimeTypes) {
        SickConfiguration sickConfiguration = sickConfigurationRepository.findSickConfigurationOfUnit(unitId);
        if (!Optional.ofNullable(sickConfiguration).isPresent()) {
            Organization organization = organizationGraphRepository.findOne(unitId, 0);
            sickConfiguration = new SickConfiguration(allowedTimeTypes, organization);

        } else {
            sickConfiguration.setTimeTypes(allowedTimeTypes);
        }
        sickConfigurationRepository.save(sickConfiguration);
        return true;
    }

    public Set<BigInteger> getSickSettingsOfUnit(Long unitId) {
        SickConfiguration sickConfiguration = sickConfigurationRepository.findSickConfigurationOfUnit(unitId);
        return sickConfiguration != null ? sickConfiguration.getTimeTypes() : Collections.emptySet();
    }

    public Map<String, Object> getSickSettingsAndDefaultDataOfUnit(Long unitId) {
        List<TimeTypeDTO> timeTypes = timeTypeRestClient.getAllTimeTypes(organizationGraphRepository.getCountryId(unitId));
        Map<String, Object> response = new HashMap<>();
        response.put("timeTypes", timeTypes);
        response.put("selectedTimeTypeIds", getSickSettingsOfUnit(unitId));
        return response;
    }

}
