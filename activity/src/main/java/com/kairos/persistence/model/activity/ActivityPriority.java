package com.kairos.persistence.model.activity;

import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;


@Document
@Getter
@Setter
public class ActivityPriority extends MongoBaseEntity implements Serializable {

    private static final long serialVersionUID = 2006631334246625719L;
    private Long countryId;
    private Long organizationId;
    private String name;
    private String description;
    private int sequence;
    private String colorCode;
}
