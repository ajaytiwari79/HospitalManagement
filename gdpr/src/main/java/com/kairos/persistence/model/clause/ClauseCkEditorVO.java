package com.kairos.persistence.model.clause;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigInteger;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClauseCkEditorVO {


    private Long id;
    private String titleHtml;
    private String descriptionHtml;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getTitleHtml() { return titleHtml; }

    public void setTitleHtml(String titleHtml) { this.titleHtml = titleHtml; }

    public String getDescriptionHtml() { return descriptionHtml; }

    public void setDescriptionHtml(String descriptionHtml) { this.descriptionHtml = descriptionHtml; }

    public ClauseCkEditorVO(Long id, String titleHtml, String descriptionHtml) {
        this.id = id;
        this.titleHtml = titleHtml;
        this.descriptionHtml = descriptionHtml;
    }
}
