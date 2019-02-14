package com.kairos.dto.activity.counter.configuration;

import java.util.List;

public class kpiFilterDefaultDataDTO {
    private Long id;
    private String name;
    private Long unitId;
    private List<Long> unitIds;

    public kpiFilterDefaultDataDTO() {
    }

    public kpiFilterDefaultDataDTO(Long id, String name, Long unitId) {
        this.id = id;
        this.name = name;
        this.unitId = unitId;
    }

    public kpiFilterDefaultDataDTO(Long id, String name, Long unitId, List<Long> unitIds) {
        this.id = id;
        this.name = name;
        this.unitId = unitId;
        this.unitIds = unitIds;
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

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public List<Long> getUnitIds() {
        return unitIds;
    }

    public void setUnitIds(List<Long> unitIds) {
        this.unitIds = unitIds;
    }
}
