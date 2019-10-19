package com.kairos.persistence.model.user.integration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by oodles on 21/2/17.
 */
@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class TimeCare  extends UserBaseEntity {
    private Integer integrationId;
    private Long organizationId;
    private String timeCareExternalId;
}
