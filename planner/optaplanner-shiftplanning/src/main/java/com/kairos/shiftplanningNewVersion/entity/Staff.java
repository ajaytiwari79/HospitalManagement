package com.kairos.shiftplanningNewVersion.entity;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.user.employment.EmploymentLinesDTO;
import com.kairos.enums.constraint.ConstraintSubType;
import com.kairos.enums.shift.PaidOutFrequencyEnum;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.skill.Skill;
import com.kairos.shiftplanning.domain.staff.*;
import com.kairos.shiftplanning.domain.tag.Tag;
import com.kairos.shiftplanning.domain.unit.Unit;
import com.kairos.shiftplanning.domain.wta_ruletemplates.WTABaseRuleTemplate;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.commons.utils.DateUtils.*;

@Getter
@Setter
@Builder
public class Staff {
    private Long id;
    private BigDecimal baseCost;
    private Map<LocalDate, List<CTARuleTemplate>> localDateCTARuletemplateMap;
    private String name;
    private Set<Skill> skillSet;
    private PaidOutFrequencyEnum paidOutFrequencyEnum;
    private SeniorAndChildCareDays seniorAndChildCareDays;
    private List<StaffChildDetail> staffChildDetails;
    private Set<Tag> tags;
    private Set<Team> teams;
    private boolean nightWorker;
    private Employment employment;
    private ExpertiseNightWorkerSetting expertiseNightWorkerSetting;
    private Unit unit;
    private Map<LocalDate,Map<ConstraintSubType, WTABaseRuleTemplate>> wtaRuleTemplateMap;
    private BreakSettings breakSettings;
    @Builder.Default
    private Map<LocalDate,BigDecimal> functionalBonus = new HashMap<>();


    public int verifyConstraints(Unit unit,Shift shift,List<Shift> shifts,ConstraintSubType constraintSubType){
        if(this.getWtaRuleTemplateMap().get(shift.getStart().toLocalDate()).containsKey(constraintSubType)){
            return this.getWtaRuleTemplateMap().get(shift.getStart().toLocalDate()).get(constraintSubType).verifyConstraints(unit,shift,shifts);
        }else if(ConstraintSubType.MINIMIZE_COST.equals(constraintSubType)){
             BigDecimal hourlyCost = getHourlyCostByDate(employment.getEmploymentLines(),shift.getStartDate());
             BigDecimal oneMinuteCost = hourlyCost.divide(BigDecimal.valueOf(60),BigDecimal.ROUND_CEILING,6);
             return hourlyCost.multiply(BigDecimal.valueOf(getHourByMinutes(shift.getMinutes()))).add(oneMinuteCost.multiply(BigDecimal.valueOf(getHourMinutesByMinutes(shift.getMinutes())))).intValue();
        }
        return 0;
    }

    public BigDecimal getHourlyCostByDate(List<EmploymentLine> employmentLines, java.time.LocalDate localDate) {
        BigDecimal hourlyCost = BigDecimal.valueOf(0);
        for (EmploymentLine employmentLine : employmentLines) {
            DateTimeInterval positionInterval = employmentLine.getInterval();
            if ((positionInterval == null && (employmentLine.getStartDate().equals(localDate) || employmentLine.getStartDate().isBefore(localDate))) || (positionInterval != null && (positionInterval.contains(asDate(localDate)) || employmentLine.getEndDate().equals(localDate)))) {
                hourlyCost = employmentLine.getHourlyCost();
                break;
            }
        }
        return hourlyCost;
    }

    public void breakContraints(Shift shift, HardMediumSoftLongScoreHolder scoreHolder, RuleContext kContext, int constraintPenality, ConstraintSubType constraintSubType) {
        if(ConstraintSubType.MINIMIZE_COST.equals(constraintSubType)){
            scoreHolder.addSoftConstraintMatch(kContext,constraintPenality);
        }else {
            this.wtaRuleTemplateMap.get(shift.getStartDate()).get(constraintSubType).breakLevelConstraints(scoreHolder, kContext, constraintPenality);
        }
    }

}
