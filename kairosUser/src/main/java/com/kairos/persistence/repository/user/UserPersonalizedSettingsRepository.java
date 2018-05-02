package com.kairos.persistence.repository.user;

import com.kairos.persistence.model.user.user_personalized_settings.UserPersonalizedSettings;
import com.kairos.persistence.model.user.user_personalized_settings.UserPersonalizedSettingsQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;

/**
 * Created by yatharth on 1/5/18.
 */
public interface UserPersonalizedSettingsRepository extends Neo4jBaseRepository<UserPersonalizedSettings,Long> {

    @Query("Match (user:User)-[:HAS_PERSONALIZED_SETTINGS]->(userPersonalizedSettings:UserPersonalizedSettings)-[:HAS_PERSONALIZED_SETTINGS]-(selfRosteringView:SelfRosteringView) where id(user)={0} return userPersonalizedSettings, selfRosteringView")
    public UserPersonalizedSettingsQueryResult findAllByUser(Long userId);

    @Query("Match(user:User)-[:HAS_PERSONALIZED_SETTINGS]->(userPersonalizedSettings:UserPersonalizedSettings{deleted:false}) where id(user)={0} return userPersonalizedSettings")
    public UserPersonalizedSettings findByUser(Long userId);

}
