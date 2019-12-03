package com.kairos.persistence.model.user_personalized_settings;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by yatharth on 2/5/18.
 */
@QueryResult
@Getter
@Setter
public class UserPersonalizedSettingsQueryResult {
    private UserPersonalizedSettings userPersonalizedSettings;
    private SelfRosteringView selfRosteringView;

}
