package com.kairos.persistence.model.user.phase;
;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.Organization;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.persistence.model.constants.RelationshipConstants.PHASE_BELONGS_TO;

/**
 * Created by pawanmandhan on 29/8/17.
 */
@RelationshipEntity(type = PHASE_BELONGS_TO)
public class PhaseOrganizationRelation extends UserBaseEntity {

    @StartNode
    private Phase phase;

    @EndNode
    private Organization organization;

    private long durationInWeeks;
    private boolean disabled;

    public PhaseOrganizationRelation() {
    }

    public PhaseOrganizationRelation(Phase phase, Organization organization, long durationInWeeks) {
        this.phase = phase;
        this.organization = organization;
        this.durationInWeeks = durationInWeeks;
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public long getDurationInWeeks() {
        return durationInWeeks;
    }

    public void setDurationInWeeks(long durationInWeeks) {
        this.durationInWeeks = durationInWeeks;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
