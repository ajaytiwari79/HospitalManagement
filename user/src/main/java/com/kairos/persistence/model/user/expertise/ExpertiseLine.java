package com.kairos.persistence.model.user.expertise;

import com.kairos.enums.shift.BreakPaymentSetting;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.services.OrganizationService;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.time.LocalDate;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.FOR_SENIORITY_LEVEL;
import static com.kairos.persistence.model.constants.RelationshipConstants.SUPPORTS_SERVICES;

@NodeEntity
@Getter
@Setter
public class ExpertiseLine extends UserBaseEntity {
    private LocalDate startDate;
    private LocalDate endDate;

    @Relationship(type = FOR_SENIORITY_LEVEL)
    private List<SeniorityLevel> seniorityLevel;
    @Relationship(type = SUPPORTS_SERVICES)
    private List<OrganizationService> organizationServices;
    private BreakPaymentSetting breakPaymentSetting;
    private int fullTimeWeeklyMinutes; // This is equals to 37 hours
    private int numberOfWorkingDaysInWeek; // 5 or 7

    private ExpertiseLine(){

    }


    private ExpertiseLine(ExpertiseLineBuilder expertiseLineBuilder) {
        this.startDate = expertiseLineBuilder.startDate;
        this.endDate = expertiseLineBuilder.endDate;
        this.breakPaymentSetting=expertiseLineBuilder.breakPaymentSetting;
        this.fullTimeWeeklyMinutes=expertiseLineBuilder.fullTimeWeeklyMinutes;
        this.numberOfWorkingDaysInWeek=expertiseLineBuilder.numberOfWorkingDaysInWeek;
    }


    public static class ExpertiseLineBuilder {
        private LocalDate startDate;
        private LocalDate endDate;
        private BreakPaymentSetting breakPaymentSetting;
        private int fullTimeWeeklyMinutes; // This is equals to 37 hours
        private Integer numberOfWorkingDaysInWeek; // 5 or 7
        private List<OrganizationService> organizationServices;
        private List<SeniorityLevel> seniorityLevel;


        public ExpertiseLineBuilder setStartDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public ExpertiseLineBuilder setEndDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public ExpertiseLineBuilder setBreakPaymentSetting(BreakPaymentSetting breakPaymentSetting) {
            this.breakPaymentSetting = breakPaymentSetting;
            return this;
        }

        public ExpertiseLineBuilder setSeniorityLevel(List<SeniorityLevel> seniorityLevel) {
            this.seniorityLevel = seniorityLevel;
            return this;
        }
        public ExpertiseLineBuilder setNumberOfWorkingDaysInWeek(Integer numberOfWorkingDaysInWeek) {
            this.numberOfWorkingDaysInWeek = numberOfWorkingDaysInWeek;
            return this;
        }

        public ExpertiseLineBuilder setFullTimeWeeklyMinutes(int fullTimeWeeklyMinutes) {
            this.fullTimeWeeklyMinutes = fullTimeWeeklyMinutes;
            return this;
        }
        public ExpertiseLineBuilder setOrganizationServices(List<OrganizationService> organizationServices) {
            this.organizationServices = organizationServices;
            return this;
        }



        public ExpertiseLine createLine() {
            return new ExpertiseLine(this);
        }
    }

    public boolean seniorityLevelChanged(ExpertiseLine expertiseLine){
        if(this.getSeniorityLevel().size()!=expertiseLine.getSeniorityLevel().size()){
            return true;
        }
        for (int i = 0; i < expertiseLine.getSeniorityLevel().size(); i++) {
            SeniorityLevel thisSeniorityLevel = this.getSeniorityLevel().get(i);
            SeniorityLevel seniorityLevel = expertiseLine.getSeniorityLevel().get(i);
            if (thisSeniorityLevel.isSeniorityLevelChanged(seniorityLevel)) {
                return true;
            }
        }
        return false;
    }


}
