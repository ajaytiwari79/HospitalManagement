package com.kairos.shiftplanning.domain.staff;

import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.night_worker.ExpertiseNightWorkerSettingDTO;
import com.kairos.enums.constraint.ConstraintSubType;
import com.kairos.enums.constraint.ConstraintType;
import com.kairos.enums.shift.PaidOutFrequencyEnum;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.skill.Skill;
import com.kairos.shiftplanning.domain.tag.Tag;
import com.kairos.shiftplanning.domain.unit.Unit;
import com.kairos.shiftplanning.domain.wta_ruletemplates.WTABaseRuleTemplate;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@XStreamAlias("Employee")
public class Employee {
    private static final Logger LOGGER = LoggerFactory.getLogger(Employee.class);
    private Long id;
    private BigDecimal baseCost;
    private Map<java.time.LocalDate,CTAResponseDTO> localDateCTAResponseDTOMap;// added 10-9-2018
   // private Map<java.time.LocalDate,Map<ConstraintType, WTABaseRuleTemplate>> localDateWTAMap;
    private Location location;
    private String name;
    private Set<Skill> skillSet;
    private Long expertiseId;
    private int totalWeeklyMinutes;
    private int workingDaysInWeek;
    private PaidOutFrequencyEnum paidOutFrequencyEnum;
    private Long employmentTypeId;
    private Long employmentId;
    private SeniorAndChildCareDays seniorAndChildCareDays;
    private List<StaffChildDetail> staffChildDetails;
    private Set<Tag> tags;
    private Map<BigInteger,ShiftImp> actualShiftsMap;
    private Set<Team> teams;
    private boolean nightWorker;
    private ExpertiseNightWorkerSetting expertiseNightWorkerSetting;
    private Map<LocalDate,Map<ConstraintSubType, WTABaseRuleTemplate>> wtaRuleTemplateMap;

    public Employee(Long id, String name, Set<Skill> skillSet, Long expertiseId, int totalWeeklyMinutes, int workingDaysInWeek, PaidOutFrequencyEnum paidOutFrequencyEnum, Long employmentTypeId) {
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

    public Long getEmploymentId() {
        return employmentId;
    }

    public void setEmploymentId(Long employmentId) {
        this.employmentId = employmentId;
    }

    public int checkConstraints(Unit unit, ShiftImp shiftImp, List<ShiftImp> shiftImps,ConstraintSubType constraintSubType) {
        return this.wtaRuleTemplateMap.get(shiftImp.getStartDate()).get(constraintSubType).checkConstraints(unit,shiftImp,shiftImps);
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

    public Long getEmploymentTypeId() {
        return employmentTypeId;
    }

    public void setEmploymentTypeId(Long employmentTypeId) {
        this.employmentTypeId = employmentTypeId;
    }
}
