package com.kairos.persistence.model.user.agreement.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.user.agreement.wta.templates.WTABaseRuleTemplate;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.List;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE16
 */
@NodeEntity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MinimumDurationBetweenShiftWTATemplate extends WTABaseRuleTemplate {

    private List<String> balanceType;
    private long minimumDurationBetweenShifts;

    public List<String> getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(List<String> balanceType) {
        this.balanceType = balanceType;
    }

    public long getMinimumDurationBetweenShifts() {
        return minimumDurationBetweenShifts;
    }

    public void setMinimumDurationBetweenShifts(long minimumDurationBetweenShifts) {
        this.minimumDurationBetweenShifts = minimumDurationBetweenShifts;
    }

    public MinimumDurationBetweenShiftWTATemplate(String name, String templateType, boolean isActive,
                                                  String description, List<String> balanceType, long minimumDurationBetweenShifts) {
        this.name = name;
        this.templateType = templateType;
        this.isActive = isActive;
        this.description = description;
        this.balanceType= balanceType;
        this.minimumDurationBetweenShifts=minimumDurationBetweenShifts;

    }
    public MinimumDurationBetweenShiftWTATemplate() {
    }
    }