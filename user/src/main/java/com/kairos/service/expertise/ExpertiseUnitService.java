package com.kairos.service.expertise;

import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.services.organizationServicesAndLevelQueryResult;
import com.kairos.persistence.model.user.expertise.Response.ExpertiseQueryResult;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.OrganizationServiceRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * CreatedBy vipulpandey on 19/11/18
 **/
@Service
@Transactional
public class ExpertiseUnitService {
    @Inject
    private ExpertiseGraphRepository expertiseGraphRepository;
    @Inject private OrganizationGraphRepository organizationGraphRepository;
    @Inject private OrganizationServiceRepository organizationServiceRepository;

    public List<ExpertiseQueryResult> findAllExpertise(Long unitId) {
        Organization organization =organizationGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(organization).isPresent()){
            // throw ex
        }
        organizationServicesAndLevelQueryResult servicesAndLevel = organizationServiceRepository.getOrganizationServiceIdsByOrganizationId(unitId);
        List<ExpertiseQueryResult> expertise= new ArrayList<>();
        if (Optional.ofNullable(servicesAndLevel).isPresent() && Optional.ofNullable(servicesAndLevel.getLevelId()).isPresent()) {
            expertise = expertiseGraphRepository.findExpertiseByOrganizationServicesAndLevelForUnit(organization.getCountry().getId(), servicesAndLevel.getServicesId(), servicesAndLevel.getLevelId());
        } else if (Optional.ofNullable(servicesAndLevel).isPresent()) {
            expertise = expertiseGraphRepository.findExpertiseByOrganizationServicesForUnit(organization.getCountry().getId(), servicesAndLevel.getServicesId());
        }
        return expertise;
    }
}
