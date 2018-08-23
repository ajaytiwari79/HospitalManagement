package com.kairos.persistence.model.user_personalized_settings;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_PERSONALIZED_SETTINGS;

/**
 * Created by yatharth on 1/5/18.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@NodeEntity
public class UserPersonalizedSettings extends UserBaseEntity {

    @Relationship(type = HAS_PERSONALIZED_SETTINGS)
    private SelfRosteringView selfRosteringView;

    public UserPersonalizedSettings() {
        //Default Constructor
    }
    public UserPersonalizedSettings(SelfRosteringView selfRosteringView) {
        this.selfRosteringView = selfRosteringView;
    }
    public SelfRosteringView getSelfRosteringView() {
        return selfRosteringView;
    }

    public void setSelfRosteringView(SelfRosteringView selfRosteringView) {
        this.selfRosteringView = selfRosteringView;
    }

}
