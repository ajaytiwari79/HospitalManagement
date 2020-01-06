package com.kairos.persistence.model.staff.personal_details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.user.country.experties.SeniorityLevelDTO;
import com.kairos.dto.user.organization.union.SectorDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.typeconversion.DateLong;

import java.util.Date;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class StaffExpertiseDTO {

    private Long id;
    private String name;
    private Long expertiseId;
    private Integer relevantExperienceInMonths;
    private Date expertiseStartDate;
    private Integer nextSeniorityLevelInMonths;
    private List<SeniorityLevelDTO> seniorityLevels;
    private SectorDTO sector;
    private SeniorityLevelDTO seniorityLevel;
    private boolean employmentExists;

}
