package com.kairos.activity.persistence.model.wta;

/**
 * @author pradeep
 * @date - 11/4/18
 */

public class WTAExpertise {
    private Long id;
    private String name;
    private String description;

    public WTAExpertise() {
    }

    public WTAExpertise(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

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
