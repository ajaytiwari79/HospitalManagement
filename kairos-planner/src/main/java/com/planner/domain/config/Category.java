package com.planner.domain.config;

import com.planner.domain.common.BaseEntity;
import org.springframework.data.cassandra.core.mapping.Table;

@Table
public class Category extends BaseEntity{

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
