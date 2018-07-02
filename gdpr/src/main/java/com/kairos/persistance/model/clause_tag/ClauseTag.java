package com.kairos.persistance.model.clause_tag;

import com.kairos.persistance.model.common.JaversBaseEntity;
import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;


@Document(collection = "clause_tag")
public class ClauseTag extends JaversBaseEntity {

    @NotNullOrEmpty(message = "Name cannot be  empty")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
