package com.kairos.persistence.model.user.open_shift;

import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.counter.configuration.CounterDTO;
import com.kairos.dto.activity.open_shift.OpenShiftIntervalDTO;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO;
import com.kairos.dto.user.country.experties.ExpertiseResponseDTO;
import com.kairos.persistence.model.user.skill.Skill;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RuleTemplateDefaultData {
    private List<OrganizationTypeAndSubType> organizationTypeAndSubType;
    private List<Skill> skills;
    private List<TimeTypeDTO> timeTypes;
    private List<ActivityDTO> activities;
    private List<OpenShiftIntervalDTO> intervals;
    private List<EmploymentTypeDTO> employmentTypes;
    private List<ExpertiseResponseDTO> expertises;
    private Integer minOpenShiftHours;
    private List<CounterDTO> counters ;

    public RuleTemplateDefaultData(List<Skill> skills, List<TimeTypeDTO> timeTypes, List<ActivityDTO> activities, List<OpenShiftIntervalDTO> intervals,
                                   List<EmploymentTypeDTO> employmentTypes, List<ExpertiseResponseDTO> expertises,Integer minOpenShiftHours,List<CounterDTO> counters) {
        this.skills = skills;
        this.timeTypes = timeTypes;
        this.activities = activities;
        this.intervals = intervals;
        this.employmentTypes = employmentTypes;
        this.expertises = expertises;
        this.minOpenShiftHours=minOpenShiftHours;
        this.counters=counters;
    }

    public RuleTemplateDefaultData(List<OrganizationTypeAndSubType> organizationTypeAndSubType, List<Skill> skills, List<TimeTypeDTO> timeTypes, List<ActivityDTO> activities,
                                   List<OpenShiftIntervalDTO> intervals, List<EmploymentTypeDTO> employmentTypes, List<ExpertiseResponseDTO> expertises,List<CounterDTO> counters) {
        this.organizationTypeAndSubType = organizationTypeAndSubType;
        this.skills = skills;
        this.timeTypes = timeTypes;
        this.activities = activities;
        this.intervals = intervals;
        this.employmentTypes = employmentTypes;
        this.expertises = expertises;
        this.counters=counters;
    }

}
