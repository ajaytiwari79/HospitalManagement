package com.kairos.persistence.model.clause_tag;

import javax.validation.constraints.NotBlank;


public class ClauseTagDeprecated {

    @NotBlank(message = "Name cannot be  empty")
    private String name;
    private boolean defaultTag;
    private Long countryId;

    public ClauseTagDeprecated(@NotBlank(message = "Name cannot be  empty") String name) {
        this.name = name;
    }

    public ClauseTagDeprecated() {
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
}
