package com.kairos.client.dto;

/**
 * Created by prabjot on 16/5/17.
 */
public class OrgTaskTypeAggregateResult {

    private String id;
    private String title;

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {

        return id;
    }

    public String getTitle() {
        return title;
    }
}
