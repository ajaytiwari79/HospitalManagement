package com.kairos.persistence.model.clause;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@Embeddable
public class ClauseCkEditorVO {

    @NotNull
    private Long id;
    @NotNull
    private String titleHtml;
    @NotNull
    private String description;

    private String descriptionHtml;

    private UUID tempClauseId;

    private boolean deleted;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getTitleHtml() { return titleHtml; }

    public void setTitleHtml(String titleHtml) { this.titleHtml = titleHtml; }

    public String getDescriptionHtml() { return descriptionHtml; }

    public void setDescriptionHtml(String descriptionHtml) { this.descriptionHtml = descriptionHtml; }

    public UUID getTempClauseId() {
        return this.tempClauseId;
    }

    public void setTempClauseId(UUID tempClauseId) {
        this.tempClauseId = tempClauseId;
    }

    public ClauseCkEditorVO(Long id, String titleHtml, String descriptionHtml, String description) {
        this.id = id;
        this.titleHtml = titleHtml;
        this.descriptionHtml = descriptionHtml;
        this.description  = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public ClauseCkEditorVO() {
    }
}
