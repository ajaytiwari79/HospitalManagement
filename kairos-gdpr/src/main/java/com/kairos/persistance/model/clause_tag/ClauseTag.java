package com.kairos.persistance.model.clause_tag;

import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;


@Document(collection = "clause_tag")
public class ClauseTag extends MongoBaseEntity {

    @NotNullOrEmpty(message = "error.name.cannotbe.null.or.empty")
    private String name;

    @NotNull
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
