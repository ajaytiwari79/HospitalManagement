package com.kairos.persistence.model.system_setting;

import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SystemLanguage extends UserBaseEntity {
    private String name;
    private String code;
    private boolean active;
    private boolean defaultLanguage;

}
