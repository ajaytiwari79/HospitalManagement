package com.kairos.service.resources;

/**
 * Created by oodles on 17/10/16.
 */
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.resources.FuelType;
import com.kairos.persistence.model.user.resources.Resource;
import com.kairos.persistence.model.user.resources.VehicleType;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.resources.ResourceGraphRepository;
import com.kairos.service.UserBaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

/**
 * Calls ResourceGraphRepository to perform CRUD operation on Resources.
 */
@Service
@Transactional
public class ResourceService extends UserBaseService {

    @Inject
    ResourceGraphRepository resourceGraphRepository;
    @Inject
    OrganizationGraphRepository organizationGraphRepository;


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


    public Map<String, Object> getUnitResourcesTypes() {
        Map<String, Object> data = new HashMap<>();
        List<String> typeList = new ArrayList<>();
        List<String> fuelList = new ArrayList<>();
        for (VehicleType type : VehicleType.values()) {
            String value = String.valueOf(VehicleType.valueOf(type.name()));
            typeList.add(value);
        }
        for (FuelType type : FuelType.values()) {
            String value = String.valueOf(FuelType.valueOf(type.name()));
            fuelList.add(value);
        }

        data.put("typeList", typeList);
        data.put("fuelList", fuelList);

        return data;
    }


    public Resource setUnitResource(Resource resource, Long unitId) {
        Organization organization = organizationGraphRepository.findOne(unitId);
        if (organization != null) {
            if (organization.getResourceList() != null) {
                List<Resource> resourceList = organization.getResourceList();
                resourceList.add(resource);
                organizationGraphRepository.save(organization);
                return resourceGraphRepository.save(resource);
            } else {
                organization.setResourceList(Arrays.asList(resource));
                organizationGraphRepository.save(organization);
                return resourceGraphRepository.save(resource);
            }

        }
        return null;
    }


}
