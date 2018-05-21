package com.kairos.persistance.model.master_data_management.asset_management;

import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "storage_format")
public class StorageFormat extends MongoBaseEntity {

    @NotNullOrEmpty
    private String name;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
