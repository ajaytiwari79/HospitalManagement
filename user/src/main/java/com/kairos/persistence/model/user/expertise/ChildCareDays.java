package com.kairos.persistence.model.user.expertise;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.time.LocalDate;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_CARE_DAYS;

public class ChildCareDays extends UserBaseEntity{
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean publish;
    @Relationship(type = HAS_CARE_DAYS)
    private List<CareDays> careDays;
}
