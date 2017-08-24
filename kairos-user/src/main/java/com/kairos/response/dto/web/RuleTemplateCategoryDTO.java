package com.kairos.response.dto.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.user.country.Country;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by vipul on 2/8/17.
 */
@QueryResult
@JsonIgnoreProperties(ignoreUnknown = true)

public class RuleTemplateCategoryDTO {
    private Long id;
    private String name;
    private String description;
    private Country country;

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
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

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+"+, name = "+name+", description="+description+"]";
    }

}
