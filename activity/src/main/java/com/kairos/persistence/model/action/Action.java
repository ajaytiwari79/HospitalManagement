package com.kairos.persistence.model.action;

import com.kairos.enums.ActionType;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created By G.P.Ranjan on 2/4/20
 **/
@Getter
@Setter
@NoArgsConstructor
public class Action extends MongoBaseEntity {

    private ActionType name;
    private String description;
    private Long unitId;
}
