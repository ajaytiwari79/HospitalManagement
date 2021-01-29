package com.kairos.persistence.model.action;

import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActionInfo extends MongoBaseEntity {
    private static final long serialVersionUID = 1971473566186477192L;
    private Long unitId;
    private Long staffId;
    private Map<String,Long> actionCount;
}
