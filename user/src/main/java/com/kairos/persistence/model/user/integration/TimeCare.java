package com.kairos.persistence.model.user.integration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by oodles on 21/2/17.
 */
@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeCare  extends UserBaseEntity {
    private Integer integrationId;
    private Long organizationId;
    private String timeCareExternalId;

    public TimeCare() {
        //Default Constructor
    }

    public Integer getIntegrationId() {
        return integrationId;
    }

    public void setIntegrationId(Integer integrationId) {
        this.integrationId = integrationId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getTimeCareExternalId() {
        return timeCareExternalId;
    }

    public void setTimeCareExternalId(String timeCareExternalId) {
        this.timeCareExternalId = timeCareExternalId;
    }
}
