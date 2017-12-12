package com.kairos.persistence.model.user.country.equipment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by prerna on 12/12/17.
 */
@QueryResult
@JsonIgnoreProperties(ignoreUnknown = true)
public class EquipmentQueryResult {
    private Long id;
    private String name;
    private String description;
    private EquipmentCategory category;

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

    public EquipmentCategory getCategory() {
        return category;
    }

    public void setCategory(EquipmentCategory category) {
        this.category = category;
    }
}
