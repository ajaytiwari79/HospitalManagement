package com.kairos.persistence.model.user.agreement.wta.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE4
 */
@NodeEntity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MinimumRestInConsecutiveDaysWTATemplate extends WTABaseRuleTemplate {

    private long minimumRest;//hh:mm
    private long daysWorked;

    public long getMinimumRest() {
        return minimumRest;
    }

    public void setMinimumRest(long minimumRest) {
        this.minimumRest = minimumRest;
    }

    public long getDaysWorked() {
        return daysWorked;
    }

    public void setDaysWorked(long daysWorked) {
        this.daysWorked = daysWorked;
    }


    public MinimumRestInConsecutiveDaysWTATemplate(String name, String templateType, boolean isActive, String description, long minimumRest, long daysWorked) {
        this.name=name;
        this.templateType=templateType;
        this.isActive=isActive;
        this.description=description;

        this.minimumRest = minimumRest;
        this.daysWorked = daysWorked;

    }
    public MinimumRestInConsecutiveDaysWTATemplate() {
    }

}
