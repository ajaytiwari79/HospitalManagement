package com.planner.domain.skill;

import com.planner.domain.common.BaseEntity;
import org.springframework.data.cassandra.mapping.Table;

@Table
public class PlanningSkill extends BaseEntity {

    private String name;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
