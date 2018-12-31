package com.kairos.dto.activity.shift;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by pavan on 13/3/18.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FunctionDTO {
    private Long id;
    @NotBlank(message = "error.function.name.notEmpty")
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Long> unionIds;
    private List<Long> organizationLevelIds;
    private String icon;

    public FunctionDTO() {
        //Default Constructor
    }

    public FunctionDTO(Long id, String name, String icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
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
        this.name = StringUtils.trim(name);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = StringUtils.trim(description);
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<Long> getUnionIds() {
        return unionIds;
    }

    public void setUnionIds(List<Long> unionIds) {
        this.unionIds = unionIds;
    }

    public List<Long> getOrganizationLevelIds() {
        return organizationLevelIds;
    }

    public void setOrganizationLevelIds(List<Long> organizationLevelIds) {
        this.organizationLevelIds = organizationLevelIds;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = StringUtils.trim(icon);
    }
}