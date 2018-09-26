package com.kairos.persistence.model.user.unit_position;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.functions.Function;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.time.LocalDate;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_FUNCTION;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_SENIORITY_LEVEL;

/**
 * CreatedBy vipulpandey on 24/9/18
 **/
@NodeEntity
public class PositionLine extends UserBaseEntity {
    @Relationship(type = HAS_SENIORITY_LEVEL)
    private SeniorityLevel seniorityLevel;
    @Relationship(type = HAS_FUNCTION)
    private List<Function> functions;
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalWeeklyMinutes;
    private int fullTimeWeeklyMinutes;
    private float avgDailyWorkingHours;
    private int workingDaysInWeek;
    private float hourlyWages;

    public PositionLine() {
        // DC
    }
    private PositionLine(PositionLineBuilder positionLineBuilder){
        this.seniorityLevel = positionLineBuilder.seniorityLevel;
        this.functions = positionLineBuilder.functions;
        this.startDate = positionLineBuilder.startDate;
        this.endDate = positionLineBuilder.endDate;
        this.totalWeeklyMinutes = positionLineBuilder.totalWeeklyMinutes;
        this.fullTimeWeeklyMinutes = positionLineBuilder.fullTimeWeeklyMinutes;
        this.avgDailyWorkingHours = positionLineBuilder.avgDailyWorkingHours;
        this.workingDaysInWeek = positionLineBuilder.workingDaysInWeek;
        this.hourlyWages = positionLineBuilder.hourlyWages;

    }

    public SeniorityLevel getSeniorityLevel() {
        return seniorityLevel;
    }

    public List<Function> getFunctions() {
        return functions;
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

    public static class PositionLineBuilder {
         private SeniorityLevel seniorityLevel;
         private List<Function> functions;
         private LocalDate startDate;
         private LocalDate endDate;
         private int totalWeeklyMinutes;
         private int fullTimeWeeklyMinutes;
         private float avgDailyWorkingHours;
         private int workingDaysInWeek;
         private float hourlyWages;

         public PositionLineBuilder setSeniorityLevel(SeniorityLevel seniorityLevel) {
             this.seniorityLevel = seniorityLevel;
             return this;
         }

         public PositionLineBuilder setFunctions(List<Function> functions) {
             this.functions = functions;
             return this;
         }

         public PositionLineBuilder setStartDate(LocalDate startDate) {
             this.startDate = startDate;
             return this;
         }

         public PositionLineBuilder setEndDate(LocalDate endDate) {
             this.endDate = endDate;
             return this;
         }

         public PositionLineBuilder setTotalWeeklyMinutes(int totalWeeklyMinutes) {
             this.totalWeeklyMinutes = totalWeeklyMinutes;
             return this;
         }

         public PositionLineBuilder setFullTimeWeeklyMinutes(int fullTimeWeeklyMinutes) {
             this.fullTimeWeeklyMinutes = fullTimeWeeklyMinutes;
             return this;
         }

         public PositionLineBuilder setAvgDailyWorkingHours(float avgDailyWorkingHours) {
             this.avgDailyWorkingHours = avgDailyWorkingHours;
             return this;
         }

         public PositionLineBuilder setWorkingDaysInWeek(int workingDaysInWeek) {
             this.workingDaysInWeek = workingDaysInWeek;
             return this;
         }

         public PositionLineBuilder setHourlyWages(float hourlyWages) {
             this.hourlyWages = hourlyWages;
             return this;
         }

       public PositionLine build() {
           return new PositionLine(this);
       }
     }
}
