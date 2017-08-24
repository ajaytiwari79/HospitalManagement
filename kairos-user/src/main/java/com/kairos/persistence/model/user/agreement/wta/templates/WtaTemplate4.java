package com.kairos.persistence.model.user.agreement.wta.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by pawanmandhan on 5/8/17.
 */
@NodeEntity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WtaTemplate4 extends WTABaseRuleTemplate {

    private String minimumRest;//hh:mm
    private long daysWorked;

    public String getMinimumRest() {
        return minimumRest;
    }

    public void setMinimumRest(String minimumRest) {
        this.minimumRest = minimumRest;
    }

    public long getDaysWorked() {
        return daysWorked;
    }

    public void setDaysWorked(long daysWorked) {
        this.daysWorked = daysWorked;
    }


    public WtaTemplate4(String name, String templateType, boolean isActive, String description,String minimumRest, long daysWorked) {
        this.name=name;
        this.templateType=templateType;
        this.isActive=isActive;
        this.description=description;

        this.minimumRest = minimumRest;
        this.daysWorked = daysWorked;

    }
    public WtaTemplate4() {
    }

}
