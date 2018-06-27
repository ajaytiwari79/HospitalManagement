package com.kairos.user.agreement.cta;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class CTARuleTemplatePhaseInfo extends UserBaseEntity{
    private Long phaseId;
    private phaseType type;
    private int beforeStart;
    public enum  phaseType{
        DAYS,HOURS;
    }

    public Long getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(Long phaseId) {
        this.phaseId = phaseId;
    }

    public phaseType getType() {
        return type;
    }

    public void setType(phaseType type) {
        this.type = type;
    }

    public int getBeforeStart() {
        return beforeStart;
    }

    public void setBeforeStart(int beforeStart) {
        this.beforeStart = beforeStart;
    }
}
