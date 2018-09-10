package com.kairos.service.organization;

import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.organization.*;
import com.kairos.persistence.model.organization_type.OrgTypeSkillQueryResult;
import com.kairos.persistence.model.user.open_shift.OrganizationTypeAndSubType;
import com.kairos.persistence.repository.organization.OrganizationTypeGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.dto.user.organization.OrganizationTypeDTO;
import com.kairos.utils.DateUtil;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.wrapper.OrganizationTypeAndSubTypeDto;
import com.kairos.wrapper.UpdateOrganizationTypeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by oodles on 18/10/16.
 */
@Transactional
@Service
public class OrganizationTypeService{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Inject
    OrganizationTypeGraphRepository organizationTypeGraphRepository;
    @Inject
    CountryGraphRepository countryGraphRepository;
    @Inject
    private ExceptionService exceptionService;

    public List<OrgTypeLevelWrapper> getOrgTypesByCountryId(Long countryId) {

        return organizationTypeGraphRepository.getOrganizationTypeByCountryId(countryId);
    }

    public OrganizationType createOrganizationTypeForCountry(Long countryId, OrganizationTypeDTO organizationTypeDTO) {
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound", countryId);

        }
        OrganizationType isAlreadyExist = organizationTypeGraphRepository.findByName(countryId, organizationTypeDTO.getName().trim());
        if (Optional.ofNullable(isAlreadyExist).isPresent()) {
            exceptionService.duplicateDataException("message.organizationtype.name.duplicate");

        }
        List<Level> levels = countryGraphRepository.getLevelsByIdsIn(countryId, organizationTypeDTO.getLevels());

        OrganizationType organizationType = new OrganizationType(organizationTypeDTO.getName(), country, levels);
        return prepareResponse(organizationTypeGraphRepository.save(organizationType));
    }


    public OrganizationType getOrganizationTypeById(Long organizationTypeId) {
        return organizationTypeGraphRepository.findOne(organizationTypeId);

    }

    public OrganizationType createOrganizationType(OrganizationType organizationType) {
        return organizationTypeGraphRepository.save(organizationType);
    }


    public List<OrganizationType> getAllOrganizationTypes() {
        return organizationTypeGraphRepository.findAll();
    }

    public boolean deleteOrganizationType(Long organizationTypeId) {
        OrganizationType organizationType = organizationTypeGraphRepository.findOne(organizationTypeId);
        if (organizationType != null) {
            organizationType.setEnable(false);
            organizationTypeGraphRepository.save(organizationType);
            return true;
        }
        return false;

    }

    public List<OrganizationTypeAndSubType> getAllOrganizationTypeAndSubType(long countryId) {
        return organizationTypeGraphRepository.getAllOrganizationTypeAndSubType(countryId);
    }


    public OrganizationType updateOrganizationType(UpdateOrganizationTypeDTO updateOrganizationTypeDTO) {
        OrganizationType orgTypeToUpdate = organizationTypeGraphRepository.findOne(updateOrganizationTypeDTO.getId());
        if (!Optional.ofNullable(orgTypeToUpdate).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organizationtype.id.notfound", updateOrganizationTypeDTO.getId());

        }
        if (!updateOrganizationTypeDTO.getLevelsToDelete().isEmpty()) {
            organizationTypeGraphRepository.removeLevelRelationshipFromOrganizationType(updateOrganizationTypeDTO.getId(), updateOrganizationTypeDTO.getLevelsToDelete());
            orgTypeToUpdate.setLevels(null);
        }
        if (!updateOrganizationTypeDTO.getLevelsToUpdate().isEmpty()) {
            List<Level> levels = countryGraphRepository.getLevelsByIdsIn(orgTypeToUpdate.getCountry().getId(), updateOrganizationTypeDTO.getLevelsToUpdate());
            orgTypeToUpdate.setLevels(levels);
        }
        orgTypeToUpdate.setName(updateOrganizationTypeDTO.getName().trim());
        return prepareResponse(organizationTypeGraphRepository.save(orgTypeToUpdate));
    }

    private OrganizationType prepareResponse(OrganizationType organizationType) {
        OrganizationType response = new OrganizationType();
        response.setId(organizationType.getId());
        response.setName(organizationType.getName());
        List<Level> activeLevels = organizationType.getLevels().parallelStream().filter(level -> !level.isDeleted()).collect(Collectors.toList());
        response.setLevels(activeLevels);
        return response;
    }

    public Map<String, Object> addOrganizationTypeSubType(OrganizationType organizationType, Long organizationTypeId) {
        OrganizationType type = organizationTypeGraphRepository.findOne(organizationTypeId);
        if (type != null) {
            organizationType = organizationTypeGraphRepository.save(organizationType);
            organizationTypeGraphRepository.createSubTypeRelation(organizationType.getId(), organizationTypeId);
            return organizationType.retrieveDetails();
        }
        return null;
    }

    public List<Object> getOrgSubTypesByTypeId(Long organizationTypeId) {
        List<Map<String, Object>> queryResponse = organizationTypeGraphRepository.getOrganizationSubTypeByTypeId(organizationTypeId);
        if (!queryResponse.isEmpty()) {
            List<Object> response = new ArrayList<>();
            for (Map<String, Object> map : queryResponse) {
                Object o = map.get("result");
                response.add(o);
            }
            return response;
        }
        return null;
    }

    /**
     * @param expertiseId
     * @param orgTypeId
     * @param isSelected
     * @author prabjot
     * this method will update the relationship of skill and organization Type based on parameter {isSelected},if parameter value is true
     * new relationship b/w skill and organization type will be created or updated(if relationship already exist) if parameter value is false
     * then relationship will be inactive (deleted param of relationship will set to true)
     */
    public List<OrgTypeSkillQueryResult> addExpertiseInOrgType(long orgTypeId, long expertiseId, boolean isSelected) {
        if (isSelected) {
            organizationTypeGraphRepository.addSkillInOrgType(orgTypeId, expertiseId, DateUtil.getCurrentDateMillis(), DateUtil.getCurrentDateMillis());
        } else {
            organizationTypeGraphRepository.deleteSkillFromOrgType(orgTypeId, expertiseId, DateUtil.getCurrentDateMillis());
        }

        // TODO remove As per request of FE its added for now
        List<OrgTypeSkillQueryResult> orgTypeSkillQueryResult = organizationTypeGraphRepository.getSkillsOfOrganizationType(orgTypeId);
        return orgTypeSkillQueryResult;
    }

    /**
     * to get skills for particular organization type
     *
     * @param orgTypeId
     * @return
     */
    public List<OrgTypeSkillQueryResult> getSkillsByOrganizationTypeId(long countryId, long orgTypeId) {
        List<OrgTypeSkillQueryResult> orgTypeSkillQueryResult = organizationTypeGraphRepository.getSkillsOfOrganizationType(orgTypeId);
        return orgTypeSkillQueryResult;
    }

    public OrganizationTypeHierarchyQueryResult getOrganizationTypeHierarchy(long countryId, Set<Long> orgSubServiceId) {
        return organizationTypeGraphRepository.getOrganizationTypeHierarchy(countryId, orgSubServiceId);
    }

    public List<Organization> getOrganizationByOrganizationTypeId(long organizationTypeId) {
        return organizationTypeGraphRepository.getOrganizationsByOrganizationType(organizationTypeId);
    }

    public void linkOrganizationTypeWithService(Set<Long> orgTypeId, long serviceId) {
        organizationTypeGraphRepository.linkOrganizationTypeWithService(orgTypeId, serviceId);
    }

    public void deleteLinkingOfOrganizationTypeAndService(Set<Long> orgTypeId, long serviceId) {
        organizationTypeGraphRepository.deleteRelOrganizationTypeWithService(orgTypeId, serviceId);
    }


    public List<OrganizationTypeAndSubTypeDto> getAllOrganizationTypeAndServiceAndSubServices(Long countryId) {
        List<Map> organizationType = organizationTypeGraphRepository.getAllOrganizationTypeAndServiceAndSubServices(countryId);
        List<OrganizationTypeAndSubTypeDto> list = new ArrayList<>();
        organizationType.forEach(o -> {
            list.add(ObjectMapperUtils.copyPropertiesByMapper(o.get("organizationType"), OrganizationTypeAndSubTypeDto.class));
        });
        return list;
    }


}
