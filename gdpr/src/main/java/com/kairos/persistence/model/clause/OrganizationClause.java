package com.kairos.persistence.model.clause;

import com.kairos.persistence.model.clause_tag.ClauseTag;
import com.kairos.persistence.model.template_type.TemplateType;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
public class OrganizationClause extends Clause {

    private Long organizationId;

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public OrganizationClause(@NotBlank String title, @NotNull String description, @NotEmpty List<ClauseTag> tags, List<TemplateType> templateTypes, Long organizationId) {
        super(title, description, tags, templateTypes);
        this.organizationId = organizationId;
    }

    public OrganizationClause(@NotBlank String title, @NotNull String description, Long organizationId) {
        super(title, description);
        this.organizationId = organizationId;
    }

    public OrganizationClause(){

    }

}
