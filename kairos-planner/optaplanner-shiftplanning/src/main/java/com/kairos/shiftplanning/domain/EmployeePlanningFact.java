package com.kairos.shiftplanning.domain;

import com.kairos.response.dto.web.experties.PaidOutFrequencyEnum;
import com.kairos.shiftplanning.domain.cta.CollectiveTimeAgreement;
import com.kairos.shiftplanning.domain.wta.*;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.joda.time.DateTime;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@XStreamAlias("EmployeePlanningFact")
public class EmployeePlanningFact {
    private static Logger log= LoggerFactory.getLogger(EmployeePlanningFact.class);
    private String id;
    private BigDecimal baseCost;
    transient private WorkingTimeConstraints workingTimeConstraints;
    private PrevShiftsInfo prevShiftsInfo;
    private DateTime prevShiftStart;
    private DateTime prevShiftEnd;
    transient private CollectiveTimeAgreement collectiveTimeAgreement;
    private Location location;
    private String name;
    private Set<Skill> skillSet;
    private List<UnavailabilityRequest> unavailabilityRequests;
    private Long expertiseId;
    private int totalWeeklyMinutes;
    private int workingDaysInWeek;
    private PaidOutFrequencyEnum paidOutFrequencyEnum;
    private Long employmentTypeId;


    public EmployeePlanningFact(String id, String name, Set<Skill> skillSet, Long expertiseId, int totalWeeklyMinutes, int workingDaysInWeek, PaidOutFrequencyEnum paidOutFrequencyEnum, Long employmentTypeId) {
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
    public int getTotalWeeklyMinutes() {
        return totalWeeklyMinutes;
    }

    public void setTotalWeeklyMinutes(int totalWeeklyMinutes) {
        this.totalWeeklyMinutes = totalWeeklyMinutes;
    }

    public int getWorkingDaysInWeek() {
        return workingDaysInWeek;
    }

    public void setWorkingDaysInWeek(int workingDaysInWeek) {
        this.workingDaysInWeek = workingDaysInWeek;
    }

    public PaidOutFrequencyEnum getPaidOutFrequencyEnum() {
        return paidOutFrequencyEnum;
    }

    public void setPaidOutFrequencyEnum(PaidOutFrequencyEnum paidOutFrequencyEnum) {
        this.paidOutFrequencyEnum = paidOutFrequencyEnum;
    }

    public CollectiveTimeAgreement getCollectiveTimeAgreement() {
        return collectiveTimeAgreement;
    }

    public void setCollectiveTimeAgreement(CollectiveTimeAgreement collectiveTimeAgreement) {
        this.collectiveTimeAgreement = collectiveTimeAgreement;
    }

    public DateTime getPrevShiftEnd() {
        return prevShiftEnd;
    }

    public void setPrevShiftEnd(DateTime prevShiftEnd) {
        this.prevShiftEnd = prevShiftEnd;
    }

    public DateTime getPrevShiftStart() {
        return prevShiftStart;
    }

    public void setPrevShiftStart(DateTime prevShiftStart) {
        this.prevShiftStart = prevShiftStart;
    }

    public PrevShiftsInfo getPrevShiftsInfo() {
        return prevShiftsInfo;
    }

    public void setPrevShiftsInfo(PrevShiftsInfo prevShiftsInfo) {
        this.prevShiftsInfo = prevShiftsInfo;
    }

    public WorkingTimeConstraints getWorkingTimeConstraints() {
        return workingTimeConstraints;
    }

    public void setWorkingTimeConstraints(WorkingTimeConstraints workingTimeConstraints) {
        this.workingTimeConstraints = workingTimeConstraints;
    }

    public BigDecimal getBaseCost() {
        return baseCost;
    }

    public void setBaseCost(BigDecimal baseCost) {
        this.baseCost = baseCost;
    }
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }



    public Set<Skill> getSkillSet() {
        return skillSet;
    }

    public void setSkillSet(Set<Skill> skillSet) {
        this.skillSet = skillSet;
    }

    private Map<Citizen, Affinity> affinityMap = new LinkedHashMap<Citizen, Affinity>();
    //private List<AvailabilityRequest> availabilityList;



    public List<UnavailabilityRequest> getUnavailabilityRequests() {
        return unavailabilityRequests;
    }

    public void setUnavailabilityRequests(List<UnavailabilityRequest> unavailabilityRequests) {
        this.unavailabilityRequests = unavailabilityRequests;
    }

    /*public List<AvailabilityRequest> getAvailabilityList() {
        return availabilityList;
    }
    public void setAvailabilityList(List<AvailabilityRequest> availabilityList) {
        this.availabilityList = availabilityList;
    }*/
    public Map<Citizen, Affinity> getAffinityMap() {
        return affinityMap;
    }

    public Affinity getAffinityByCitizen(Citizen citizen) {
        return affinityMap.get(citizen);
    }

    public void setAffinityMap(Map<Citizen, Affinity> affinityMap) {
        this.affinityMap = affinityMap;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public Long getExpertiseId() {
        return expertiseId;
    }

    public void setExpertiseId(Long expertiseId) {
        this.expertiseId = expertiseId;
    }

    public String toString() {
        return "E:" + id;//+"-"+getAvailabilityList();//+skillSet+"-
    }
    public EmployeePlanningFact() {
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        EmployeePlanningFact employee = (EmployeePlanningFact) o;

        return new EqualsBuilder()
                .append(id, employee.id)
                /*.append(avialableMinutes, employee.avialableMinutes)
                .append(location, employee.location)
                .append(name, employee.name)
                .append(skillSet, employee.skillSet)
                .append(affinityMap, employee.affinityMap)
                .append(unavailabilityRequests, employee.unavailabilityRequests)*/
                .isEquals();
    }

    @Override
    public int hashCode() {
        /*return new HashCodeBuilder(17, 37)
                .append(id)
                .toHashCode();*/
        int hashcode=id.hashCode();
        //log.info("EmployeePlanningFact hashcode:"+id+":"+hashcode);
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
