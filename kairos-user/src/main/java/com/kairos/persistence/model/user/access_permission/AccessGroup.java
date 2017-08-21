package com.kairos.persistence.model.user.access_permission;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;

import com.kairos.persistence.model.common.UserBaseEntity;

/**
 * Created by prabjot on 9/27/16.
 */
@NodeEntity
public class AccessGroup extends UserBaseEntity {

    @NotEmpty(message = "error.name.notnull") @NotNull(message = "error.name.notnull")
    private String name;
    private boolean isEnabled = true;
    private boolean typeOfTaskGiver;

    public AccessGroup(String name) {
        this.name = name;
    }
    public AccessGroup(){}

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {

        isEnabled = enabled;
    }

    public boolean isTypeOfTaskGiver() {
        return typeOfTaskGiver;
    }

    public void setTypeOfTaskGiver(boolean typeOfTaskGiver) {
        this.typeOfTaskGiver = typeOfTaskGiver;
    }
}
