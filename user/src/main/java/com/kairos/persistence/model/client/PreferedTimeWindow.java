package com.kairos.persistence.model.client;

import com.kairos.config.neo4j.converter.LocalTimeConverter;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.Organization;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;

import java.time.LocalTime;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;

/**
 * @author pradeep
 * @date - 28/6/18
 */
@NodeEntity
public class PreferedTimeWindow extends UserBaseEntity {

    private String name;
    @Convert(LocalTimeConverter.class)
    private LocalTime fromTime;
    @Convert(LocalTimeConverter.class)
    private LocalTime toTime;
    @Relationship(type = BELONGS_TO)
    private Organization organization;





    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public PreferedTimeWindow() {
    }

    public PreferedTimeWindow(LocalTime fromTime, LocalTime toTime, Organization organization, String name) {
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.organization = organization;
        this.name = name;
    }

    public LocalTime getFromTime() {
        return fromTime;
    }

    public void setFromTime(LocalTime fromTime) {
        this.fromTime = fromTime;
    }

    public LocalTime getToTime() {
        return toTime;
    }

    public void setToTime(LocalTime toTime) {
        this.toTime = toTime;
    }


}
