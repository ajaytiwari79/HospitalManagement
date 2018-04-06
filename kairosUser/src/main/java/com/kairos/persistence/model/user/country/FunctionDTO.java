package com.kairos.persistence.model.user.country;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.Organization;
import org.neo4j.ogm.annotation.typeconversion.DateLong;
import org.springframework.data.neo4j.annotation.QueryResult;
import java.util.Date;
import java.util.List;

/**
 * Created by pavan on 14/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@QueryResult
public class FunctionDTO {
    private Long id;
    private String name;
    private String description;
    @DateLong
    private Date startDate;
    @DateLong
    private Date endDate;
    private List<Organization> unions;
    private List<Level> organizationLevels;



    public FunctionDTO() {
        //Default Constructor
    }

    public FunctionDTO(Long id, String name, String description, Date startDate, Date endDate, List<Organization> unions, List<Level> organizationLevels) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.unions = unions;
        this.organizationLevels = organizationLevels;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<Organization> getUnions() {
        return unions;
    }

    public void setUnions(List<Organization> unions) {
        this.unions = unions;
    }

    public List<Level> getOrganizationLevels() {
        return organizationLevels;
    }

    public void setOrganizationLevels(List<Level> organizationLevels) {
        this.organizationLevels = organizationLevels;
    }

}
