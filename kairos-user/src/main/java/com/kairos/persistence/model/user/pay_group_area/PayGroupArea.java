package com.kairos.persistence.model.user.pay_group_area;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.user.region.Municipality;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.math.BigDecimal;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_MUNICIPALITY;
import static com.kairos.persistence.model.constants.RelationshipConstants.IN_LEVEL;

/**
 * @Created by prabjot on 20/12/17.
 * @Modified by VIPUl for KP-2320 on 9-March-18
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NodeEntity
public class PayGroupArea extends UserBaseEntity {
    private String name;
    private String description;
    @Relationship(type = IN_LEVEL)
    private Level level;

    public PayGroupArea() {
        //default constructor
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
}
