package com.kairos.persistence.model.pay_table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.user.pay_group_area.PayGroupArea;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * Created by vipul on 16/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@QueryResult
public class OrganizationLevelPayGroupAreaDTO {

    private Long id;
    private String name;
    private String description;
    private Integer payTablesCount;
    private List<PayGroupArea> payGroupAreas;

    public OrganizationLevelPayGroupAreaDTO() {
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

    public Integer getPayTablesCount() {
        return payTablesCount;
    }

    public void setPayTablesCount(Integer payTablesCount) {
        this.payTablesCount = payTablesCount;
    }

    public List<PayGroupArea> getPayGroupAreas() {
        return payGroupAreas;
    }

    public void setPayGroupAreas(List<PayGroupArea> payGroupAreas) {
        this.payGroupAreas = payGroupAreas;
    }
}
