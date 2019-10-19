package com.kairos.dto.planner.vrp.vrpPlanning;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Set;

/**
 * @author pradeep
 * @date - 20/6/18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class EmployeeDTO {

    private String id;
    private String name;
    private Set<String> skills;
    private int efficiency;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        EmployeeDTO that = (EmployeeDTO) o;

        return new EqualsBuilder()
                .append(efficiency, that.efficiency)
                .append(id, that.id)
                .append(name, that.name)
                .append(skills, that.skills)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(skills)
                .append(efficiency)
                .toHashCode();
    }
}
