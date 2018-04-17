package com.planning.responseDto.workingTimeAgreement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;


/**
 * Created by Pradeep singh on 5/8/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MinimumWeeklyRestPeriodDTO {

    private long continuousWeekRest;
    private int weight;
    private String level;
    private String templateType;

    public MinimumWeeklyRestPeriodDTO(long continuousWeekRest, int weight, String level) {
        this.continuousWeekRest = continuousWeekRest;
        this.weight = weight;
        this.level = level;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public long getContinuousWeekRest() {
        return continuousWeekRest;
    }

    public void setContinuousWeekRest(long continuousWeekRest) {
        this.continuousWeekRest = continuousWeekRest;
    }

    public MinimumWeeklyRestPeriodDTO(long continuousWeekRest) {
        this.continuousWeekRest = continuousWeekRest;
    }

    public MinimumWeeklyRestPeriodDTO() {
    }


}
