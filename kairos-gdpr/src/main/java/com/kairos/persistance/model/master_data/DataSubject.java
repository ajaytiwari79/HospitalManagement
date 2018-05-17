package com.kairos.persistance.model.master_data;


import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "dataSubject")
public class DataSubject extends MongoBaseEntity {

    @NotNullOrEmpty(message = "name of DataSubject cannot be null or empty ")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
