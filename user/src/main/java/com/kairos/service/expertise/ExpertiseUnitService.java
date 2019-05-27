package com.kairos.service.expertise;

import com.kairos.config.env.EnvConfig;
import com.kairos.persistence.model.organization.Unit;
import com.kairos.persistence.model.organization.services.OrganizationServicesAndLevelQueryResult;
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
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstants.FORWARD_SLASH;
import static com.kairos.constants.UserMessagesConstants.MESSAGE_ORGANIZATION_ID_NOTFOUND;

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
        Unit unit = organizationGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(unit).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_ID_NOTFOUND, unitId);
        }
        Long countryId= unit.isParentOrganization()? unit.getCountry().getId():organizationGraphRepository.getCountryByParentOrganization(unitId).getId();
        OrganizationServicesAndLevelQueryResult servicesAndLevel = organizationServiceRepository.getOrganizationServiceIdsByOrganizationId(unitId);
        List<ExpertiseQueryResult> expertises = new ArrayList<>();
        if (Optional.ofNullable(servicesAndLevel).isPresent() && Optional.ofNullable(servicesAndLevel.getLevelId()).isPresent()) {
            expertises = expertiseGraphRepository.findExpertiseByOrganizationServicesAndLevelForUnit(countryId, servicesAndLevel.getServicesId(), servicesAndLevel.getLevelId());
        } else if (Optional.ofNullable(servicesAndLevel).isPresent()) {
            expertises = expertiseGraphRepository.findExpertiseByOrganizationServicesForUnit(countryId, servicesAndLevel.getServicesId());
        }
        if (CollectionUtils.isNotEmpty(expertises)) {
            List<Long> expertiseIds = expertises.stream().map(ExpertiseQueryResult::getId).collect(Collectors.toList());
            List<ExpertiseLocationStaffQueryResult> locations = organizationLocationRelationShipGraphRepository.getExpertisesLocationInOrganization(expertiseIds, unitId);
            List<ExpertiseLocationStaffQueryResult> staffs = staffGraphRepository.findAllUnionRepresentativeOfExpertiseInUnit(expertiseIds, unitId);

            Map<Long, Map<String, Object>> staffMap = staffs.stream().collect(Collectors.toMap(current -> current.getExpertiseId(), v -> v.getStaff()));
            Map<Long, Location> locationMap = locations.stream().collect(Collectors.toMap(current -> current.getExpertiseId(), v -> v.getLocation()));
            expertises.forEach(current -> {
                current.setUnionRepresentative(staffMap.get(current.getId()));
                current.setUnionLocation(locationMap.get(current.getId()));
            });
        }
        return expertises;


    }

    public Map<String, Object> getStaffListOfExpertise(Long expertiseId, Long unitId) {
        Map<String, Object> response = new HashMap<>();
        Unit unit = organizationGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(unit).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_ID_NOTFOUND, unitId);
        }
        if (!unit.isParentOrganization()) {
            unit = organizationGraphRepository.getParentOfOrganization(unitId);
        }
        List<StaffPersonalDetailDTO> staffs = staffGraphRepository.getAllStaffByUnitIdAndExpertiseId(unit.getId(), envConfig.getServerHost() + FORWARD_SLASH + envConfig.getImagesPath(), expertiseId);
        List<Location> locations = expertiseGraphRepository.findAllLocationsOfUnionInExpertise(expertiseId);

        response.put("staffs", staffs);
        response.put("locations", locations);
        return response;
    }

    public boolean updateExpertiseAtUnit(Long unitId, Long staffId, Long expertiseId, Long locationId) {
        organizationLocationRelationShipGraphRepository.setLocationInOrganizationForExpertise(expertiseId, unitId, locationId);
        staffGraphRepository.removePreviousUnionRepresentativeOfExpertiseInUnit(unitId, expertiseId);
        staffGraphRepository.assignStaffAsUnionRepresentativeOfExpertise(staffId, expertiseId);
        return true;
    }

    public List<ExpertiseQueryResult> findAllExpertiseWithUnits() {
        return expertiseGraphRepository.findAllExpertiseWithUnitIds();
    }

}
