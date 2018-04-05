package com.kairos.persistence.model.user.agreement.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.user.agreement.wta.templates.WTABaseRuleTemplate;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE15
 */
@NodeEntity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MinimumDailyRestingTimeWTATemplate extends WTABaseRuleTemplate {

    private long continuousDayRestHours;

    public long getContinuousDayRestHours() {
        return continuousDayRestHours;
    }

    public void setContinuousDayRestHours(long continuousDayRestHours) {
        this.continuousDayRestHours = continuousDayRestHours;
    }

    public MinimumDailyRestingTimeWTATemplate(String name, String templateType, boolean disabled,
                                              String description, long continuousDayRestHours) {
        this.name = name;
        this.templateType = templateType;
        this.disabled = disabled;
        this.description = description;
        this.continuousDayRestHours=continuousDayRestHours;
    }

    public MinimumDailyRestingTimeWTATemplate() {

    }

}