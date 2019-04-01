package com.kairos.persistence.model.wta;

import java.util.List;

/**
 * @author pradeep
 * @date - 11/4/18
 */

public class Expertise {
    private Long id;
    private String name;
    private String description;
    private List<Long> tags;

    public Expertise() {
    }

    public Expertise(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Expertise(Long id, String name, String description ,List<Long> tags) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tags=tags;

    }

    public List<Long> getTags() { return tags; }

    public void setTags(List<Long> tags) { this.tags = tags; }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
