package com.kairos.persistence.repository.system_setting;

import com.kairos.persistence.model.system_setting.GeneralSetting;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

/**
 * Created By G.P.Ranjan on 25/11/19
 **/
@Repository
public interface GeneralSettingGraphRepository extends Neo4jBaseRepository<GeneralSetting,Long> {
}
