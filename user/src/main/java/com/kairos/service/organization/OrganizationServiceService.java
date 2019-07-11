package com.kairos.service.organization;

import com.kairos.commons.utils.DateUtils;
import com.kairos.enums.OrganizationLevel;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.organization.*;
import com.kairos.persistence.model.organization.services.OrganizationService;
import com.kairos.persistence.model.organization.services.OrganizationServiceQueryResult;
import com.kairos.persistence.model.organization.team.Team;
import com.kairos.persistence.repository.organization.*;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.constants.AppConstants.ORGANIZATION;
import static com.kairos.constants.AppConstants.TEAM;
import static com.kairos.constants.UserMessagesConstants.*;

/**
 * Created by prabjot on 16/9/16.
 */
@Transactional
@Service
public class OrganizationServiceService{

    @Inject
    private OrganizationTypeGraphRepository organizationTypeGraphRepository;
    @Inject
    private OrganizationServiceRepository organizationServiceRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    //TODO move this dependency in task
    @Inject
    private OrganizationExternalServiceRelationshipRepository organizationExternalServiceRelationshipRepository;
    @Inject
    private TeamGraphRepository teamGraphRepository;

    @Inject
    private ExceptionService exceptionService;

    private static final Logger logger = LoggerFactory.getLogger(OrganizationServiceService.class);


    public Map<String, Object> updateOrganizationService(long id, String name, String description,Long countryId) {
        if(isNull(name) || name.trim().isEmpty()){
            exceptionService.actionNotPermittedException(ERROR_ORGANIZATIONSERVICE_NAME_NOTEMPTY);
        }
        OrganizationService organizationService = organizationServiceRepository.findOne(id);
        if (organizationService == null) {
            return null;
        }
        boolean alreadyExistWithSameName = organizationServiceRepository.checkDuplicateService(countryId, "(?i)" + name.trim(),id);
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
            objectList.add(result.get("result"));
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
            logger.info("Can't create duplicate sub service in same category");
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
        logger.info("Sending Response: " + response);

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

        logger.info("Creating : " + subService.getName() + " In " + organizationService.getName());
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

    public OrganizationServiceQueryResult updateCustomNameOfService(long serviceId, long organizationId, String customName, String type) {
        if (type.equalsIgnoreCase("team")) {
            return teamGraphRepository.addCustomNameOfServiceForTeam(serviceId, organizationId, customName);
        } else {
            return organizationGraphRepository.addCustomNameOfServiceForOrganization(serviceId, organizationId, customName);
        }

    }

    public OrganizationServiceQueryResult updateCustomNameOfSubService(long subServiceId, long organizationId, String customName, String type) {
        if (type.equalsIgnoreCase("team")) {
            return teamGraphRepository.addCustomNameOfSubServiceForTeam(organizationId, subServiceId, customName);
        } else {
            return organizationGraphRepository.addCustomNameOfSubServiceForOrganization(subServiceId, organizationId, customName);
        }
    }

    private Boolean addDefaultCustomNameRelationShipOfServiceForOrganization(long subOrganizationServiceId, long organizationId) {
        return organizationGraphRepository.addCustomNameOfServiceForOrganization(subOrganizationServiceId, organizationId);
    }

    private Boolean addDefaultCustomNameRelationShipOfServiceForTeam(long subOrganizationServiceId, long teamId) {
        return teamGraphRepository.addCustomNameOfServiceForTeam(subOrganizationServiceId, teamId);
    }

    public Map<String, Object> updateServiceToOrganization(long id, long organizationServiceId, boolean isSelected, String type) {

        OrganizationService organizationService = organizationServiceRepository.findOne(organizationServiceId);
        if (organizationService == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATIONSERVICE_ID_NOTFOUND);

        }

        if (ORGANIZATION.equalsIgnoreCase(type)) {
            Organization unit = organizationGraphRepository.findOne(id);
            if (unit == null) {
                exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_ID_NOTFOUND,id);

            }

            if (isSelected) {
                logger.info("check if already exist-------> " + organizationGraphRepository.isServiceAlreadyExist(id, organizationService.getId()));
                if (organizationGraphRepository.isServiceAlreadyExist(id, organizationService.getId()) == 0) {
                    organizationGraphRepository.addOrganizationServiceInUnit(id, Arrays.asList(organizationService.getId()), DateUtils.getCurrentDate().getTime(), DateUtils.getCurrentDate().getTime());
                } else {
                    organizationGraphRepository.updateServiceFromOrganization(id, organizationService.getId());
                }
                addDefaultCustomNameRelationShipOfServiceForOrganization(organizationService.getId(), id);
            } else {
                organizationGraphRepository.removeServiceFromOrganization(id, organizationService.getId());
            }
//                        getServiceOfSubService
        } else if (TEAM.equalsIgnoreCase(type)) {
            Team team = teamGraphRepository.findOne(id);
            if (team == null) {
                exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATIONSERVICE_TEAM_NOTFOUND);

            }
            if (isSelected) {
                if (teamGraphRepository.countOfServices(id, organizationService.getId()) == 0) {
                    teamGraphRepository.addServiceInTeam(id, organizationService.getId(), DateUtils.getCurrentDate().getTime(), DateUtils.getCurrentDate().getTime());
                } else {
                    teamGraphRepository.updateOrganizationService(id, organizationServiceId, true, DateUtils.getCurrentDate().getTime());
                }
                addDefaultCustomNameRelationShipOfServiceForTeam(organizationService.getId(), id);
            } else {
                teamGraphRepository.updateOrganizationService(id, organizationServiceId, false, DateUtils.getCurrentDate().getTime());
            }

        }
        return organizationServiceData(id, type);
    }


    public List<Object> getOrgServicesByOrgType(long orgType) {
        List<Map<String, Object>> organizationServices = organizationServiceRepository.getOrgServicesByOrgType(orgType);
        if (organizationServices != null) {
            List<Object> objectList = new ArrayList<>();
            for (Map<String, Object> map : organizationServices) {
                Object o = map.get("result");
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
                logger.info("Already Selected now Deselecting ");
                organizationTypeGraphRepository.deleteService(orgTypeId, serviceId);
                List<Map<String, Object>> mapList = organizationServiceRepository.getOrgServicesByOrgType(orgTypeId);
                for (Map<String, Object> map : mapList) {
                    Object o = map.get("result");
                    objectList.add(o);

                }
                return objectList;
            } else {
                logger.info("Not  Selected now Selecting ");
                organizationTypeGraphRepository.selectService(orgTypeId, serviceId);
                List<Map<String, Object>> mapList = organizationServiceRepository.getOrgServicesByOrgType(orgTypeId);
                for (Map<String, Object> map : mapList) {
                    Object o = map.get("result");
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
        boolean alreadyExistWithSameName = organizationServiceRepository.checkDuplicateService(countryId, name,-1L);
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

    public Map<String, Object> organizationServiceData(long id, String type) {


        Map<String, Object> response = null;

        if (ORGANIZATION.equalsIgnoreCase(type)) {

            Organization organization = organizationGraphRepository.findOne(id, 0);
            if (organization == null) {
                return null;
            }
            Organization parent;
            if (organization.getOrganizationLevel().equals(OrganizationLevel.CITY)) {
                parent = organizationGraphRepository.getParentOrganizationOfCityLevel(organization.getId());

            } else {
                parent = organizationGraphRepository.getParentOfOrganization(organization.getId());
            }
            if (parent != null) {
                response = filterSkillData(organizationGraphRepository.getServicesForUnit(parent.getId(), id));
            } else {
                response = filterSkillData(organizationGraphRepository.getServicesForParent(id));
            }

        } else if (TEAM.equalsIgnoreCase(type)) {
            Team team = teamGraphRepository.findOne(id);
            if (team == null) {
                exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATIONSERVICE_TEAM_NOTFOUND);

            }
            response = filterSkillData(teamGraphRepository.getOrganizationServicesOfTeam(id));

        }
        return response;
    }

    private Map<String, Object> filterSkillData(List<Map<String, Object>> skillData) {
        Map<String, Object> response = new HashMap<>();
        for (Map<String, Object> map : skillData) {

            if (((Map<String, Object>) map.get("data")).get("availableServices") != null) {
                response.put("availableServices", ((Map<String, Object>) map.get("data")).get("availableServices"));
            }
            if (((Map<String, Object>) map.get("data")).get("selectedServices") != null) {
                response.put("selectedServices", ((Map<String, Object>) map.get("data")).get("selectedServices"));
            }
        }

        return response;

    }


    /**
     *
     * @param orgTypesIds
     * @return list of Organization services and Children SubServices
     */
    public List<Object> getOrgServicesByOrgSubTypesIds(Set<Long> orgTypesIds) {
        List<Map<String, Object>> organizationServices = organizationServiceRepository.getOrgServicesByOrgSubTypesIds(orgTypesIds);
        if (organizationServices != null) {
            List<Object> objectList = new ArrayList<>();
            for (Map<String, Object> map : organizationServices) {
                Object o = map.get("result");
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
        Organization unit = organizationGraphRepository.findOne(id);
        if (unit == null) {
            return null;
        }
        return filterSkillData(organizationGraphRepository.getImportedServicesForUnit(unit.getId()));

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
