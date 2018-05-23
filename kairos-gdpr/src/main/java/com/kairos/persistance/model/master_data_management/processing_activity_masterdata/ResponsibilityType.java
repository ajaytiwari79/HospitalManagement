package com.kairos.persistance.model.master_data_management.processing_activity_masterdata;


import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "responsibility_type")
public class ResponsibilityType extends MongoBaseEntity {
    @NotNullOrEmpty(message = " responsibility Type cannot be null or empty ")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
