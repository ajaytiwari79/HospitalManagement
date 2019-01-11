package com.kairos.response.dto.clause;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.master_data.ClauseTagDTO;
import com.kairos.response.dto.master_data.TemplateTypeResponseDTO;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/*
* clause basic response dto is for Agreement section
*
* */


@JsonIgnoreProperties(ignoreUnknown = true)
public class ClauseBasicResponseDTO {

    private BigInteger id;
    private String title;
    private String titleHtml;
    private String description;
    private String descriptionHtml;
    private List<ClauseTagDTO> tags = new ArrayList<>();
    private boolean linkedWithOtherTemplate;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ClauseTagDTO> getTags() {
        return tags;
    }

    public void setTags(List<ClauseTagDTO> tags) {
        this.tags = tags;
    }

    public String getTitleHtml() { return titleHtml; }

    public void setTitleHtml(String titleHtml) { this.titleHtml = titleHtml; }

    public String getDescriptionHtml() { return descriptionHtml; }

    public void setDescriptionHtml(String descriptionHtml) { this.descriptionHtml = descriptionHtml; }

    public boolean isLinkedWithOtherTemplate() {
        return linkedWithOtherTemplate;
    }

    public void setLinkedWithOtherTemplate(boolean linkedWithOtherTemplate) {
        this.linkedWithOtherTemplate = linkedWithOtherTemplate;
    }

    public ClauseBasicResponseDTO() {
    }
}
