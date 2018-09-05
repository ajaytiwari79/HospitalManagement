package com.planner.domain.query_results;

import com.kairos.shiftplanning.enums.SkillType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.Objects;

@QueryResult
public class SkillQueryResult {

    private String id;
    private String name;
    private SkillType skillType;
    private int weight;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SkillType getSkillType() {
        return skillType;
    }

    public void setSkillType(SkillType skillType) {
        this.skillType = skillType;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SkillQueryResult that = (SkillQueryResult) o;

        return new EqualsBuilder()
                .append(weight, that.weight)
                .append(id, that.id)
                .append(name, that.name)
                .append(skillType, that.skillType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(skillType)
                .append(weight)
                .toHashCode();
    }
}
