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
public class WtaTemplate14 extends WTABaseRuleTemplate {

    private long interval;//
    private long validationStartDate;

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public long getValidationStartDate() {
        return validationStartDate;
    }

    public void setValidationStartDate(long validationStartDate) {
        this.validationStartDate = validationStartDate;
    }

    public WtaTemplate14(String name, String templateType,  boolean isActive,
                         String description, long interval, long validationStartDate) {
        this.name = name;
        this.templateType = templateType;
        this.isActive = isActive;
        this.description = description;

        this.interval=interval;
        this.validationStartDate=validationStartDate;
    }

    public WtaTemplate14() {
    }
}
