package com.kairos.dto.activity.counter.configuration;

import java.util.List;

public class KPIFilterDefaultDataDTO<T> {
    private T id;
    private String name;
    private Long unitId;
    private List<Long> unitIds;

    public KPIFilterDefaultDataDTO() {
    }

    public KPIFilterDefaultDataDTO(T id, String name) {
        this.id = id;
        this.name = name;
    }

    public KPIFilterDefaultDataDTO(T id, String name, Long unitId) {
        this.id = id;
        this.name = name;
        this.unitId = unitId;
    }

    public KPIFilterDefaultDataDTO(T id, String name, List<Long> unitIds) {
        this.id = id;
        this.name = name;
        this.unitIds = unitIds;
    }

    public T getId() {
        return id;
    }

    public void setId(T id) {
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
