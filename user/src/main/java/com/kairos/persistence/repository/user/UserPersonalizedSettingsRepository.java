package com.kairos.persistence.repository.user;

import com.kairos.persistence.model.user_personalized_settings.UserPersonalizedSettings;
import com.kairos.persistence.model.user_personalized_settings.UserPersonalizedSettingsQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_PERSONALIZED_SETTINGS;

/**
 * Created by yatharth on 1/5/18.
 */
public interface UserPersonalizedSettingsRepository extends Neo4jBaseRepository<UserPersonalizedSettings,Long> {

    @Query("Match (user:User)-[:"+HAS_PERSONALIZED_SETTINGS +"]->(userPersonalizedSettings:UserPersonalizedSettings{deleted:false})-[:"+HAS_PERSONALIZED_SETTINGS +"]-(selfRosteringView:SelfRosteringView) where id(user)={0} return userPersonalizedSettings, selfRosteringView")
     UserPersonalizedSettingsQueryResult findAllByUser(Long userId);

}
