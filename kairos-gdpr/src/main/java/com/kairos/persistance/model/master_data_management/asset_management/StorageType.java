package com.kairos.persistance.model.master_data_management.asset_management;

import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;


@Document(collection = "storage_type")
public class StorageType extends MongoBaseEntity {
    @NotNullOrEmpty
    private String name;

    @NotNull(message = "error.message.countryId.cannot.be.null")
    private Long countryId;

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
