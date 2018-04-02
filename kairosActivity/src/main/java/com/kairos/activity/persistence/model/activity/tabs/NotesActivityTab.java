package com.kairos.activity.persistence.model.activity.tabs;

import java.io.Serializable;

/**
 * Created by vipul on 24/8/17.
 */
public class NotesActivityTab implements Serializable{
    private String content;
    private String originalDocumentName;
    private String modifiedDocumentName;

    public NotesActivityTab() {
        //Default Constructor
    }

    public NotesActivityTab(String content,String originalDocumentName,String modifiedDocumentName) {
        this.content = content;
        this.originalDocumentName=originalDocumentName;
        this.modifiedDocumentName=modifiedDocumentName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOriginalDocumentName() {
        return originalDocumentName;
    }

    public void setOriginalDocumentName(String originalDocumentName) {
        this.originalDocumentName = originalDocumentName;
    }

    public String getModifiedDocumentName() {
        return modifiedDocumentName;
    }

    public void setModifiedDocumentName(String modifiedDocumentName) {
        this.modifiedDocumentName = modifiedDocumentName;
    }
}
