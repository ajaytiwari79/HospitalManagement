package com.kairos.user.country.equipment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.response.dto.web.equipment.EquipmentCategoryDTO;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by prerna on 12/12/17.
 */
@QueryResult
@JsonIgnoreProperties(ignoreUnknown = true)
public class EquipmentQueryResult {
    private Long id;
    private String name;
    private String description;
    private Map<String,Object> category;

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

    public Map<String,Object> getCategory() {
        return category;
    }

    public void setCategory(Map<String,Object> category) {
        this.category = category;
    }
}
