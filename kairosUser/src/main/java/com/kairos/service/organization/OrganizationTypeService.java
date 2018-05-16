package com.kairos.service.organization;

import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.organization.*;
import com.kairos.persistence.model.organization.OrganizationService;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.repository.organization.OrganizationTypeGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.response.dto.web.UpdateOrganizationTypeDTO;
import com.kairos.response.dto.web.organizationtype_service_dto.OrganizationServiceResponseDto;
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


    //bobby getAllOrganizationTypeByIds
    public Map<Long,OrganizationType> getAllOrganizationTypeByIds(Set<Long> orgTypeId) {

        List<OrganizationType> organizationTypes=organizationTypeGraphRepository.getAllOrganizationTypeByIds(orgTypeId);
        Map<Long,OrganizationType> gdprOrgTypeAndSubTypes = organizationTypes.stream()
                .collect(Collectors.toMap(OrganizationType::getId, OrganizationType::basicDetails));
return gdprOrgTypeAndSubTypes;
    }

    /*public List<OrganizationType> getAllOrganizationSubTypeByIds(Set<Long> orgTypeId) {
        return organizationTypeGraphRepository.getAllOrganizationSubTypeByIds(orgTypeId);
    }
*/

  /*  //bobby organikzation type and service list and sub service
    public List<OrganizationTypeAndSubTypeResponseDto> getOrgTypeAndOrgServicesResponseDto() {

        List<OrganizationTypeAndSubTypeResponseDto> OrganizationTypeAndSubTypeResponseDto = new ArrayList<>();

        List<OrganizationType> organizationTypes = organizationTypes();
        ListIterator<OrganizationType> iterator = organizationTypes.listIterator();
        while (iterator.hasNext()) {
            OrganizationType organizationType=iterator.next();
            OrganizationTypeAndSubTypeResponseDto orgTypeAndServiceDto = new OrganizationTypeAndSubTypeResponseDto();
            Long orgId = organizationType.getId();
            orgTypeAndServiceDto.setId(orgId);
            orgTypeAndServiceDto.setName(organizationType.getName());
            List<Long> orgSubTypeIds = organizationTypeGraphRepository.getAllOrganizationSubTypeIds(organizationType.getId());

            List<OrganizationTypeAndSubTypeResponseDto> organizationSubTypeList = new ArrayList<>();
            for (Long orgSubTypeid : orgSubTypeIds) {
                System.err.println("++++++orgsubType +++++++++++++");

                OrganizationType orgSubType = organizationTypeGraphRepository.getOrganizationSubTypeById(orgId, orgSubTypeid);
                System.err.println("++++++orgsubType +++++++++++++"+orgSubType.getName());
                OrganizationTypeAndSubTypeResponseDto orgTypeSubType = new OrganizationTypeAndSubTypeResponseDto();
                orgTypeSubType.setId(orgSubType.getId());
                orgTypeSubType.setName(orgSubType.getName());
                List<OrganizationService> organizationServiceList = organizationTypeGraphRepository.getOrganizationServiceListOnOrganizationSubType(orgSubType.getId());
                if (organizationServiceList.size() != 0) {
                    System.err.println("++++++organizationServiceList +++++++++++++"+organizationServiceList);
                    List<OrganizationServiceResponseDto> OrgServiceListByOrgtanizationSubType = getOrgServiceListByOrgtanizationSubType(organizationServiceList);
                    orgTypeSubType.setOrganizationServiceList(OrgServiceListByOrgtanizationSubType);
                }
                organizationSubTypeList.add(orgTypeSubType);

            }
            orgTypeAndServiceDto.setOrganizationSubType(organizationSubTypeList);
            OrganizationTypeAndSubTypeResponseDto.add(orgTypeAndServiceDto);

        }
        return OrganizationTypeAndSubTypeResponseDto;

    }


    public List<OrganizationServiceResponseDto> getOrgServiceListByOrgtanizationSubType(List<OrganizationService> organizationServiceList) {
        List<OrganizationServiceResponseDto> organiazationServiceList = new ArrayList<>();
        ListIterator<OrganizationService> iterator = organizationServiceList.listIterator();
        while (iterator.hasNext()) {
            System.err.println("++++++serviceList+++++++++++++");
            OrganizationService  organizationService=(OrganizationService)iterator.next();
            OrganizationServiceResponseDto organizationServiceResponseDto = new OrganizationServiceResponseDto();
            if (organizationService!=null) {
                organizationServiceResponseDto.setId(organizationService.getId());
            organizationServiceResponseDto.setName(organizationService.getName());
               *//* OrganizationServiceResponseDto organizationSubServiceResponseDto = new OrganizationServiceResponseDto();
                organizationSubServiceResponseDto.setId(iterator.next().getId());
                organizationSubServiceResponseDto.setName(iterator.next().getName());
*//*
            }
            organiazationServiceList.add(organizationServiceResponseDto);
        }

        return organiazationServiceList;
    }



    public List<OrganizationType> organizationTypes() {
        return organizationTypeGraphRepository.getAllOrganizationTypeByIds();
    }
*/

    public List<OrganizationTypeAndSubTypeResponseDto> getAllOrganizationTypeAndServiceAndSubServices(Long countryId)
    {
        List<Map> organizationType = organizationTypeGraphRepository.getAllOrganizationTypeAndServiceAndSubServices(countryId);
        List<OrganizationTypeAndSubTypeResponseDto> list = new ArrayList<>();
        organizationType.forEach(o->{
            list.add(ObjectMapperUtils.copyPropertiesByMapper(o.get("organizationType"),OrganizationTypeAndSubTypeResponseDto.class));
        });
        return list;
    }


}
