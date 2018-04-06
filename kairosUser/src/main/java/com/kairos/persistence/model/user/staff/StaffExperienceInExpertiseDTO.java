package com.kairos.persistence.model.user.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.neo4j.ogm.annotation.typeconversion.DateLong;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.Date;

/**
 * Created by pavan on 27/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@QueryResult
public class StaffExperienceInExpertiseDTO {
    private Long id;
    private String name;
    private Long expertiseId;
    private Integer relevantExperienceInMonths;
    @DateLong
    private Date expertiseStartDate;


    public StaffExperienceInExpertiseDTO() {
        //Default Constructor
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

    public Long getExpertiseId() {
        return expertiseId;
    }

    public void setExpertiseId(Long expertiseId) {
        this.expertiseId = expertiseId;
    }

    public Integer getRelevantExperienceInMonths() {
        return relevantExperienceInMonths;
    }

    public void setRelevantExperienceInMonths(Integer relevantExperienceInMonths) {
        this.relevantExperienceInMonths = relevantExperienceInMonths;
    }

    public Date getExpertiseStartDate() {
        return expertiseStartDate;
    }

    public void setExpertiseStartDate(Date expertiseStartDate) {
        this.expertiseStartDate = expertiseStartDate;
    }

    public StaffExperienceInExpertiseDTO(Long id, String name, Long expertiseId, Integer relevantExperienceInMonths, Date expertiseStartDate) {
        this.id = id;
        this.name = name;
        this.expertiseId = expertiseId;
        this.relevantExperienceInMonths = relevantExperienceInMonths;
        this.expertiseStartDate = expertiseStartDate;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("StaffExperienceInExpertiseDTO{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", expertiseId=").append(expertiseId);
        sb.append(", relevantExperienceInMonths=").append(relevantExperienceInMonths);
        sb.append(", expertiseStartDate=").append(expertiseStartDate);
        sb.append('}');
        return sb.toString();
    }
}
