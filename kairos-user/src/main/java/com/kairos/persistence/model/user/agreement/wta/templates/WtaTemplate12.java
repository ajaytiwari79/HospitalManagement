package com.kairos.persistence.model.user.agreement.wta.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by pawanmandhan on 5/8/17.
 */@NodeEntity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WtaTemplate12 extends WTABaseRuleTemplate {

    private long maximumVeto;

    public long getMaximumVeto() {
        return maximumVeto;
    }

    public void setMaximumVeto(long maximumVeto) {
        this.maximumVeto = maximumVeto;
    }

    public WtaTemplate12(String name, String templateType,boolean isActive,
                         String description, long maximumVeto) {
        this.name = name;
        this.templateType = templateType;
        this.isActive = isActive;
        this.description = description;
        this.maximumVeto=maximumVeto;

    }
    public WtaTemplate12() {
    }

}
