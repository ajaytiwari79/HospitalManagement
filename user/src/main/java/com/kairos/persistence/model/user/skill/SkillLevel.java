package com.kairos.persistence.model.user.skill;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Skill Level Domain
 */

@NodeEntity
public class SkillLevel extends UserBaseEntity {
    @GraphId
    private Long id;

    private String name;

    public SkillLevel() {
    }

    public SkillLevel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Long getId() {
        return id;
    }
}
