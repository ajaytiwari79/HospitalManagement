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
public class MinimumWeeklyRestPeriodWTATemplate extends WTABaseRuleTemplate {

    private long continuousWeekRest;

    public long getContinuousWeekRest() {
        return continuousWeekRest;
    }

    public void setContinuousWeekRest(long continuousWeekRest) {
        this.continuousWeekRest = continuousWeekRest;
    }

    public MinimumWeeklyRestPeriodWTATemplate(String name, String templateType, boolean isActive,
                                              String description, long continuousWeekRest) {
        this.name = name;
        this.templateType = templateType;
        this.isActive = isActive;
        this.description = description;

        this.continuousWeekRest=continuousWeekRest;

    }

    public MinimumWeeklyRestPeriodWTATemplate() {
    }
}
