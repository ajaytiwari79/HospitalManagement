package com.kairos.persistence.model.user.employment;

import com.kairos.enums.EmploymentSubType;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.functions.Function;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_FUNCTION;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_SENIORITY_LEVEL;

/**
 * CreatedBy vipulpandey on 24/9/18
 **/
@NodeEntity
public class EmploymentLine extends UserBaseEntity {
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

    public EmploymentLine() {
        // DC
    }
    private EmploymentLine(EmploymentLineBuilder employmentLineBuilder){
        this.seniorityLevel = employmentLineBuilder.seniorityLevel;
        this.functions = employmentLineBuilder.functions;
        this.startDate = employmentLineBuilder.startDate;
        this.endDate = employmentLineBuilder.endDate;
        this.totalWeeklyMinutes = employmentLineBuilder.totalWeeklyMinutes;
        this.fullTimeWeeklyMinutes = employmentLineBuilder.fullTimeWeeklyMinutes;
        this.avgDailyWorkingHours = employmentLineBuilder.avgDailyWorkingHours;
        this.workingDaysInWeek = employmentLineBuilder.workingDaysInWeek;
        this.hourlyCost = employmentLineBuilder.hourlyCost;
    }

    public void setSeniorityLevel(SeniorityLevel seniorityLevel) {
        this.seniorityLevel = seniorityLevel;
    }

    public void setFunctions(List<Function> functions) {
        this.functions = functions;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setTotalWeeklyMinutes(int totalWeeklyMinutes) {
        this.totalWeeklyMinutes = totalWeeklyMinutes;
    }

    public void setFullTimeWeeklyMinutes(int fullTimeWeeklyMinutes) {
        this.fullTimeWeeklyMinutes = fullTimeWeeklyMinutes;
    }

    public void setAvgDailyWorkingHours(float avgDailyWorkingHours) {
        this.avgDailyWorkingHours = avgDailyWorkingHours;
    }

    public void setWorkingDaysInWeek(int workingDaysInWeek) {
        this.workingDaysInWeek = workingDaysInWeek;
    }

    public BigDecimal getHourlyCost() {
        return hourlyCost;
    }

    public void setHourlyCost(BigDecimal hourlyCost) {
        this.hourlyCost = hourlyCost;
    }

    public SeniorityLevel getSeniorityLevel() {
        return seniorityLevel;
    }

    public List<Function> getFunctions() {
        return Optional.ofNullable(functions).orElse(new ArrayList<>());
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getTotalWeeklyMinutes() {
        return totalWeeklyMinutes;
    }

    public int getFullTimeWeeklyMinutes() {
        return fullTimeWeeklyMinutes;
    }

    public float getAvgDailyWorkingHours() {
        return avgDailyWorkingHours;
    }

    public int getWorkingDaysInWeek() {
        return workingDaysInWeek;
    }



    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public static class EmploymentLineBuilder {
         private SeniorityLevel seniorityLevel;
         private List<Function> functions;
         private LocalDate startDate;
         private LocalDate endDate;
         private int totalWeeklyMinutes;
         private int fullTimeWeeklyMinutes;
         private float avgDailyWorkingHours;
         private int workingDaysInWeek;
         private BigDecimal hourlyCost;

         public EmploymentLineBuilder setSeniorityLevel(SeniorityLevel seniorityLevel) {
             this.seniorityLevel = seniorityLevel;
             return this;
         }

         public EmploymentLineBuilder setFunctions(List<Function> functions) {
             this.functions = functions;
             return this;
         }

         public EmploymentLineBuilder setStartDate(LocalDate startDate) {
             this.startDate = startDate;
             return this;
         }

         public EmploymentLineBuilder setEndDate(LocalDate endDate) {
             this.endDate = endDate;
             return this;
         }

         public EmploymentLineBuilder setTotalWeeklyMinutes(int totalWeeklyMinutes) {
             this.totalWeeklyMinutes = totalWeeklyMinutes;
             return this;
         }

         public EmploymentLineBuilder setFullTimeWeeklyMinutes(int fullTimeWeeklyMinutes) {
             this.fullTimeWeeklyMinutes = fullTimeWeeklyMinutes;
             return this;
         }

         public EmploymentLineBuilder setAvgDailyWorkingHours(float avgDailyWorkingHours) {
             this.avgDailyWorkingHours = avgDailyWorkingHours;
             return this;
         }

         public EmploymentLineBuilder setWorkingDaysInWeek(int workingDaysInWeek) {
             this.workingDaysInWeek = workingDaysInWeek;
             return this;
         }

         public EmploymentLineBuilder setHourlyCost(BigDecimal hourlyCost) {
             this.hourlyCost = hourlyCost;
             return this;
         }

       public EmploymentLine build() {
           return new EmploymentLine(this);
       }
     }
}
