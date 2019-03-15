package com.kairos.dto.gdpr.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClauseBasicDTO {


    private Long id;

    @NotBlank(message = "error.message.title.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private String title;

    private String titleHtml;

    @NotBlank(message = "error.message.description.notNull.orEmpty")
    private String description;

    private String descriptionHtml;

    private boolean requireUpdate;

    @NotNull(message = "error.message.clause.order")
    private Integer orderedIndex;

    private UUID tempClauseId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() { return title; }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description.trim();
    }

    public boolean isRequireUpdate() {
        return requireUpdate;
    }

    public void setRequireUpdate(boolean requireUpdate) {
        this.requireUpdate = requireUpdate;
    }

    public Integer getOrderedIndex() {
        return orderedIndex;
    }

    public void setOrderedIndex(Integer orderedIndex) {
        this.orderedIndex = orderedIndex;
    }

    public String getTitleHtml() {

        if (titleHtml == null) {
            titleHtml = "<p>"+title+"</p>";
        }
        return titleHtml.trim();
    }

    public void setTitleHtml(String titleHtml) {
        this.titleHtml = titleHtml;
    }

    public String getDescriptionHtml() {
        if (descriptionHtml == null) {
            descriptionHtml = "<p>"+description+"</p>";
        }
        return descriptionHtml.trim();
    }

    public void setDescriptionHtml(String descriptionHtml) {
        this.descriptionHtml = descriptionHtml;
    }

    public UUID getTempClauseId() {
        return this.tempClauseId;
    }

    public void setTempClauseId(UUID tempClauseId) {
        this.tempClauseId = tempClauseId;
    }

    public ClauseBasicDTO() {
        this.tempClauseId = UUID.randomUUID();
    }
}
