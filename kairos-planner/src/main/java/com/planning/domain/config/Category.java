package com.planning.domain.config;

import com.planning.domain.common.BaseEntity;
import org.springframework.data.cassandra.mapping.Table;

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
