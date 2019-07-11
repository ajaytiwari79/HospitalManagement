package com.kairos.persistence.model.user.resources;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.*;

import static com.kairos.persistence.model.constants.RelationshipConstants.UNAVAILABLE_ON;

/**
 * Created by prabjot on 25/10/17.
 */
@RelationshipEntity(type = UNAVAILABLE_ON)
public class ResourceUnavailabilityRelationship extends UserBaseEntity{
    @StartNode
    private Resource resource;
    @EndNode
    private ResourceUnAvailability resourceUnAvailability;
    private Integer month;
    private Integer year;

    public ResourceUnavailabilityRelationship() {
        //default constructor
    }

    public ResourceUnavailabilityRelationship(Resource resource, ResourceUnAvailability resourceUnAvailability, Integer month, Integer year) {
        this.resource = resource;
        this.resourceUnAvailability = resourceUnAvailability;
        this.month = month;
        this.year = year;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public ResourceUnAvailability getResourceUnAvailability() {
        return resourceUnAvailability;
    }

    public void setResourceUnAvailability(ResourceUnAvailability resourceUnAvailability) {
        this.resourceUnAvailability = resourceUnAvailability;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
