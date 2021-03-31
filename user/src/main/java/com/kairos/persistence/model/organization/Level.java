package com.kairos.persistence.model.organization;


import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.ogm.annotation.NodeEntity;

import javax.validation.constraints.NotBlank;

import static com.kairos.constants.UserMessagesConstants.MESSAGE_COUNTRY_LEVEL_NAME_NOTEMPTY;

/**
 * Created by prabjot on 21/8/17.
 */
@NodeEntity
@Getter
@Setter
public class Level extends UserBaseEntity {

    private static final long serialVersionUID = 1495484063377486532L;
    @NotBlank(message = MESSAGE_COUNTRY_LEVEL_NAME_NOTEMPTY)
    private String name;
    private String description;
    private boolean isEnabled = true;


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
}