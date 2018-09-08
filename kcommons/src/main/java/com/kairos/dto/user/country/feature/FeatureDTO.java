package com.kairos.dto.user.country.feature;

/**
 * Created by prerna on 4/12/17.
 */
public class FeatureDTO {

    private Long id;
    private String name;
    private String description;

    public FeatureDTO(){};

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

    public FeatureDTO(String name, String description){
        this.name = name;
        this.description = description;
    }

}
