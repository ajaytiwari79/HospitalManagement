package com.kairos.persistence.model.user.expertise;

import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.time.LocalDate;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

@NodeEntity
@Getter
@Setter
public class ChildCareDays {
    private LocalDate startDate;
    private LocalDate endDate;
    @Relationship(type = HAS_CARE_DAYS)
    private List<CareDays> childDays;
}
