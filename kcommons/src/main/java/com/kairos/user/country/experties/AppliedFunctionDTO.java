package com.kairos.user.country.experties;


import java.util.List;

/**
 * Created by oodles on 1/6/18.
 */
public class AppliedFunctionDTO {


    private Long id;
    private String name;
    private String icon;
    private List<Long> appliedDates;

    public AppliedFunctionDTO() {

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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<Long> getAppliedDates() {
        return appliedDates;
    }

    public void setAppliedDates(List<Long> appliedDates) {
        this.appliedDates = appliedDates;
    }
}

