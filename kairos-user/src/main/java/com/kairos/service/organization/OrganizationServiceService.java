package com.kairos.service.organization;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.organization.enums.OrganizationLevel;
import com.kairos.persistence.model.organization.team.Team;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.OrganizationServiceRepository;
import com.kairos.persistence.repository.organization.OrganizationTypeGraphRepository;
import com.kairos.persistence.repository.organization.TeamGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.service.UserBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

import static com.kairos.constants.AppConstants.ORGANIZATION;
import static com.kairos.constants.AppConstants.TEAM;

/**
 * Created by prabjot on 16/9/16.
 */
@Transactional
@Service
public class OrganizationServiceService extends UserBaseService {

    @Inject
    OrganizationTypeGraphRepository organizationTypeGraphRepository;
    @Inject
    private OrganizationServiceRepository organizationServiceRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    //TODO move this dependency in task
   /* @Inject
    private TaskTypeService taskTypeService;
    @Inject
    private TaskTypeMongoRepository taskTypeMongoRepository;
    @Inject*/
    private TeamGraphRepository teamGraphRepository;




    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public Map<String, Object> updateOrganizationService(long id, String name, String description) {
       com.kairos.persistence.model.organization.OrganizationService organizationService = organizationServiceRepository.findOne(id);
        if (organizationService == null) {
            return null;
        }
        organizationService.setName(name);
        organizationService.setDescription(description);
        save(organizationService);
        return organizationService.retrieveDetails();
    }

    public com.kairos.persistence.model.organization.OrganizationService getOrganizationServiceById(Long id) {
        return organizationServiceRepository.findOne(id);
    }

    public List<com.kairos.persistence.model.organization.OrganizationService> getOrganizationServiceByName(Long countryId, String name) {
        return organizationServiceRepository.getByServiceName(countryId, name);
    }

    public List<Object> getAllOrganizationService(long countryId) {
        List<Map<String, Object>> map = organizationServiceRepository.getOrganizationServicesByCountryId(countryId);
        List<Object> objectList = new ArrayList<>();
        for (Map<String, Object> result : map) {
            objectList.add(result.get("result"));
        }
        return objectList;
    }

    public com.kairos.persistence.model.organization.OrganizationService updateOrganizationServiceById(com.kairos.persistence.model.organization.OrganizationService organizationService) {
        save(organizationService);
        return organizationService;
    }

    public boolean deleteOrganizationServiceById(Long id) {
        com.kairos.persistence.model.organization.OrganizationService organizationService = organizationServiceRepository.findOne(id);
        if (organizationService == null) {
            return false;
        }
        organizationService.setEnabled(false);
        save(organizationService);
        if (organizationServiceRepository.findOne(id).isEnabled()){
            return false;
        }
        return true;
    }

    public com.kairos.persistence.model.organization.OrganizationService addSubService(final long serviceId,com.kairos.persistence.model.organization.OrganizationService subService) {
        com.kairos.persistence.model.organization.OrganizationService organizationService = organizationServiceRepository.findOne(serviceId);
        if (organizationService == null) {
            return null;
        }

        String name = "(?i)"+subService.getName();
        if (organizationServiceRepository.checkDuplicateSubService(organizationService.getId(),name)!=null){
            logger.info("Can't create duplicate sub service in same category");
            return null;
        }

        List<com.kairos.persistence.model.organization.OrganizationService> subServices = organizationService.getOrganizationSubService();
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
        logger.info("Sending Response: "+response);

        return subService;

    }

    public Map<String, Object> addCountrySubService(final long serviceId,com.kairos.persistence.model.organization.OrganizationService subService) {
        com.kairos.persistence.model.organization.OrganizationService organizationService = organizationServiceRepository.findOne(serviceId);
        if (organizationService == null) {
            throw  new DataNotFoundByIdException("Can't find Organization Service with provided Id");
        }

        String name = "(?i)"+subService.getName();
        if (organizationServiceRepository.checkDuplicateSubService(organizationService.getId(),name)!=null){
            throw new DuplicateDataException("Can't create duplicate sub service in same category");
        }

        logger.info("Creating : "+subService.getName()+" In "+organizationService.getName());
        List<com.kairos.persistence.model.organization.OrganizationService> subServicesList = organizationService.getOrganizationSubService();

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

    public Map<String,Object> updateServiceToOrganization(long id, long organizationServiceId,boolean isSelected,String type) {

        com.kairos.persistence.model.organization.OrganizationService organizationService = organizationServiceRepository.findOne(organizationServiceId);
        if(organizationService == null){
            throw new InternalError("organization service is null");
        }

        if (ORGANIZATION.equalsIgnoreCase(type)) {
            Organization unit = organizationGraphRepository.findOne(id);
            if(unit == null){
                throw new InternalError("Organization is null");
            }

            if(isSelected){
                logger.info("check if already exist-------> "+organizationGraphRepository.isServiceAlreadyExist(id,organizationService.getId()));
                if(organizationGraphRepository.isServiceAlreadyExist(id,organizationService.getId()) == 0){
                    organizationGraphRepository.addOrganizationServiceInUnit(id,Arrays.asList(organizationService.getId()),new Date().getTime(),new Date().getTime());
                }else {
                    organizationGraphRepository.updateServiceFromOrganization(id,organizationService.getId());
                }
            } else {
                organizationGraphRepository.removeServiceFromOrganization(id,organizationService.getId());
            }
        } else if (TEAM.equalsIgnoreCase(type)) {
            Team team = teamGraphRepository.findOne(id);
            if(team == null){
                throw new InternalError("team can not null");
            }
            if(isSelected){
                if(teamGraphRepository.countOfServices(id,organizationService.getId()) == 0){
                    teamGraphRepository.addServiceInTeam(id,organizationService.getId(),new Date().getTime(),new Date().getTime());
                }else {
                    teamGraphRepository.updateOrganizationService(id,organizationServiceId,true,new Date().getTime());
                }
            } else {
                teamGraphRepository.updateOrganizationService(id,organizationServiceId,false,new Date().getTime());
            }

        }
        return organizationServiceData(id,type);
    }



    public List<Object> getOrgServicesByOrgType(long orgType) {
        List<Map<String, Object>> organizationServices = organizationServiceRepository.getOrgServicesByOrgType(orgType);
        if (organizationServices!=null){
            List<Object> objectList = new ArrayList<>();
            for (Map<String,Object> map: organizationServices){
                Object o =  map.get("result");
                objectList.add(o);
            }
            return  objectList;
        }
        return  null;
    }

    public List<Object> linkOrgServiceWithOrgType(long orgTypeId, long serviceId) {
        OrganizationType organizationType = organizationTypeGraphRepository.findOne(orgTypeId);
        List<Object> objectList  = new ArrayList<>();
        if (organizationType!=null){
            if (checkIfServiceExistsWithOrganizationType(orgTypeId,serviceId)!=0){
                logger.info("Already Selected now Deselecting ");
                organizationTypeGraphRepository.deleteService(orgTypeId,serviceId);
                List<Map<String,Object>> mapList = organizationServiceRepository.getOrgServicesByOrgType(orgTypeId);
                for (Map<String,Object> map:  mapList) {
                    Object o = map.get("result");
                    objectList.add(o);

                }
                return objectList;
            }
            else {
                logger.info("Not  Selected now Selecting ");
                organizationTypeGraphRepository.selectService(orgTypeId,serviceId);
                List<Map<String,Object>> mapList = organizationServiceRepository.getOrgServicesByOrgType(orgTypeId);
                for (Map<String,Object> map:  mapList) {
                    Object o = map.get("result");
                    objectList.add(o);
                }
                return objectList;
            }
        }
        return null;
    }

    private int checkIfServiceExistsWithOrganizationType(long orgTypeId,long serviceId)  {
        return  organizationTypeGraphRepository.checkIfServiceExistsWithOrganizationType(orgTypeId,serviceId);

    }


    public com.kairos.persistence.model.organization.OrganizationService createOrganizationService(long countryId, com.kairos.persistence.model.organization.OrganizationService organizationService) {
        Country country = countryGraphRepository.findOne(countryId);
        if (country == null) {
            return null;
        }
        String name = "(?i)"+organizationService.getName();
        com.kairos.persistence.model.organization.OrganizationService organizationService1 = organizationServiceRepository.checkDuplicateService(countryId,name);
        if(organizationService1 != null){
            return organizationService1;
            //throw  new DuplicateDataException("Can't create organization service");
        }
        List<com.kairos.persistence.model.organization.OrganizationService> organizationServices = country.getOrganizationServices();
        organizationServices = (organizationServices == null) ? new ArrayList<>() : organizationServices;
        organizationServices.add(organizationService);
        country.setOrganizationServices(organizationServices);
        save(country);
        return organizationService;
    }

    public com.kairos.persistence.model.organization.OrganizationService createCountryOrganizationService(long countryId, com.kairos.persistence.model.organization.OrganizationService organizationService) {
        Country country = countryGraphRepository.findOne(countryId);
        if (country == null) {
            return null;
        }
        String name = "(?i)"+organizationService.getName();
        com.kairos.persistence.model.organization.OrganizationService organizationService1 = organizationServiceRepository.checkDuplicateService(countryId,name);
        if(organizationService1 != null){
            throw  new DuplicateDataException("Can't create organization service");
        }
        List<com.kairos.persistence.model.organization.OrganizationService> organizationServices = country.getOrganizationServices();
        organizationServices = (organizationServices == null) ? new ArrayList<>() : organizationServices;
        organizationServices.add(organizationService);
        country.setOrganizationServices(organizationServices);
        save(country);
        return organizationService;
    }

    public com.kairos.persistence.model.organization.OrganizationService getByName(String name) {
        return organizationServiceRepository.findByName(name);
    }


    public Map<String,Object> organizationServiceData(long id,String type) {


        Map<String,Object> response = null;

        if (ORGANIZATION.equalsIgnoreCase(type)) {

            Organization organization = organizationGraphRepository.findOne(id, 0);
            if (organization == null) {
                return null;
            }
            if (Optional.ofNullable(organization.getKmdExternalId()).isPresent()) {
                response = filterSkillData(organizationGraphRepository.getImportedServicesForUnit(id));
            } else {
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
        }
        } else if (TEAM.equalsIgnoreCase(type)) {
            Team team = teamGraphRepository.findOne(id);
            if(team == null){
                throw new InternalError("Team is null");
            }
            response = filterSkillData(teamGraphRepository.getOrganizationServicesOfTeam(id));

        }
        return response;
    }

    private Map<String,Object> filterSkillData(List<Map<String,Object>> skillData){
        Map<String,Object> response = new HashMap<>();
        for(Map<String,Object> map : skillData){

            if(((Map<String,Object>)map.get("data")).get("availableServices") != null){
                response.put("availableServices",((Map<String,Object>)map.get("data")).get("availableServices"));
            }
            if(((Map<String,Object>)map.get("data")).get("selectedServices") != null){
                response.put("selectedServices",((Map<String,Object>)map.get("data")).get("selectedServices"));
            }
        }

        return response;

    }

   /* public HashMap<String, Object> getTaskTypes(long id, long subServiceId,String type) {

        List<Map<String,Object>> visibleTaskTypes = new ArrayList<>();;
        List<Map<String,Object>> selectedTaskTypes = new ArrayList<>();
        if(ORGANIZATION.equalsIgnoreCase(type)){
            Organization organization = organizationGraphRepository.findOne(id);
            if (organization == null) {
                return null;
            }
            Organization parent;
            if (organization.getOrganizationLevel().equals(OrganizationLevel.CITY)) {
                parent = organizationGraphRepository.getParentOrganizationOfCityLevel(organization.getId());

            } else {
                parent = organizationGraphRepository.getParentOfOrganization(organization.getId());
            }
            if(parent == null){
                for(TaskType taskType : taskTypeMongoRepository.findBySubServiceIdAndOrganizationIdAndIsEnabled(subServiceId,0,true)){
                    visibleTaskTypes.add(taskType.getBasicTaskTypeInfo());
                }
            } else {
                visibleTaskTypes = new ArrayList<>();
                for(TaskType taskType :  taskTypeMongoRepository.findBySubServiceIdAndOrganizationIdAndIsEnabled(subServiceId,parent.getId(),true)){
                    visibleTaskTypes.add(taskType.getBasicTaskTypeInfo());
                }
            }
            for(TaskType taskType : taskTypeMongoRepository.findBySubServiceIdAndOrganizationIdAndIsEnabled(subServiceId,id,true)){
                selectedTaskTypes.add(taskType.getBasicTaskTypeInfo());
            }
        } else if(TEAM.equalsIgnoreCase(type)){
            Organization unit = organizationGraphRepository.getOrganizationByTeamId(id);
            if(unit == null){
                throw new InternalError("team can not exist without organization,organization not found");
            }
            for(TaskType taskType :  taskTypeMongoRepository.findBySubServiceIdAndOrganizationIdAndIsEnabled(subServiceId,unit.getId(),true)){
                visibleTaskTypes.add(taskType.getBasicTaskTypeInfo());
            }

            for(TaskType taskType : taskTypeMongoRepository.findByTeamIdAndSubServiceIdAndIsEnabled(id,subServiceId,true)){
                selectedTaskTypes.add(taskType.getBasicTaskTypeInfo());
            }

        }

        HashMap<String,Object> response = new HashMap<>();
        response.put("parentOrganizationTypes",visibleTaskTypes);
        response.put("unitTaskTypes",selectedTaskTypes);
        return response;
    }

    /**
     * @author prabjot
     * to update task type in organization/team based on type of node
     * @param id id of organization or team will be decided by type parameter
     * @param subServiceId
     * @param taskTypeId
     * @param isSelected if true task type will be added otherwise
     * @param type type can be {organization},{team}
     * @return
     */
    /*public Map<String,Object> updateTaskType(long id, long subServiceId, String taskTypeId, boolean isSelected, String type) {

        if(ORGANIZATION.equalsIgnoreCase(type)){
            if(isSelected){
                taskTypeService.linkTaskTypesWithOrg(taskTypeId,id,subServiceId);
            } else {
                taskTypeService.deleteTaskType(taskTypeId,id,subServiceId);
            }
        } else if(TEAM.equalsIgnoreCase(type)){
            TaskType taskType = taskTypeMongoRepository.findOne(new BigInteger(taskTypeId));
            if(taskType == null){
                throw new InternalError("Task type can not null");
            }
            if(isSelected){
                taskType.setTeamId(id);
                taskTypeService.save(taskType);
            } else {
                taskType.setEnabled(false);
                taskTypeService.save(taskType);
            }
        }
        return getTaskTypes(id,subServiceId,type);
    }*/

    public com.kairos.persistence.model.organization.OrganizationService saveImportedServices(com.kairos.persistence.model.organization.OrganizationService organizationService) {
        organizationServiceRepository.save(organizationService);
        return organizationService;
    }

    public Map<String,Object> organizationImportedServiceData(long id) {

        Map<String,Object> response = null;
        Organization unit = organizationGraphRepository.findOne(id);
        if (unit == null) {
            return null;
        }
        response = filterSkillData(organizationGraphRepository.getImportedServicesForUnit(unit.getId()));


        return response;
    }

    public com.kairos.persistence.model.organization.OrganizationService mapImportedService(Long importedServiceId, Long serviceId) {

        com.kairos.persistence.model.organization.OrganizationService organizationService = organizationServiceRepository.findOne(serviceId);
        com.kairos.persistence.model.organization.OrganizationService mappedOrganizationService = organizationServiceRepository.findOne(importedServiceId);
        organizationService.setReferenceId(importedServiceId.toString());
        mappedOrganizationService.setHasMapped(true);
        organizationServiceRepository.save(mappedOrganizationService);
        organizationServiceRepository.save(organizationService);
        return organizationService;
    }

    public Country getCountryByOranizationid(long organizationServiceId){
        return countryGraphRepository.getCountryByOrganizationService(organizationServiceId);
    }


}
