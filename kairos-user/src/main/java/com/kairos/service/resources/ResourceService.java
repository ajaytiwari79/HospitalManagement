package com.kairos.service.resources;

/**
 * Created by oodles on 17/10/16.
 */

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.resources.*;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.resources.ResourceGraphRepository;
import com.kairos.persistence.repository.user.resources.ResourceUnavailabilityRelationshipRepository;
import com.kairos.persistence.repository.user.resources.VehicleGraphRepository;
import com.kairos.service.UserBaseService;
import com.kairos.service.country.CountryService;
import com.kairos.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
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
    @Inject
    ResourceUnavailabilityRelationshipRepository unavailabilityRelationshipRepository;


    /**
     * Get the List of Resources in Organization
     *
     * @param organizationId
     * @return list of Resources
     */
    public List<Map<String, Object>> getAllResourcesByOrgId(Long organizationId) {
        return resourceGraphRepository.getResourceByOrganizationId(organizationId);

    }


    /**
     * Get a Resource by Id
     *
     * @param resourceId
     * @return resource
     */
    public Resource getResourceById(Long resourceId) {
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
     *
     * @param organizationId
     * @param resource
     * @return resource
     */
    public Resource addResourceToOrganization(Long organizationId, Resource resource) {
        Organization currentOrganization = organizationGraphRepository.findOne(organizationId);

        if (currentOrganization.getResourceList() == null) {
            currentOrganization.setResourceList(Arrays.asList(resourceGraphRepository.save(resource)));
            organizationGraphRepository.save(currentOrganization);
            return resource;
        }
        List<Resource> resourceList = currentOrganization.getResourceList();
        resourceList.add(resource);
        organizationGraphRepository.save(currentOrganization);
        return resource;
    }

    /**
     * Safe-Delete a Resource by id
     *
     * @param resourceId
     */
    public boolean deleteResource(Long resourceId) {
        Resource resource = resourceGraphRepository.findOne(resourceId);
        if(Optional.ofNullable(resource).isPresent()){
            resource.setDeleted(true);
            return resourceGraphRepository.save(resource) != null;
        }
        throw new DataNotFoundByIdException("Resource not found by id");
    }

    public List<ResourceWrapper> getUnitResources(Long unitId, String date) {
        Instant instant = Instant.parse(date);
        LocalDateTime startDate = LocalDateTime.ofInstant(instant, ZoneId.of(ZoneOffset.UTC.getId()));
        List<ResourceWrapper> resources = resourceGraphRepository.getResources(unitId,startDate.getMonth().getValue(),startDate.getYear());
        return resources;
    }


    public ResourceTypeWrapper getUnitResourcesTypes(Long unitId) {
        Long countryId = organizationGraphRepository.getCountryId(unitId);
        List<Vehicle> vehicleTypes = countryService.getVehicleList(countryId);
        ResourceTypeWrapper resourceWrapper = new ResourceTypeWrapper(vehicleTypes, Arrays.asList(FuelType.values()));
        return resourceWrapper;
    }


    public Resource addResource(ResourceDTO resourceDTO, Long unitId) throws ParseException {
        Organization organization = (Optional.ofNullable(unitId).isPresent()) ? organizationGraphRepository.findOne(unitId) : null;
        if (!Optional.ofNullable(organization).isPresent()) {
            logger.error("Incorrect unit id " + unitId);
            throw new DataNotFoundByIdException("Incorrect unit id ");
        }

        Vehicle vehicle = vehicleGraphRepository.findOne(resourceDTO.getVehicleTypeId());
        if (!Optional.ofNullable(vehicle).isPresent()) {
            logger.error("Vehicle type not found " + resourceDTO.getVehicleTypeId());
            throw new DataNotFoundByIdException("Vehicle type not found");
        }
        Resource resource = new Resource(vehicle, resourceDTO.getRegistrationNumber(), resourceDTO.getNumber(),
                resourceDTO.getModelDescription(), resourceDTO.getCostPerKM(), resourceDTO.getFuelType());
        organization.addResource(resource);
        save(organization);
        return resource;
    }

    public Resource updateResource(ResourceDTO resourceDTO, Long resourceId) throws ParseException {
        Resource resource = (Optional.ofNullable(resourceId).isPresent()) ? resourceGraphRepository.findOne(resourceId) : null;
        if (!Optional.ofNullable(resource).isPresent()) {
            logger.error("Incorrect resource id" + resourceId);
            throw new DataNotFoundByIdException("Incorrect id of resource");
        }
        Vehicle vehicle = vehicleGraphRepository.findOne(resourceDTO.getVehicleTypeId());
        if (!Optional.ofNullable(vehicle).isPresent()) {
            logger.error("Vehicle type not found " + resourceDTO.getVehicleTypeId());
            throw new DataNotFoundByIdException("Vehicle type not found");
        }
        resource.setVehicleType(vehicle);
        return resourceGraphRepository.save(resource);
    }

    public Resource setResourceUnavailability(ResourceUnavailabilityDTO unavailabilityDTO,Long resourceId){
        Resource resource = resourceGraphRepository.findOne(resourceId);
        if(resource == null){
            logger.error("Resource not found by id " + resource);
            throw new DataNotFoundByIdException("Resource not found");
        }
        List<ResourceUnavailabilityRelationship> resourceUnavailabilityRelationships = new ArrayList<>
                (unavailabilityDTO.getUnavailabilityDates().size());


        for(String unavailabilityDate : unavailabilityDTO.getUnavailabilityDates()){
            ResourceUnAvailability resourceUnAvailability = new ResourceUnAvailability().
                    setUnavailability(unavailabilityDTO,unavailabilityDate);
            try {
                LocalDateTime startDateIncludeTime = LocalDateTime.ofInstant(DateUtil.convertToOnlyDate(unavailabilityDate,
                        MONGODB_QUERY_DATE_FORMAT).toInstant(), ZoneId.systemDefault());
                ResourceUnavailabilityRelationship resourceUnavailabilityRelationship = new ResourceUnavailabilityRelationship(resource,
                        resourceUnAvailability,startDateIncludeTime.getMonth().getValue(),startDateIncludeTime.getYear());
                resourceUnavailabilityRelationships.add(resourceUnavailabilityRelationship);
            } catch (ParseException e){
                throw new InternalError("Incorrect resource date ");
            }
        }
        unavailabilityRelationshipRepository.save(resourceUnavailabilityRelationships);
        return null;
    }


}
