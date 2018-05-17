package com.kairos.service.organization;

import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.client.dto.gdpr.OrganizationTypeAndServiceRequestDto;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.organization.*;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.repository.organization.OrganizationServiceRepository;
import com.kairos.persistence.repository.organization.OrganizationTypeGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.response.dto.web.UpdateOrganizationTypeDTO;
import com.kairos.response.dto.web.gdpr.OrganizationTypeAndServiceResponseDto;
import com.kairos.response.dto.web.organizationtype_service_dto.OrganizationTypeAndSubTypeResponseDto;
import com.kairos.service.UserBaseService;
import com.kairos.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.kairos.response.dto.web.OrganizationTypeDTO;

import javax.inject.Inject;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by oodles on 18/10/16.
 */
@Transactional
@Service
public class OrganizationTypeService extends UserBaseService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Inject
    OrganizationTypeGraphRepository organizationTypeGraphRepository;
    @Inject
    CountryGraphRepository countryGraphRepository;

    @Inject
    OrganizationServiceRepository organizationServiceRepository;

    public List<OrgTypeLevelWrapper> getOrgTypesByCountryId(Long countryId) {

        return organizationTypeGraphRepository.getOrganizationTypeByCountryId(countryId);
    }

    public OrganizationType createOrganizationTypeForCountry(Long countryId, OrganizationTypeDTO organizationTypeDTO) {
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            throw new DataNotFoundByIdException("Invalid country id " + countryId);
        }
        OrganizationType isAlreadyExist = organizationTypeGraphRepository.findByName(countryId, organizationTypeDTO.getName().trim());
        if (Optional.ofNullable(isAlreadyExist).isPresent()) {
            throw new DuplicateDataException("OrganizationType already exists");
        }
        List<Level> levels = countryGraphRepository.getLevelsByIdsIn(countryId, organizationTypeDTO.getLevels());

        OrganizationType organizationType = new OrganizationType(organizationTypeDTO.getName(), country, levels);
        return prepareResponse(save(organizationType));
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
            save(organizationType);
            return true;
        }
        return false;

    }

    public List<Object> getAllWTAWithOrganization(long countryId) {
        List<Map<String, Object>> map = organizationTypeGraphRepository.getAllWTAWithOrganization(countryId);
        List<Object> objectList = new ArrayList<>();
        for (Map<String, Object> result : map) {
            objectList.add(result.get("result"));
        }
        return objectList;
    }


    public OrganizationType updateOrganizationType(UpdateOrganizationTypeDTO updateOrganizationTypeDTO) {
        OrganizationType orgTypeToUpdate = organizationTypeGraphRepository.findOne(updateOrganizationTypeDTO.getId());
        if (!Optional.ofNullable(orgTypeToUpdate).isPresent()) {
            throw new DataNotFoundByIdException("Invalid organization type id " + updateOrganizationTypeDTO.getId());
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
        return prepareResponse(save(orgTypeToUpdate));
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
     * this method will update the relationship of expertise and organization Type based on parameter {isSelected},if parameter value is true
     * new relationship b/w expertise and organization type will be created or updated(if relationship already exist) if parameter value is false
     * then relationship will be inactive (isEnabled param of relationship will set to false)
     */
    public void addExpertiseInOrgType(long orgTypeId, long expertiseId, boolean isSelected) {
        if (isSelected) {
            if (organizationTypeGraphRepository.orgTypeHasAlreadySkill(orgTypeId, expertiseId) == 0) {
                organizationTypeGraphRepository.addExpertiseInOrgType(orgTypeId, expertiseId, DateUtil.getCurrentDate().getTime(), DateUtil.getCurrentDate().getTime());
            } else {
                organizationTypeGraphRepository.updateOrgTypeExpertise(orgTypeId, expertiseId, DateUtil.getCurrentDate().getTime());
            }
        } else {
            organizationTypeGraphRepository.deleteOrgTypeExpertise(orgTypeId, expertiseId, DateUtil.getCurrentDate().getTime());
        }
    }

    /**
     * to get expertise for particular organization type
     *
     * @param orgTypeId
     * @return
     */
    public List<Map<String, Object>> getExpertise(long countryId, long orgTypeId, String selectedDate) throws ParseException {
        Long selectedDateInLong = (selectedDate != null) ? DateUtil.getIsoDateInLong(selectedDate) : DateUtil.getCurrentDateMillis();
        OrgTypeExpertiseQueryResult orgTypeExpertiseQueryResult = organizationTypeGraphRepository.getExpertiseOfOrganizationType(countryId, orgTypeId, selectedDateInLong);
        return orgTypeExpertiseQueryResult.getExpertise();
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


    //bobby getAllOrganizationTypeAndSubTypesByIds
    public List<OrganizationBasicResponse> getAllOrganizationTypeByIds(Set<Long> orgTypeId) {

        List<OrganizationBasicResponse> organizationTypes = organizationTypeGraphRepository.getAllOrganizationTypeByIds(orgTypeId);
//        List<OrganizationBasicResponse> organizationServices=organizationServiceRepository.getAllOrganizationServicesByIds(orgTypeId7);
        return organizationTypes;
    }


    public List<OrganizationTypeAndSubTypeResponseDto> getAllOrganizationTypeAndServiceAndSubServices(Long countryId) {
        List<Map> organizationType = organizationTypeGraphRepository.getAllOrganizationTypeAndServiceAndSubServices(countryId);
        List<OrganizationTypeAndSubTypeResponseDto> list = new ArrayList<>();
        organizationType.forEach(o -> {
            list.add(ObjectMapperUtils.copyPropertiesByMapper(o.get("organizationType"), OrganizationTypeAndSubTypeResponseDto.class));
        });
        return list;
    }


    public OrganizationTypeAndServiceResponseDto organizationTypesAndServicesAndSubTypes(OrganizationTypeAndServiceRequestDto requestDto) {
        OrganizationTypeAndServiceResponseDto responseDtoResult = new OrganizationTypeAndServiceResponseDto();
        responseDtoResult.setOrganizationTypes(organizationTypeGraphRepository.getAllOrganizationTypeByIds(requestDto.getOrganizationTypeIds()));
        responseDtoResult.setOrganizationSubTypes(organizationTypeGraphRepository.getAllOrganizationTypeByIds(requestDto.getOrganizationSubTypeIds()));
        responseDtoResult.setOrganizationServices(organizationServiceRepository.getAllOrganizationServicesByIds(requestDto.getOrganizationServiceIds()));
        responseDtoResult.setOrganizationSubServices(organizationServiceRepository.getAllOrganizationServicesByIds(requestDto.getOrganizationSubServiceIds()));
        return responseDtoResult;

    }

}
