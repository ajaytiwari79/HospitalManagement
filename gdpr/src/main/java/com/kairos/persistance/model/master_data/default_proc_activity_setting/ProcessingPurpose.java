package com.kairos.persistance.model.master_data.default_proc_activity_setting;


import com.kairos.persistance.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Document(collection = "processing_purpose")
public class ProcessingPurpose extends MongoBaseEntity {


    @NotBlank(message = "error.message.name.cannot.be.null.or.empty")
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


    public ProcessingPurpose(String name) {
        this.name = name;
    }
    public ProcessingPurpose() {
    }
}
