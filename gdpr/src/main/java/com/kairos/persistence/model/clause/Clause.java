package com.kairos.persistence.model.clause;


import com.kairos.annotations.PermissionModel;
import com.kairos.persistence.model.clause_tag.ClauseTag;
import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.template_type.TemplateType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@PermissionModel
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Clause extends BaseEntity {

    @NotBlank(message = "error.message.title.notNull.orEmpty")
    private String title;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<ClauseTag> tags  = new ArrayList<>();

    @NotNull
    @Column(columnDefinition = "text")
    private String description;


    private Long parentClauseId;

    @ManyToMany
    private List<TemplateType> templateTypes  = new ArrayList<>();

    @Nullable
    private UUID tempClauseId;

    public Clause(@NotBlank String title, @NotNull String description) {
        this.title = title;
        this.description = description;
    }

    public Clause(@NotBlank String title, @NotNull String description, @NotEmpty List<ClauseTag> tags, List<TemplateType> templateTypes) {
        this.title = title;
        this.description = description;
        this.tags=tags;
        this.templateTypes=templateTypes;
    }

    public Clause(@NotBlank String title, @NotNull String description, @NotEmpty List<ClauseTag> tags, List<TemplateType> templateTypes,UUID tempClauseId) {
        this.title = title;
        this.description = description;
        this.tags=tags;
        this.templateTypes=templateTypes;
        this.tempClauseId=tempClauseId;
    }

}
