package com.kairos.persistence.model.staff;

import com.kairos.commons.utils.DateUtils;
import com.kairos.persistence.model.organization.union.Sector;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.typeconversion.DateLong;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/**
 * Created by pavan on 7/5/18.
 */

@QueryResult
@Getter
@Setter
public class StaffExpertiseQueryResult {
    private Long id;
    private String name;
    private Long expertiseId;
    private Integer relevantExperienceInMonths;
    @DateLong
    private Date expertiseStartDate;
    private Integer nextSeniorityLevelInMonths;
    private List<SeniorityLevel> seniorityLevels;
    private Sector sector;
    private SeniorityLevel seniorityLevel;
    private boolean employmentExists;

    public Integer getRelevantExperienceInMonths() {
        return (int) ChronoUnit.MONTHS.between(DateUtils.asLocalDate(this.getExpertiseStartDate()), LocalDate.now());
    }
}
