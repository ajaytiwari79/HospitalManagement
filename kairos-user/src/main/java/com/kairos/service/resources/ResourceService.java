package com.kairos.service.resources;

/**
 * Created by oodles on 17/10/16.
 */
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.resources.Vehicle;
import com.kairos.persistence.model.user.resources.*;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.resources.ResourceGraphRepository;
import com.kairos.persistence.repository.user.resources.VehicleGraphRepository;
import com.kairos.service.UserBaseService;
import com.kairos.service.country.CountryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.*;
import java.util.*;

import static com.kairos.util.DateUtil.MONGODB_QUERY_DATE_FORMAT;

/**
 * Calls ResourceGraphRepository to perform CRUD operation on Resources.
 */
@Service
@Transactional
public class ResourceService extends UserBaseService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    ResourceGraphRepository resourceGraphRepository;
    @Inject
    OrganizationGraphRepository organizationGraphRepository;
    @Inject
    CountryService countryService;
    @Inject
    VehicleGraphRepository vehicleGraphRepository;


    /**
     * Get the List of Resources in Organization
     * @param organizationId
     * @return list of Resources
     */
    public List<Map<String,Object>> getAllResourcesByOrgId(Long organizationId){
        return  resourceGraphRepository.getResourceByOrganizationId(organizationId);

    }


    /**
     * Get a Resource by Id
     * @param resourceId
     * @return resource
     */
    public Resource getResourceById(Long resourceId){
        Resource resource = resourceGraphRepository.findOne(resourceId);
        return resource;
    }


    /**
     * Calls ResourceGraphRepository , find Resource by id as provided in method argument
     * and return updated Resource
     *
     * @param resource
     * @return resource
     */
    public Resource updateResource(Resource resource) {
        Resource currentResource = resourceGraphRepository.findOne(resource.getId());
        if (currentResource != null) {
            currentResource = resource;
        }
        return resourceGraphRepository.save(currentResource);
    }

    /**
     * Add a Resource to Organization
     * @param organizationId
     * @param resource
     * @return resource
     */
    public Resource addResourceToOrganization(Long organizationId, Resource resource) {
        Organization currentOrganization = organizationGraphRepository.findOne(organizationId);

        if (currentOrganization.getResourceList()==null){
            currentOrganization.setResourceList(Arrays.asList(resourceGraphRepository.save(resource)));
            organizationGraphRepository.save(currentOrganization);
            return  resource;
        }
        List<Resource> resourceList = currentOrganization.getResourceList();
        resourceList.add(resource);
        organizationGraphRepository.save(currentOrganization);
        return resource;
    }

    /**
     * Safe-Delete a Resource by id
     * @param resourceId
     */
    public void safeDeleteResource(Long resourceId){
        Resource resource = resourceGraphRepository.findOne(resourceId);
        resource.setDeleted(true);
        resourceGraphRepository.save(resource);

    }

    public List<Object> getUnitResources(Long unitId) {
        List<Map<String, Object>> mapList = getAllResourcesByOrgId(unitId);
        List<Object> objectList = new ArrayList<>();
        if (!mapList.isEmpty()) {
            for (Map<String, Object> map : mapList) {
                Object o = map.get("resourceList");
                objectList.add(o);
            }
            return objectList;
        }
        return null;
    }


    public ResourceWrapper getUnitResourcesTypes(Long unitId) {
        Long countryId = organizationGraphRepository.getCountryId(unitId);
        List<Vehicle> vehicleTypes = countryService.getVehicleList(countryId);
        ResourceWrapper resourceWrapper = new ResourceWrapper(vehicleTypes,Arrays.asList(FuelType.values()));
        return resourceWrapper;
    }


    public Resource addResource(ResourceDTO resourceDTO, Long unitId) {
        Organization organization = (Optional.ofNullable(unitId).isPresent())?organizationGraphRepository.findOne(unitId):null;
        if(!Optional.ofNullable(organization).isPresent()){
            logger.error("Incorrect unit id " + unitId);
            throw new DataNotFoundByIdException("Incorrect unit id ");
        }

        Vehicle vehicle = vehicleGraphRepository.findOne(resourceDTO.getVehicleTypeId());
        if(!Optional.ofNullable(vehicle).isPresent()){
            logger.error("Vehicle type not found " + resourceDTO.getVehicleTypeId());
            throw new DataNotFoundByIdException("Vehicle type not found");
        }
        Resource resource = new Resource(vehicle,resourceDTO.getRegistrationNumber(),resourceDTO.getNumber(),
                resourceDTO.getModelDescription(),resourceDTO.getCostPerKM());
        resource.setAvailability(resourceDTO);
        organization.addResource(resource);
        save(organization);
        return resource;
    }


}
