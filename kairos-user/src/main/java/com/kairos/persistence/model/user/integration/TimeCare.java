package com.kairos.persistence.model.user.integration;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by oodles on 21/2/17.
 */
@NodeEntity
public class TimeCare  extends UserBaseEntity {
    private Integer integrationId;
    private Long organizationId;

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

    public TimeCare() {
    }
}
