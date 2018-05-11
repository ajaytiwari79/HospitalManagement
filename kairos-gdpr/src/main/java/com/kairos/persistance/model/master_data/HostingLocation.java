package com.kairos.persistance.model.master_data;

import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "hosting_location")
public class HostingLocation extends MongoBaseEntity {

    @NotNullOrEmpty
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
