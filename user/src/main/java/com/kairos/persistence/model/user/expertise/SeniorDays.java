package com.kairos.persistence.model.user.expertise;

import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.time.LocalDate;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO_EXPERTISE;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_CARE_DAYS;

@NodeEntity
@Getter
@Setter
public class SeniorDays extends UserBaseEntity{
    @Relationship(type = BELONGS_TO_EXPERTISE)
    private Expertise expertise;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean publish;
    @Relationship(type = HAS_CARE_DAYS)
    private List<CareDays> careDays;
}
