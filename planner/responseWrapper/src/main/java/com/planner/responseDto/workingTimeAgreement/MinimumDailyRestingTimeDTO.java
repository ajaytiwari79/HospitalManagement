package com.planner.responseDto.workingTimeAgreement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;


/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE15
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MinimumDailyRestingTimeDTO {

    private long continuousDayRestHours;
    private int weight;
    private String level;
    private String templateType;


    public MinimumDailyRestingTimeDTO(long continuousDayRestHours, int weight, String level) {
        this.continuousDayRestHours = continuousDayRestHours;
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

    public long getContinuousDayRestHours() {
        return continuousDayRestHours;
    }

    public void setContinuousDayRestHours(long continuousDayRestHours) {
        this.continuousDayRestHours = continuousDayRestHours;
    }

    public MinimumDailyRestingTimeDTO(long continuousDayRestHours) {
        this.continuousDayRestHours = continuousDayRestHours;
    }

    public MinimumDailyRestingTimeDTO() {

    }


}