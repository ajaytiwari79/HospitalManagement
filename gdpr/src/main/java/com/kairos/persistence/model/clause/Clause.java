package com.kairos.persistence.model.clause;

import com.kairos.persistence.model.clause_tag.ClauseTag;
import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.template_type.TemplateType;
import lombok.*;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.*;

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
