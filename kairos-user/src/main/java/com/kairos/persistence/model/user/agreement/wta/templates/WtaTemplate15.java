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
public class WtaTemplate15 extends WTABaseRuleTemplate {

    private String continuousDayRestHours;

    public String getContinuousDayRestHours() {
        return continuousDayRestHours;
    }

    public void setContinuousDayRestHours(String continuousDayRestHours) {
        this.continuousDayRestHours = continuousDayRestHours;
    }

    public WtaTemplate15(String name, String templateType, boolean isActive,
                         String description, String continuousDayRestHours) {
        this.name = name;
        this.templateType = templateType;
        this.isActive = isActive;
        this.description = description;
        this.continuousDayRestHours=continuousDayRestHours;
    }

    public WtaTemplate15() {

    }

}