package com.kairos.persistence.model.organization.team;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.skill.Skill;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.TEAM_HAS_LOCATION;
import static com.kairos.persistence.model.constants.RelationshipConstants.TEAM_HAS_SKILLS;


/**
 * Created by prabjot on 9/20/16.
 */
@NodeEntity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Team extends UserBaseEntity {

    private static final long serialVersionUID = -7287662943308577836L;
    private String name;
    private String description;

    @Relationship(type = TEAM_HAS_SKILLS)
    private List<Skill> skillList=new ArrayList<>();

    @Relationship(type = TEAM_HAS_LOCATION)
    private ContactAddress contactAddress;

    private boolean isEnabled = true;
    private Set<BigInteger> activityIds=new HashSet<>();


    public Team(String name, String description,  ContactAddress contactAddress) {
        this.name = name;
        this.description = description;
        this.contactAddress = contactAddress;
    }


}
