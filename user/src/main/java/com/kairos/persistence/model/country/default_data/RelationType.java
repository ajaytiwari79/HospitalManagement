package com.kairos.persistence.model.country.default_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.apache.commons.lang.StringUtils;
import org.neo4j.ogm.annotation.NodeEntity;

import javax.validation.constraints.NotBlank;

import static com.kairos.constants.UserMessagesConstants.ERROR_RELATIONTYPE_NAME_NOTEMPTY;

/**
 * Created by Jasgeet on 15/9/17.
 */
@NodeEntity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RelationType extends UserBaseEntity {
    private static final long serialVersionUID = 7881110983923799984L;
    @NotBlank(message = ERROR_RELATIONTYPE_NAME_NOTEMPTY)
    private String name;
    private String description;
    private boolean enabled = true;

    public RelationType() {
    }

    public RelationType(@NotBlank(message = ERROR_RELATIONTYPE_NAME_NOTEMPTY) String name, String description) {
        this.name = name;
        this.description = description;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
