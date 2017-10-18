package com.kairos.persistence.model.user.position;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;

import javax.validation.constraints.NotNull;

/**
 * Created by pawanmandhan on 27/7/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@NodeEntity

public class PositionName extends UserBaseEntity {

    @NotEmpty(message = "error.position.name.notempty")
    @NotNull(message = "error.position.name.notnull")
    private String name;

    private String description;

    @JsonIgnore
    private boolean isEnabled = true;


    public PositionName() {
    }

    public PositionName(String name) {
        this.name = name;
    }


    public PositionName(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
