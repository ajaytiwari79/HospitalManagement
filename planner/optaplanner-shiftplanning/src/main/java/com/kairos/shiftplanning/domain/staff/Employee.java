package com.kairos.shiftplanning.domain.staff;

import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.shift.EmploymentType;
import com.kairos.enums.Employment;
import com.kairos.enums.EmploymentSubType;
import com.kairos.enums.shift.PaidOutFrequencyEnum;
import com.kairos.shiftplanning.domain.cta.CollectiveTimeAgreement;
import com.kairos.shiftplanning.domain.shift.Shift;
import com.kairos.shiftplanning.domain.skill.Skill;
import com.kairos.shiftplanning.domain.wta.WorkingTimeAgreement;
import com.kairos.shiftplanning.domain.wta.WorkingTimeConstraints;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.joda.time.DateTime;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Getter
@Setter
@XStreamAlias("Employee")
public class Employee {
    private static final Logger LOGGER = LoggerFactory.getLogger(Employee.class);
    private String id;
    private BigDecimal baseCost;
    transient private WorkingTimeConstraints workingTimeConstraints;
    private PrevShiftsInfo prevShiftsInfo;
    private DateTime prevShiftStart;
    private DateTime prevShiftEnd;
    transient private CollectiveTimeAgreement collectiveTimeAgreement;
    private Map<java.time.LocalDate,CTAResponseDTO> localDateCTAResponseDTOMap;// added 10-9-2018
    private Map<java.time.LocalDate,WorkingTimeAgreement> localDateWTAMap;
    private Location location;
    private String name;
    private Set<Skill> skillSet;
    private Long expertiseId;
    private int totalWeeklyMinutes;
    private int workingDaysInWeek;
    private PaidOutFrequencyEnum paidOutFrequencyEnum;
    private Long employmentTypeId;
    private Long employmentId;


    public Employee(String id, String name, Set<Skill> skillSet, Long expertiseId, int totalWeeklyMinutes, int workingDaysInWeek, PaidOutFrequencyEnum paidOutFrequencyEnum, Long employmentTypeId) {
        super();
        this.id = id;
        this.name = name;
        this.skillSet = skillSet;
        this.expertiseId = expertiseId;
        this.totalWeeklyMinutes=totalWeeklyMinutes;
        this.workingDaysInWeek=workingDaysInWeek;
        this.paidOutFrequencyEnum=paidOutFrequencyEnum;
        this.employmentTypeId = employmentTypeId;
    }


    public String toString() {
        return "E:" + id;
    }
    public Employee() {
    }

    public Long getEmploymentId() {
        return employmentId;
    }

    public void setEmploymentId(Long employmentId) {
        this.employmentId = employmentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Employee employee = (Employee) o;

        return new EqualsBuilder()
                .append(id, employee.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        int hashcode=id.hashCode();
        return hashcode;
    }
    public int checkConstraints(List<Shift> shifts, int index){
        return getWorkingTimeConstraints().checkConstraint(shifts, index);

    }

    public int checkConstraints(Shift shift, int index){
        return getWorkingTimeConstraints().checkConstraint(shift, index);
    }
    public void breakLevelConstraints(HardMediumSoftLongScoreHolder scoreHolder, RuleContext kContext, int index, int contraintPenality){
        getWorkingTimeConstraints().breakLevelConstraints(scoreHolder,kContext,index,contraintPenality);
    }

    public Long getEmploymentTypeId() {
        return employmentTypeId;
    }

    public void setEmploymentTypeId(Long employmentTypeId) {
        this.employmentTypeId = employmentTypeId;
    }
}
