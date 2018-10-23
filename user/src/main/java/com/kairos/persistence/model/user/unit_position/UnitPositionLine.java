package com.kairos.persistence.model.user.unit_position;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.functions.Function;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

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
public class UnitPositionLine extends UserBaseEntity {
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
    private float hourlyWages;          // payGroupArea

    public UnitPositionLine() {
        // DC
    }
    private UnitPositionLine(UnitPositionLineBuilder unitPositionLineBuilder){
        this.seniorityLevel = unitPositionLineBuilder.seniorityLevel;
        this.functions = unitPositionLineBuilder.functions;
        this.startDate = unitPositionLineBuilder.startDate;
        this.endDate = unitPositionLineBuilder.endDate;
        this.totalWeeklyMinutes = unitPositionLineBuilder.totalWeeklyMinutes;
        this.fullTimeWeeklyMinutes = unitPositionLineBuilder.fullTimeWeeklyMinutes;
        this.avgDailyWorkingHours = unitPositionLineBuilder.avgDailyWorkingHours;
        this.workingDaysInWeek = unitPositionLineBuilder.workingDaysInWeek;
        this.hourlyWages = unitPositionLineBuilder.hourlyWages;

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

    public void setHourlyWages(float hourlyWages) {
        this.hourlyWages = hourlyWages;
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

    public float getHourlyWages() {
        return hourlyWages;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public static class UnitPositionLineBuilder {
         private SeniorityLevel seniorityLevel;
         private List<Function> functions;
         private LocalDate startDate;
         private LocalDate endDate;
         private int totalWeeklyMinutes;
         private int fullTimeWeeklyMinutes;
         private float avgDailyWorkingHours;
         private int workingDaysInWeek;
         private float hourlyWages;

         public UnitPositionLineBuilder setSeniorityLevel(SeniorityLevel seniorityLevel) {
             this.seniorityLevel = seniorityLevel;
             return this;
         }

         public UnitPositionLineBuilder setFunctions(List<Function> functions) {
             this.functions = functions;
             return this;
         }

         public UnitPositionLineBuilder setStartDate(LocalDate startDate) {
             this.startDate = startDate;
             return this;
         }

         public UnitPositionLineBuilder setEndDate(LocalDate endDate) {
             this.endDate = endDate;
             return this;
         }

         public UnitPositionLineBuilder setTotalWeeklyMinutes(int totalWeeklyMinutes) {
             this.totalWeeklyMinutes = totalWeeklyMinutes;
             return this;
         }

         public UnitPositionLineBuilder setFullTimeWeeklyMinutes(int fullTimeWeeklyMinutes) {
             this.fullTimeWeeklyMinutes = fullTimeWeeklyMinutes;
             return this;
         }

         public UnitPositionLineBuilder setAvgDailyWorkingHours(float avgDailyWorkingHours) {
             this.avgDailyWorkingHours = avgDailyWorkingHours;
             return this;
         }

         public UnitPositionLineBuilder setWorkingDaysInWeek(int workingDaysInWeek) {
             this.workingDaysInWeek = workingDaysInWeek;
             return this;
         }

         public UnitPositionLineBuilder setHourlyWages(float hourlyWages) {
             this.hourlyWages = hourlyWages;
             return this;
         }

       public UnitPositionLine build() {
           return new UnitPositionLine(this);
       }
     }
}
