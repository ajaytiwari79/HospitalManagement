package com.kairos.persistence.model.organization;



import com.kairos.persistence.model.common.UserBaseEntity;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.ogm.annotation.NodeEntity;

import javax.validation.constraints.NotBlank;

/**
 * Created by prabjot on 21/8/17.
 */
@NodeEntity
public class Level extends UserBaseEntity {

    @NotBlank(message = "message.country.level.name.notEmpty")
    private String name;
    private String description;
    private boolean isEnabled = true;
    private boolean deleted;


    public Level() {
        //default constructor
    }

    public Level(String name) {
        this.name = StringUtils.trim(name);
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = StringUtils.trim(name);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = StringUtils.trim(description);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}