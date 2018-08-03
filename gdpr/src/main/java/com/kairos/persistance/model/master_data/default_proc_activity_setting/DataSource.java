package com.kairos.persistance.model.master_data.default_proc_activity_setting;

import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.utils.custom_annotation.NotNullOrEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Pattern;

@Document(collection = "dataSource")
public class DataSource extends  MongoBaseEntity {


    @NotNullOrEmpty(message = "Name can't be empty")
    @Pattern(message = "Number and Special characters are not allowed for Name",regexp = "^[a-zA-Z\\s]+$")
    private String name;

    private Long countryId;

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }
    public String getName() {
        return name.trim();
    }

    public void setName(String name) {
        this.name = name;
    }


    public DataSource(String name) {
        this.name = name;
    }

    public DataSource() {
    }
}
