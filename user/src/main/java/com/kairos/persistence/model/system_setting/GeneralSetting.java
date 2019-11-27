package com.kairos.persistence.model.system_setting;

import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created By G.P.Ranjan on 25/11/19
 **/
@NodeEntity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeneralSetting extends UserBaseEntity {
    private short idleTimeInMinutes;
    private short awayTimeInMinutes;
}
