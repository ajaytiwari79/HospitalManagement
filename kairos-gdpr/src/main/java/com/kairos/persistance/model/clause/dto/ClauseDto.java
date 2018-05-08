package com.kairos.persistance.model.clause.dto;

import com.kairos.persistance.model.clause.Clause;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class ClauseDto {


    @NotEmpty(message = "error.clause.title.cannotbe.empty")
    @NotNull(message = "error.clause.title.cannotbe.null")
    private String title;

    private List<String> tags = new ArrayList<>();

    @NotEmpty(message = "error.clause.title.cannotbe.empty")
    @NotNull(message = "error.clause.title.cannotbe.null")
    private String description;


    private List<Long> organizationServiceIds;
    private List<Long> organizationTypeIds;
    private List<Long> accountType;

    public String getTitle() {
        return title;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getDescription() {
        return description;
    }

    public List<Long> getOrganizationServiceIds() {
        return organizationServiceIds;
    }

    public List<Long> getOrganizationTypeIds() {
        return organizationTypeIds;
    }

    public List<Long> getAccountType() {
        return accountType;
    }

}
