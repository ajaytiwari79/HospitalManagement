package com.kairos.service.expertise;

import com.kairos.config.env.EnvConfig;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.services.organizationServicesAndLevelQueryResult;
import com.kairos.persistence.model.organization.union.Location;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetailDTO;
import com.kairos.persistence.model.user.expertise.Response.ExpertiseLocationStaffQueryResult;
import com.kairos.persistence.model.user.expertise.Response.ExpertiseQueryResult;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.OrganizationServiceRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.expertise.OrganizationPersonalizeLocationRelationShipGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstants.FORWARD_SLASH;

/**
 * CreatedBy vipulpandey on 19/11/18
 **/
@Service
@Transactional
public class ExpertiseUnitService {
    @Inject
    private ExpertiseGraphRepository expertiseGraphRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private OrganizationServiceRepository organizationServiceRepository;
    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private EnvConfig envConfig;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private OrganizationPersonalizeLocationRelationShipGraphRepository organizationLocationRelationShipGraphRepository;

    public List<ExpertiseQueryResult> findAllExpertise(Long unitId) {
        Organization organization = organizationGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(organization).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.id.notFound", unitId);
        }
        organizationServicesAndLevelQueryResult servicesAndLevel = organizationServiceRepository.getOrganizationServiceIdsByOrganizationId(unitId);
        List<ExpertiseQueryResult> expertise = new ArrayList<>();
        if (Optional.ofNullable(servicesAndLevel).isPresent() && Optional.ofNullable(servicesAndLevel.getLevelId()).isPresent()) {
            expertise = expertiseGraphRepository.findExpertiseByOrganizationServicesAndLevelForUnit(organization.getCountry().getId(), servicesAndLevel.getServicesId(), servicesAndLevel.getLevelId());
        } else if (Optional.ofNullable(servicesAndLevel).isPresent()) {
            expertise = expertiseGraphRepository.findExpertiseByOrganizationServicesForUnit(organization.getCountry().getId(), servicesAndLevel.getServicesId());
        }
        Map<String, Object> response = new HashMap<>();
        List<Long> expertiseIds = expertise.stream().map(ExpertiseQueryResult::getId).collect(Collectors.toList());
        List<ExpertiseLocationStaffQueryResult> locations= organizationLocationRelationShipGraphRepository.getExpertiseWiseLocationInOrganization(expertiseIds,unitId);
        List<ExpertiseLocationStaffQueryResult> staffs=staffGraphRepository.findAllUnionRepresentativeOfExpertiseInUnit(expertiseIds,unitId);

        Map<Long,Map<String,Object>> staffMap= staffs.stream().collect(Collectors.toMap(current->current.getExpertiseId(),v->v.getStaff()));
        Map<Long,Location> locationMap= locations.stream().collect(Collectors.toMap(current->current.getExpertiseId(),v->v.getLocation()));
        expertise.forEach(current->{
            current.setUnionRepresentative(staffMap.get(current.getId()));
            current.setLocation(locationMap.get(current.getId()));
        });
        return expertise;

    }

    public Map<String, Object> getStaffListOfExpertise(Long expertiseId, Long unitId) {
        Map<String, Object> response = new HashMap<>();
        Organization organization = organizationGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(organization).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.id.notFound", unitId);
        }
        if (!organization.isParentOrganization()) {
            organization = organizationGraphRepository.getParentOfOrganization(unitId);
        }
        List<StaffPersonalDetailDTO> staff = staffGraphRepository.getAllStaffByUnitIdAndExpertiseId(organization.getId(), envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath(), expertiseId);
        List<Location> locations = expertiseGraphRepository.findAllLocationsOfUnionInExpertise(expertiseId);
        response.put("staff", staff);
        response.put("locations", locations);
        return response;
    }

    public boolean updateExpertiseAtUnit(Long unitId, Long staffId, Long expertiseId, Long locationId) {
        organizationLocationRelationShipGraphRepository.setLocationInOrganizationForExpertise(expertiseId, unitId, locationId);
        staffGraphRepository.removePreviousUnionRepresentativeOfExpertiseInUnit(unitId, expertiseId);
        staffGraphRepository.assignStaffAsUnionRepresentativeOfExpertise(staffId, expertiseId);
        return true;
    }

}
