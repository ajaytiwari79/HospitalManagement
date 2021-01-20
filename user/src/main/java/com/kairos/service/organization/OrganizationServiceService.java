package com.kairos.service.organization;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.CommonsExceptionUtil;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.user.organization.OrganizationServiceDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.organization.OrganizationExternalServiceRelationship;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.organization.Unit;
import com.kairos.persistence.model.organization.services.OrganizationService;
import com.kairos.persistence.model.organization.services.OrganizationServiceQueryResult;
import com.kairos.persistence.repository.organization.*;
import com.kairos.persistence.repository.system_setting.SystemLanguageGraphRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.GdprIntegrationService;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.neo4j.util.IterableUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.constants.UserMessagesConstants.*;

/**
 * Created by prabjot on 16/9/16.
 */
@Transactional
@Service
public class OrganizationServiceService {

    public static final String RESULT = "result";
    public static final String AVAILABLE_SERVICES = "availableServices";
    public static final String SELECTED_SERVICES = "selectedServices";
    @Inject
    private OrganizationTypeGraphRepository organizationTypeGraphRepository;
    @Inject
    private OrganizationServiceRepository organizationServiceRepository;
    @Inject
    private UnitGraphRepository unitGraphRepository;
    @Inject
    private OrganizationBaseRepository organizationBaseRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    //TODO move this dependency in task
    @Inject
    private OrganizationExternalServiceRelationshipRepository organizationExternalServiceRelationshipRepository;
    @Inject
    private TeamGraphRepository teamGraphRepository;

    @Inject
    private ExceptionService exceptionService;
    @Inject
    private GdprIntegrationService gdprIntegrationService;
    @Inject
    private UserGraphRepository userGraphRepository;
    @Inject
    private SystemLanguageGraphRepository systemLanguageGraphRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationServiceService.class);


    @CacheEvict(value = "findAllActivityByCountry", key = "#countryId")
    public Map<String, Object> updateOrganizationService(long id, String name, String description, Long countryId) {
        if (isNull(name) || name.trim().isEmpty()) {
            exceptionService.actionNotPermittedException(ERROR_ORGANIZATIONSERVICE_NAME_NOTEMPTY);
        }
        OrganizationService organizationService = organizationServiceRepository.findOne(id);
        if (organizationService == null) {
            return null;
        }
        boolean alreadyExistWithSameName = organizationServiceRepository.checkDuplicateService(countryId, "(?i)" + name.trim(), id);
        if (alreadyExistWithSameName) {
            exceptionService.duplicateDataException(MESSAGE_ORGANIZATIONSERVICE_SERVICE_DUPLICATE);
        }
        organizationService.setName(name);
        organizationService.setDescription(description);
        organizationServiceRepository.save(organizationService);
        return organizationService.retrieveDetails();
    }

    public OrganizationService getOrganizationServiceById(Long id) {
        return organizationServiceRepository.findOne(id);
    }

    //@Cacheable(value = "getAllOrganizationService", key = "#countryId", cacheManager = "cacheManager")
    public Iterable<OrganizationService> getAllOrganizationService(Long countryId) {
        Set<Long> serviceIds=organizationServiceRepository.getOrganizationServicesIdsByCountryId(countryId);
        return organizationServiceRepository.findAllById(serviceIds);
     }

    @CacheEvict(value = "findAllActivityByCountry", allEntries = true)
    public boolean deleteOrganizationServiceById(Long id) {
        OrganizationService organizationService = organizationServiceRepository.findOne(id);
        if (organizationService == null) {
            return false;
        }
        organizationService.setEnabled(false);
        organizationServiceRepository.save(organizationService);
        return true;
    }

    public OrganizationService addSubService(final long serviceId, OrganizationService subService) {
        OrganizationService organizationService = organizationServiceRepository.findOne(serviceId);
        if (organizationService == null) {
            return null;
        }

        String name = "(?i)" + subService.getName();
        if (organizationServiceRepository.checkDuplicateSubService(organizationService.getId(), name) != null) {
            LOGGER.info("Can't create duplicate sub service in same category");
            return null;
        }

        List<OrganizationService> subServices = organizationService.getOrganizationSubService();
        if (subServices != null) {
            subServices.add(subService);
        } else {
            subServices = new ArrayList<>();
            subServices.add(subService);
        }
        organizationService.setOrganizationSubService(subServices);
        organizationServiceRepository.save(organizationService);
        Map<String, Object> response = new HashMap<>(2);
        response.put("id", subService.getId());
        response.put("name", subService.getName());
        response.put("description", subService.getDescription());
        LOGGER.info("Sending Response: {}" , response);

        return subService;

    }

    @CacheEvict(value = "findAllActivityByCountry", allEntries = true)
    public Map<String, Object> addCountrySubService(final long serviceId, OrganizationService subService) {
        OrganizationService organizationService = organizationServiceRepository.findById(serviceId).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_ORGANIZATIONSERVICE_ID_NOTFOUND)));
        String name = "(?i)" + subService.getName();
        if (organizationServiceRepository.checkDuplicateSubService(organizationService.getId(), name) != null) {
            exceptionService.duplicateDataException(MESSAGE_ORGANIZATIONSERVICE_SUBSERVICE_DUPLICATED);

        }

        List<OrganizationService> subServicesList = organizationService.getOrganizationSubService();

        if (subServicesList != null) {
            subServicesList.add(subService);
        } else {
            subServicesList = new ArrayList<>();
            subServicesList.add(subService);
        }
        organizationService.setOrganizationSubService(subServicesList);
        organizationServiceRepository.save(organizationService);
        return subService.retrieveDetails();

    }

    @CacheEvict(value = "findAllActivityByCountry", allEntries = true)
    public OrganizationServiceQueryResult updateCustomNameOfService(long serviceId, long organizationId, String customName) {

        return unitGraphRepository.addCustomNameOfServiceForOrganization(serviceId, organizationId, customName);


    }

    @CacheEvict(value = "findAllActivityByCountry", allEntries = true)
    public OrganizationServiceQueryResult updateCustomNameOfSubService(long subServiceId, long organizationId, String customName) {
            return teamGraphRepository.addCustomNameOfSubServiceForTeam(organizationId, subServiceId, customName);
    }

    @CacheEvict(value = "findAllActivityByCountry", allEntries = true)
    private Boolean addDefaultCustomNameRelationShipOfServiceForOrganization(long subOrganizationServiceId, long organizationId) {
        return unitGraphRepository.addCustomNameOfServiceForOrganization(subOrganizationServiceId, organizationId);
    }


    public Map<String, Object> updateServiceToOrganization(Long id, Long organizationServiceId, boolean isSelected) {
        Unit unit=unitGraphRepository.findById(id).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_DATANOTFOUND,"Unit",id)));
        OrganizationService organizationService = organizationServiceRepository.findById(organizationServiceId).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_ORGANIZATIONSERVICE_ID_NOTFOUND)));
        if (isSelected) {
            LOGGER.info("check if already exist-------> ");
            if (unitGraphRepository.isServiceAlreadyExist(id, organizationService.getId()) == 0) {
                unitGraphRepository.addOrganizationServiceInUnit(id, Arrays.asList(organizationService.getId()), DateUtils.getDate().getTime(), DateUtils.getDate().getTime());
            } else {
                unitGraphRepository.updateServiceFromOrganization(id, organizationService.getId());
            }
            addDefaultCustomNameRelationShipOfServiceForOrganization(organizationService.getId(), id);
            //call to create asset for org.
            List<Long> orgSubTypeIds = unit.getOrganizationSubTypes().stream().map(UserBaseEntity::getId).collect(Collectors.toList());
            gdprIntegrationService.createDefaultAssetForUnit(UserContext.getUserDetails().getCountryId(),unit.getId(), orgSubTypeIds,organizationServiceId);
        } else {
            unitGraphRepository.removeServiceFromOrganization(id, organizationService.getId());
        }
        return organizationServiceData(id);
    }


    public List<OrganizationServiceDTO> getOrgServicesByOrgType(long orgType) {
        List<Long> organizationServiceIds = organizationServiceRepository.getAllOrganizationServiceId(orgType);
        List<OrganizationService> organizationServices = IterableUtils.toList(organizationServiceRepository.findAllById(organizationServiceIds));
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(organizationServices, OrganizationServiceDTO.class);
    }

    @CacheEvict(value = "findAllActivityByCountry", allEntries = true)
    public List<Object> linkOrgServiceWithOrgType(long orgTypeId, long serviceId) {
        OrganizationType organizationType = organizationTypeGraphRepository.findOne(orgTypeId);
        List<Object> objectList = new ArrayList<>();
        if (organizationType != null) {
            if (checkIfServiceExistsWithOrganizationType(orgTypeId, serviceId) != 0) {
                LOGGER.info("Already Selected now Deselecting ");
                organizationTypeGraphRepository.deleteService(orgTypeId, serviceId);
                List<Map<String, Object>> mapList = ObjectMapperUtils.copyCollectionPropertiesByMapper(organizationServiceRepository.getOrgServicesByOrgType(orgTypeId), HashedMap.class);
                for (Map<String, Object> map : mapList) {
                    Object o = map.get(RESULT);
                    objectList.add(o);

                }
            } else {
                LOGGER.info("Not  Selected now Selecting ");
                organizationTypeGraphRepository.selectService(orgTypeId, serviceId);
                List<Map<String, Object>> mapList = ObjectMapperUtils.copyCollectionPropertiesByMapper(organizationServiceRepository.getOrgServicesByOrgType(orgTypeId), HashedMap.class);
                for (Map<String, Object> map : mapList) {
                    Object o = map.get(RESULT);
                    objectList.add(o);
                }
            }
            objectList.forEach(objectMap->{
                Map<String, Object> map = (Map<String, Object>)objectMap;
                ((List)map.get("children")).forEach(child->{
                    Map<String, Object> childMap = (Map<String, Object>)child;
                    TranslationUtil.convertTranslationFromStringToMap(childMap);
                });
                TranslationUtil.convertTranslationFromStringToMap(map);
            });
        }
        return objectList;
    }

    private int checkIfServiceExistsWithOrganizationType(long orgTypeId, long serviceId) {
        return organizationTypeGraphRepository.checkIfServiceExistsWithOrganizationType(orgTypeId, serviceId);

    }

    @CacheEvict(value = "findAllActivityByCountry", key = "#countryId")
    public OrganizationService createCountryOrganizationService(Long countryId, OrganizationService organizationService) {
        Country country = countryGraphRepository.findOne(countryId);
        if (country == null) {
            return null;
        }
        String name = "(?i)" + organizationService.getName().trim();
        boolean alreadyExistWithSameName = organizationServiceRepository.checkDuplicateService(countryId, name, -1L);
        if (alreadyExistWithSameName) {
            exceptionService.duplicateDataException(MESSAGE_ORGANIZATIONSERVICE_SERVICE_DUPLICATE);
        }
        List<OrganizationService> organizationServices = country.getOrganizationServices();
        organizationServices = (organizationServices == null) ? new ArrayList<>() : organizationServices;
        organizationServices.add(organizationService);
        country.setOrganizationServices(organizationServices);
        countryGraphRepository.save(country);
        return organizationService;
    }

    public Map<String, Object> organizationServiceData(long id) {
        List<Long> allUnitIds=organizationBaseRepository.fetchAllUnitIds(id);
        List<Map<String, Object>> services=(allUnitIds.size()==1 && allUnitIds.get(0).equals(id))?unitGraphRepository.getServicesForUnit(id):unitGraphRepository.getServicesForUnits(allUnitIds);
        services = ObjectMapperUtils.copyCollectionPropertiesByMapper(services, HashedMap.class);
        List<Map<String, Object>> avialableService = null;
        List<Map<String, Object>> selectedService = null;
        for(Map<String,Object> map : services){
            Map<String, Object> service =(Map<String, Object>) map.get("data");
            if(isNotNull(service.get(AVAILABLE_SERVICES))){
                avialableService = (List<Map<String,Object>>)service.get(AVAILABLE_SERVICES);
            }
            if(isNotNull(service.get(SELECTED_SERVICES))){
                selectedService = (List<Map<String,Object>>)service.get(SELECTED_SERVICES);
            }
            TranslationUtil.convertTranslationFromStringToMap(service);
        }
        Map<String,Object> organizationServiceMap = new HashMap<>();
        organizationServiceMap.put(AVAILABLE_SERVICES,avialableService);
        organizationServiceMap.put(SELECTED_SERVICES,selectedService);
        return organizationServiceMap;

    }


    private Map<String, Object> filterSkillData(List<Map<String, Object>> skillData) {
        Map<String, Object> response = new HashMap<>();
        for (Map<String, Object> map : skillData) {
            if (((Map<String, Object>) map.get("data")).get(AVAILABLE_SERVICES) != null) {
                response.put(AVAILABLE_SERVICES, ((Map<String, Object>) map.get("data")).get(AVAILABLE_SERVICES));
            }
            if (((Map<String, Object>) map.get("data")).get(SELECTED_SERVICES) != null) {
                response.put(SELECTED_SERVICES, ((Map<String, Object>) map.get("data")).get(SELECTED_SERVICES));
            }
        }

        return response;

    }


    /**
     * @param orgTypesIds
     * @return list of Organization services and Children SubServices
     */
    public List<Object> getOrgServicesByOrgSubTypesIds(Set<Long> orgTypesIds) {
        List<Object> objectList = new ArrayList<>();
        List<Map<String, Object>> organizationServices = organizationServiceRepository.getOrgServicesByOrgSubTypesIds(orgTypesIds);
        if (organizationServices != null) {
            for (Map<String, Object> map : organizationServices) {
                Object o = map.get(RESULT);
                objectList.add(o);
            }
        }
        return objectList;
    }


    public OrganizationService saveImportedServices(OrganizationService organizationService) {
        organizationServiceRepository.save(organizationService);
        return organizationService;
    }

    public Map<String, Object> organizationImportedServiceData(long id) {
        Unit unit = unitGraphRepository.findOne(id);
        if (unit == null) {
            return null;
        }
        return filterSkillData(unitGraphRepository.getImportedServicesForUnit(unit.getId()));

    }

    public OrganizationService mapImportedService(Long importedServiceId, Long serviceId) {

        OrganizationService organizationService = organizationServiceRepository.findOne(serviceId);
        OrganizationService mappedOrganizationService = organizationServiceRepository.findOne(importedServiceId);
        organizationServiceRepository.removeOrganizationExternalServiceRelationship(importedServiceId);
        OrganizationExternalServiceRelationship organizationExternalServiceRelationship = new OrganizationExternalServiceRelationship();
        organizationExternalServiceRelationship.setExternalService(mappedOrganizationService);
        organizationExternalServiceRelationship.setService(organizationService);
        mappedOrganizationService.setHasMapped(true);
        organizationExternalServiceRelationshipRepository.save(organizationExternalServiceRelationship);
        return organizationService;
    }

    public OrganizationService findOne(Long id) {
        OrganizationService organizationService = organizationServiceRepository.findOne(id);
        if (!Optional.ofNullable(organizationService).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATIONSERVICE_ID_NOTFOUND);

        }
        return organizationService;
    }

    public List<OrganizationServiceQueryResult> getAllOrganizationServicesByUnitId(Long unitId){
        return organizationServiceRepository.getAllOrganizationServicesByUnitId(unitId);
    }

}
