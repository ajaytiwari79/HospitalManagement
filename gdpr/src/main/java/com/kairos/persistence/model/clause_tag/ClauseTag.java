package com.kairos.persistence.model.clause_tag;

import com.kairos.persistence.model.common.BaseEntity;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;


@Entity
public class ClauseTag extends BaseEntity {

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    private String name;
    private boolean defaultTag;
    private Long countryId;
    private Long organizationId;

    public ClauseTag(@NotBlank(message = "error.message.name.notNull.orEmpty") String name) {
        this.name = name;
    }

    public ClauseTag() {
    }

    public ClauseTag(@NotBlank(message = "error.message.name.notNull.orEmpty") String name, boolean defaultTag) {
        this.name = name;
        this.defaultTag = defaultTag;
    }

    public boolean isDefaultTag() { return defaultTag; }

    public void setDefaultTag(boolean defaultTag) { this.defaultTag = defaultTag; }

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

    public Long getOrganizationId() { return organizationId; }

    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }
}
