package com.kairos.persistence.model.user.expertise;

import com.kairos.persistence.model.common.UserBaseEntity;
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
public class SeniorCareDays  extends UserBaseEntity {
    private LocalDate startDate;
    private LocalDate endDate;
    @Relationship(type = HAS_CARE_DAYS)
    private List<CareDays> seniorDays;
    private boolean published;
    @Relationship(type = APPLICABLE_FOR_EXPERTISE)
    private Expertise expertise;
    private boolean hasDraftCopy;
    @Relationship(type = VERSION_OF)
    private SeniorCareDays parentCareDays;
}
