package com.kairos.service.organization;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.DateUtils;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.organization.OrganizationExternalServiceRelationship;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.organization.Unit;
import com.kairos.persistence.model.organization.services.OrganizationService;
import com.kairos.persistence.model.organization.services.OrganizationServiceQueryResult;
import com.kairos.persistence.repository.organization.*;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.GdprIntegrationService;
import com.kairos.utils.user_context.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationServiceService.class);


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


    public List<Object> getAllOrganizationService(long countryId) {
        List<Map<String, Object>> map = organizationServiceRepository.getOrganizationServicesByCountryId(countryId);
        List<Object> objectList = new ArrayList<>();
        for (Map<String, Object> result : map) {
            objectList.add(result.get(RESULT));
        }
        return objectList;
    }


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
        LOGGER.info("Sending Response: " + response);

        return subService;

    }

    public Map<String, Object> addCountrySubService(final long serviceId, OrganizationService subService) {
        OrganizationService organizationService = organizationServiceRepository.findOne(serviceId);
        if (organizationService == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATIONSERVICE_ID_NOTFOUND);

        }

        String name = "(?i)" + subService.getName();
        if (organizationServiceRepository.checkDuplicateSubService(organizationService.getId(), name) != null) {
            exceptionService.duplicateDataException(MESSAGE_ORGANIZATIONSERVICE_SUBSERVICE_DUPLICATED);

        }

        LOGGER.info("Creating : " + subService.getName() + " In " + organizationService.getName());
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

    public OrganizationServiceQueryResult updateCustomNameOfService(long serviceId, long organizationId, String customName) {

        return unitGraphRepository.addCustomNameOfServiceForOrganization(serviceId, organizationId, customName);


    }

    public OrganizationServiceQueryResult updateCustomNameOfSubService(long subServiceId, long organizationId, String customName) {
            return teamGraphRepository.addCustomNameOfSubServiceForTeam(organizationId, subServiceId, customName);
    }

    private Boolean addDefaultCustomNameRelationShipOfServiceForOrganization(long subOrganizationServiceId, long organizationId) {
        return unitGraphRepository.addCustomNameOfServiceForOrganization(subOrganizationServiceId, organizationId);
    }

    public Map<String, Object> updateServiceToOrganization(long id, long organizationServiceId, boolean isSelected) {
        Unit unit=unitGraphRepository.findById(id).orElseThrow(()->new DataNotFoundByIdException(exceptionService.convertMessage(MESSAGE_DATANOTFOUND,"Unit",id)));
        OrganizationService organizationService = organizationServiceRepository.findOne(organizationServiceId);
        if (organizationService == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATIONSERVICE_ID_NOTFOUND);
        }
        if (isSelected) {
            LOGGER.info("check if already exist-------> ");
            if (unitGraphRepository.isServiceAlreadyExist(id, organizationService.getId()) == 0) {
                unitGraphRepository.addOrganizationServiceInUnit(id, Arrays.asList(organizationService.getId()), DateUtils.getCurrentDate().getTime(), DateUtils.getCurrentDate().getTime());
            } else {
                unitGraphRepository.updateServiceFromOrganization(id, organizationService.getId());
            }
            addDefaultCustomNameRelationShipOfServiceForOrganization(organizationService.getId(), id);
            //call to create asset for org.
            List<Long> orgSubTypeIds = unit.getOrganizationSubTypes().stream().map(unitSubType -> unitSubType.getId()).collect(Collectors.toList());
            gdprIntegrationService.createDefaultAssetForUnit(UserContext.getUserDetails().getCountryId(),unit.getId(), orgSubTypeIds,organizationServiceId);
        } else {
            unitGraphRepository.removeServiceFromOrganization(id, organizationService.getId());
        }
        return organizationServiceData(id);
    }


    public List<Object> getOrgServicesByOrgType(long orgType) {
        List<Map<String, Object>> organizationServices = organizationServiceRepository.getOrgServicesByOrgType(orgType);
        if (organizationServices != null) {
            List<Object> objectList = new ArrayList<>();
            for (Map<String, Object> map : organizationServices) {
                Object o = map.get(RESULT);
                objectList.add(o);
            }
            return objectList;
        }
        return null;
    }

    public List<Object> linkOrgServiceWithOrgType(long orgTypeId, long serviceId) {
        OrganizationType organizationType = organizationTypeGraphRepository.findOne(orgTypeId);
        List<Object> objectList = new ArrayList<>();
        if (organizationType != null) {
            if (checkIfServiceExistsWithOrganizationType(orgTypeId, serviceId) != 0) {
                LOGGER.info("Already Selected now Deselecting ");
                organizationTypeGraphRepository.deleteService(orgTypeId, serviceId);
                List<Map<String, Object>> mapList = organizationServiceRepository.getOrgServicesByOrgType(orgTypeId);
                for (Map<String, Object> map : mapList) {
                    Object o = map.get(RESULT);
                    objectList.add(o);

                }
                return objectList;
            } else {
                LOGGER.info("Not  Selected now Selecting ");
                organizationTypeGraphRepository.selectService(orgTypeId, serviceId);
                List<Map<String, Object>> mapList = organizationServiceRepository.getOrgServicesByOrgType(orgTypeId);
                for (Map<String, Object> map : mapList) {
                    Object o = map.get(RESULT);
                    objectList.add(o);
                }
                return objectList;
            }
        }
        return null;
    }

    private int checkIfServiceExistsWithOrganizationType(long orgTypeId, long serviceId) {
        return organizationTypeGraphRepository.checkIfServiceExistsWithOrganizationType(orgTypeId, serviceId);

    }


    public OrganizationService createCountryOrganizationService(long countryId, OrganizationService organizationService) {
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
        return filterSkillData(services);

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
        List<Map<String, Object>> organizationServices = organizationServiceRepository.getOrgServicesByOrgSubTypesIds(orgTypesIds);
        if (organizationServices != null) {
            List<Object> objectList = new ArrayList<>();
            for (Map<String, Object> map : organizationServices) {
                Object o = map.get(RESULT);
                objectList.add(o);
            }
            return objectList;
        }
        return null;
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


}
