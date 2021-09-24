package com.kairos.persistence.model.user_personalized_settings;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.AbsenceViewSettings;
import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by yatharth on 1/5/18.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@NodeEntity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SelfRosteringView extends UserBaseEntity {

    private AbsenceViewSettings absenceViewSettings;

}
