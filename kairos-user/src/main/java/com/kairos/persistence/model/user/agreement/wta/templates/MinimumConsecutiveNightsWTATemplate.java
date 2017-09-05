package com.kairos.persistence.model.user.agreement.wta.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE6
 */
@NodeEntity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MinimumConsecutiveNightsWTATemplate extends WTABaseRuleTemplate {


    private long daysLimit;

    public long getDaysLimit() {
        return daysLimit;
    }

    public void setDaysLimit(long daysLimit) {
        this.daysLimit = daysLimit;
    }

    public MinimumConsecutiveNightsWTATemplate(String name, String templateType, boolean isActive, String description, long daysLimit) {
        super(name, templateType, description);
        this.daysLimit = daysLimit;
    }

    public MinimumConsecutiveNightsWTATemplate() {
    }
}
