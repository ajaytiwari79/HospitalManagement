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
public class WtaTemplate6 extends WTABaseRuleTemplate {


    private long numberOfDays;

    public long getNumberOfDays() {
        return numberOfDays;
    }

    public void setNumberOfDays(long number) {
        this.numberOfDays = numberOfDays;
    }

    public WtaTemplate6(String name, String templateType,  boolean isActive, String description, long numberOfDays) {
        super(name, templateType, description);
        this.numberOfDays = numberOfDays;
    }

    public WtaTemplate6() {
    }
}
