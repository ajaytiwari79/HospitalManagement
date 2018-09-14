package com.kairos.dto.activity.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.activity.activity_tabs.GeneralActivityTabDTO;

import java.math.BigInteger;

/**
 * Created by pavan on 8/2/18.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompositeActivityDTO {
    private BigInteger id;
    // TODO CHECK HOW TO UPDATE ID DYNAMICALLY
    private BigInteger compositeId;
    private String name;
    private String description;
    private Long countryId;
    private BigInteger categoryId;
    private String categoryName;
    private Long unitId ;
    private GeneralActivityTabDTO generalActivityTab;
    private Long countryActivityId;
    private Boolean allowedBefore;
    private Boolean allowedAfter;

    public CompositeActivityDTO() {
        //default Constructor
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
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

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public BigInteger getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(BigInteger categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }


    public GeneralActivityTabDTO getGeneralActivityTab() {
        return generalActivityTab;
    }

    public void setGeneralActivityTab(GeneralActivityTabDTO generalActivityTab) {
        this.generalActivityTab = generalActivityTab;
    }

    public Long getCountryActivityId() {
        return countryActivityId;
    }

    public void setCountryActivityId(Long countryActivityId) {
        this.countryActivityId = countryActivityId;
    }

    public Boolean getAllowedBefore() {
        return allowedBefore;
    }

    public void setAllowedBefore(Boolean allowedBefore) {
        this.allowedBefore = allowedBefore;
    }

    public Boolean getAllowedAfter() {
        return allowedAfter;
    }

    public void setAllowedAfter(Boolean allowedAfter) {
        this.allowedAfter = allowedAfter;
    }

    public BigInteger getCompositeId() {
        return compositeId;
    }

    public void setCompositeId(BigInteger compositeId) {
        this.compositeId = compositeId;
    }
}
