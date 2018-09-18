package com.kairos.persistence.model.clause_tag;

import com.kairos.persistence.model.common.MongoBaseEntity;
import org.javers.core.metamodel.annotation.TypeName;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;


@Document(collection = "clause_tag")
@TypeName("clause_tag")
public class ClauseTag extends MongoBaseEntity {

    @NotBlank(message = "Name cannot be  empty")
    private String name;

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
