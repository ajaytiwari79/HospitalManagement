package com.kairos.persistence.model.activity;

import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;


@Document
@Getter
@Setter
public class ActivityPriority extends MongoBaseEntity {

    private Long countryId;
    private Long organizationId;
    private String name;
    private String description;
    private int sequence;
    private String colorCode;
}
