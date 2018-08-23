package com.kairos.persistence.model.country.default_data;

import com.kairos.persistence.model.access_permission.AccessPage;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

//  Created By vipul   On 9/8/18
@QueryResult
public class UnitTypeQueryResult {
    private Long id;
    private String name;
    private String description;
    private List<AccessPage> modules;

    public UnitTypeQueryResult() {
        // dc
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public List<AccessPage> getModules() {
        return modules;
    }

    public void setModules(List<AccessPage> modules) {
        this.modules = modules;
    }
}
