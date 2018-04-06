package com.kairos.persistence.model.user.agreement.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.user.agreement.wta.templates.WTABaseRuleTemplate;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.List;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE8
 */
@NodeEntity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MinimumRestConsecutiveNightsWTATemplate extends WTABaseRuleTemplate {


    private List<String> balanceType;//multiple check boxes
    private long minimumRest;
    private long nightsWorked;

    public List<String> getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(List<String> balanceType) {
        this.balanceType = balanceType;
    }

    public long getMinimumRest() {
        return minimumRest;
    }

    public void setMinimumRest(long minimumRest) {
        this.minimumRest = minimumRest;
    }

    public long getNightsWorked() {
        return nightsWorked;
    }

    public void setNightsWorked(long nightsWorked) {
        this.nightsWorked = nightsWorked;
    }

    public MinimumRestConsecutiveNightsWTATemplate(String name, String templateType, boolean disabled, String description, List<String> balanceType, long minimumRest, long nightsWorked) {
        this.nightsWorked = nightsWorked;
        this.balanceType = balanceType;
        this.minimumRest=minimumRest;
        this.name = name;
        this.templateType = templateType;
        this.disabled = disabled;
        this.description = description;

    }
    public MinimumRestConsecutiveNightsWTATemplate() {
    }
}
