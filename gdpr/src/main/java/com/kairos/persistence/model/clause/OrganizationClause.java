package com.kairos.persistence.model.clause;

import com.kairos.persistence.model.clause_tag.ClauseTag;
import com.kairos.persistence.model.template_type.TemplateType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class OrganizationClause extends Clause {

    private Long organizationId;

    public OrganizationClause(@NotBlank String title, @NotNull String description, @NotEmpty List<ClauseTag> tags, List<TemplateType> templateTypes, Long organizationId, UUID tempClauseId) {
        super(title, description, tags, templateTypes,tempClauseId);
        this.organizationId = organizationId;
    }

    public OrganizationClause(@NotBlank String title, @NotNull String description, Long organizationId) {
        super(title, description);
        this.organizationId = organizationId;
    }

}
