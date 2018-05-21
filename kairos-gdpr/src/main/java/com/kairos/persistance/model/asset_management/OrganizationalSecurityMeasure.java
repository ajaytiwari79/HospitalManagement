package com.kairos.persistance.model.asset_management;

import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "organization_security_measure")
public class OrganizationalSecurityMeasure extends MongoBaseEntity {

    @NotNullOrEmpty
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;

    }
    }
