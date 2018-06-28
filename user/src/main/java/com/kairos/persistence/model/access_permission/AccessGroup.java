package com.kairos.persistence.model.access_permission;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.user.access_permission.AccessGroupRole;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.typeconversion.EnumString;

import javax.validation.constraints.NotNull;

/**
 * Created by prabjot on 9/27/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@NodeEntity
public class AccessGroup extends UserBaseEntity {

    @NotEmpty(message = "error.name.notnull") @NotNull(message = "error.name.notnull")
    private String name;
    private boolean enabled = true;
    private boolean typeOfTaskGiver;
    private String description;

    @Property(name = "role")
    @EnumString(AccessGroupRole.class)
    private AccessGroupRole role;

    public AccessGroup(String name, String description, AccessGroupRole role) {
        this.name = name;
        this.description = description;
        this.role = role;
    }

    public AccessGroup(){}

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isTypeOfTaskGiver() {
        return typeOfTaskGiver;
    }

    public void setTypeOfTaskGiver(boolean typeOfTaskGiver) {
        this.typeOfTaskGiver = typeOfTaskGiver;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AccessGroupRole getRole() {
        return role;
    }

    public void setRole(AccessGroupRole role) {
        this.role = role;
    }
}
