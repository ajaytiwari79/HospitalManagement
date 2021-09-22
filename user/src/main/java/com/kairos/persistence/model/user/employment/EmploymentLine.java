package com.kairos.persistence.model.user.employment;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.functions.Function;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import lombok.*;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_FUNCTION;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_SENIORITY_LEVEL;

/**
 * CreatedBy vipulpandey on 24/9/18
 **/
@NodeEntity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmploymentLine extends UserBaseEntity {
    private static final long serialVersionUID = -7588894651726243264L;
    @Relationship(type = HAS_SENIORITY_LEVEL)
    private SeniorityLevel seniorityLevel;
    @Relationship(type = HAS_FUNCTION)
    private List<Function> functions;
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalWeeklyMinutes;
    private int fullTimeWeeklyMinutes;  // Its coming from expertise
    private float avgDailyWorkingHours;
    private int workingDaysInWeek;       // same from expertise
    private BigDecimal hourlyCost;          // payGroupArea

}
