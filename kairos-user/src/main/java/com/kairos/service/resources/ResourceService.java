package com.kairos.service.resources;

/**
 * Created by oodles on 17/10/16.
 */

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.resources.*;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.resources.ResourceGraphRepository;
import com.kairos.persistence.repository.user.resources.ResourceUnavailabilityRelationshipRepository;
import com.kairos.persistence.repository.user.resources.VehicleGraphRepository;
import com.kairos.service.UserBaseService;
import com.kairos.service.country.CountryService;
import com.kairos.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.text.ParseException;
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
        if (Optional.ofNullable(resource).isPresent()) {
            return resourceGraphRepository.save(resource) != null;
        }
        throw new DataNotFoundByIdException("Resource not found by id");
    }

    public List<ResourceWrapper> getUnitResources(Long unitId) {
        return resourceGraphRepository.getResources(unitId);
    }

    public List<ResourceWrapper> getOrganizationResourcesWithUnAvailability(Long unitId, String date) {
        Instant instant = Instant.parse(date);
        LocalDateTime startDate = LocalDateTime.ofInstant(instant, ZoneId.of(ZoneOffset.UTC.getId()));
        List<ResourceWrapper> resources = resourceGraphRepository.getResourcesWithUnAvailability(unitId, startDate.getMonth().getValue(), startDate.getYear());
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
        Resource dbResourceObject = resourceGraphRepository.getResourceByRegistrationNumberAndUnit(unitId,resourceDTO.getRegistrationNumber());

        if(Optional.ofNullable(dbResourceObject).isPresent()){
            throw new DuplicateDataException("Resource already exist with register number " + resourceDTO.getRegistrationNumber());
        }

        Vehicle vehicle = vehicleGraphRepository.findOne(resourceDTO.getVehicleTypeId());
        if (!Optional.ofNullable(vehicle).isPresent()) {
            logger.error("Vehicle type not found " + resourceDTO.getVehicleTypeId());
            throw new DataNotFoundByIdException("Vehicle type not found");
        }
        Resource resource = new Resource(vehicle, resourceDTO.getRegistrationNumber(), resourceDTO.getNumber(),
                resourceDTO.getModelDescription(), resourceDTO.getCostPerKM(), resourceDTO.getFuelType());
        if(!StringUtils.isBlank(resourceDTO.getDecommissionDate())){
            LocalDateTime decommissionDate = LocalDateTime.ofInstant(DateUtil.convertToOnlyDate(resourceDTO.getDecommissionDate(),
                    MONGODB_QUERY_DATE_FORMAT).toInstant(), ZoneId.systemDefault()).
                    withHour(0).withMinute(0).withSecond(0).withNano(0);
            resource.setDecommissionDate(decommissionDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        resource.setCreationDate(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
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

    public List<ResourceUnAvailability> setResourceUnavailability(ResourceUnavailabilityDTO unavailabilityDTO, Long resourceId) {
        Resource resource = resourceGraphRepository.findOne(resourceId);
        if (!Optional.ofNullable(resource).isPresent()) {
            logger.error("Resource not found by id " + resource);
            throw new DataNotFoundByIdException("Resource not found");
        }
        List<ResourceUnavailabilityRelationship> resourceUnavailabilityRelationships = new ArrayList<>
                (unavailabilityDTO.getUnavailabilityDates().size());

        List<ResourceUnAvailability> resourceUnAvailabilities = new ArrayList<>();
        for (String unavailabilityDate : unavailabilityDTO.getUnavailabilityDates()) {
            ResourceUnAvailability resourceUnAvailability = new ResourceUnAvailability(unavailabilityDTO.isFullDay()).
                    setUnavailability(unavailabilityDTO, unavailabilityDate);
            try {
                LocalDateTime startDateIncludeTime = LocalDateTime.ofInstant(DateUtil.convertToOnlyDate(unavailabilityDate,
                        MONGODB_QUERY_DATE_FORMAT).toInstant(), ZoneId.systemDefault());
                ResourceUnavailabilityRelationship resourceUnavailabilityRelationship = new ResourceUnavailabilityRelationship(resource,
                        resourceUnAvailability, startDateIncludeTime.getMonth().getValue(), startDateIncludeTime.getYear());
                resourceUnavailabilityRelationships.add(resourceUnavailabilityRelationship);
                resourceUnAvailabilities.add(resourceUnAvailability);
            } catch (ParseException e) {
                throw new InternalError("Incorrect resource date ");
            }
        }
        unavailabilityRelationshipRepository.save(resourceUnavailabilityRelationships);
        return resourceUnAvailabilities;
    }

    public ResourceUnAvailability updateResourceUnavailability(ResourceUnavailabilityDTO unavailabilityDTO, Long unAvailabilityId,
                                                                  Long resourceId) throws ParseException {
        ResourceUnAvailability resourceUnAvailability = resourceGraphRepository.getResourceUnavailabilityById(resourceId,unAvailabilityId);
        if(!Optional.ofNullable(resourceUnAvailability).isPresent()){
            logger.error("Incorrect id of Resource unavailability " + unAvailabilityId);
            throw new DataNotFoundByIdException("Resource unavailability not found ");
        }
        if(unavailabilityDTO.isFullDay()){
            resourceUnAvailability.setFullDay(unavailabilityDTO.isFullDay());
            resourceUnAvailability.setStartTime(null);
            resourceUnAvailability.setEndTime(null);
        } else {
            if(!unavailabilityDTO.isFullDay() && !StringUtils.isBlank(unavailabilityDTO.getStartTime())){
                LocalDateTime timeFrom = LocalDateTime.ofInstant(DateUtil.convertToOnlyDate(unavailabilityDTO.getStartTime(),
                        MONGODB_QUERY_DATE_FORMAT).toInstant(), ZoneId.systemDefault());
                resourceUnAvailability.setStartTime(timeFrom.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            }

            if(!unavailabilityDTO.isFullDay() && !StringUtils.isBlank(unavailabilityDTO.getEndTime())){
                LocalDateTime timeTo = LocalDateTime.ofInstant(DateUtil.convertToOnlyDate(unavailabilityDTO.getEndTime(),
                        MONGODB_QUERY_DATE_FORMAT).toInstant(), ZoneId.systemDefault());
                resourceUnAvailability.setEndTime(timeTo.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            }
        }
        return save(resourceUnAvailability);
    }

    public void deleteUnavailability(Long resourceId, Long unavailableDateId) {
        resourceGraphRepository.deleteResourceUnavailability(resourceId, unavailableDateId);
    }

    public List<ResourceUnAvailability> getResourceUnAvailability(Long resourceId,String date){
        Resource resource = resourceGraphRepository.findOne(resourceId);
        if(!Optional.ofNullable(resource).isPresent()){
            logger.error("Resource not found by id " + resource);
            throw new DataNotFoundByIdException("Resource not found");
        }
        Instant instant = Instant.parse(date);
        LocalDateTime startDate = LocalDateTime.ofInstant(instant, ZoneId.of(ZoneOffset.UTC.getId()));
        return resourceGraphRepository.getResourceUnavailability(resourceId,startDate.getMonth().getValue(),startDate.getYear());
    }


}
