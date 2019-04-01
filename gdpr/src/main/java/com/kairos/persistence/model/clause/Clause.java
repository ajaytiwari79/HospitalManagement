package com.kairos.persistence.model.clause;


import com.kairos.annotations.PermissionMethod;
import com.kairos.annotations.PermissionModel;
import com.kairos.persistence.model.clause_tag.ClauseTag;
import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.template_type.TemplateType;

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
public class Clause extends BaseEntity {

    @NotBlank(message = "error.message.title.notNull.orEmpty")
    private String title;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<ClauseTag> tags  = new ArrayList<>();

    // Mandatory or not to be discussed with Ulrik
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
    public Clause() {
    }

    public List<TemplateType> getTemplateTypes() {
        return templateTypes;
    }

    @PermissionMethod(value = "templateType")
    public void setTemplateTypes(List<TemplateType> templateTypes) {
        this.templateTypes = templateTypes;
    }


    public Long getParentClauseId() {
        return parentClauseId;
    }

    public void setParentClauseId(Long parentClauseId) {
        this.parentClauseId = parentClauseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ClauseTag> getTags() {
        return tags;
    }

    public void setTags(List<ClauseTag> tags) {
        this.tags = tags;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public UUID getTempClauseId() {
        return tempClauseId;
    }

    public void setTempClauseId(UUID tempClauseId) {
        this.tempClauseId = tempClauseId;
    }


}
