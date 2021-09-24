package com.kairos.persistence.model.user.request_component;

import com.kairos.enums.RequestType;
import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by oodles on 22/8/17.
 */
@NodeEntity
@Getter
@Setter
public class RequestComponent extends UserBaseEntity {
    private String description;
    private RequestType requestType;
    private String requestSentType;
    private Long requestSentId;
}
