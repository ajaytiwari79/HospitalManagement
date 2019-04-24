package com.kairos.planning.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.util.*;

@XStreamAlias("Employee")
@PlanningEntity(difficultyComparatorClass = EmployeeDifficultyComparator.class)
public class Employee extends TaskOrEmployee{
	public Employee() {
	}
	public Employee(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	private String id;
	private Long externalId;

	public Long getAvialableMinutes() {
		return avialableMinutes;
	}

	public void setAvialableMinutes(Long avialableMinutes) {
		this.avialableMinutes = avialableMinutes;
	}

	private Long avialableMinutes;
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	private Location location;
	private String name;
	@PlanningVariable(valueRangeProviderRefs = {"vehicleRange"},strengthComparatorClass = VehicleComparator.class)
	private Vehicle vehicle;
	private Set<Skill> skillSet;
	public Set<Skill> getSkillSet() {
		return skillSet;
	}
	public void setSkillSet(Set<Skill> skillSet) {
		this.skillSet = skillSet;
	}
	private Map<Citizen, Affinity> affinityMap= new LinkedHashMap<Citizen, Affinity>();
	//private List<AvailabilityRequest> availabilityList;
	
	private List<UnavailabilityRequest> unavailabilityRequests;

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

	public Affinity getAffinityByCitizen(Citizen citizen){
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

	public Long getExternalId() {
		return externalId;
	}

	public void setExternalId(Long externalId) {
		this.externalId = externalId;
	}

	/*public Long getAvailableMinutes(){
		long mins=0l;
		if(availabilityList==null) return mins;
		for(AvailabilityRequest availabilityRequest:availabilityList){
			mins+=availabilityRequest.getMinutes();
		}
		return mins;
	}
	public String getAvailableMinutesAsString(){
		final StringBuilder availability= new StringBuilder("[");
		availabilityList.forEach(availabilityRequest->{
			availability.append(availabilityRequest.getIntervalAsString());
		});
		return availability.append("]").toString();
	}*/
	/*public Long getAvailableMinutesAfterPlanning(){
		long avialableMinutes=0l;
		for(AvailabilityRequest availabilityRequest:availabilityList){
			avialableMinutes+=availabilityRequest.getMinutes();
		}
		int plannedMinutes=0;
        Task temp=nextTask;
		while(temp!=null){
            plannedMinutes+=temp.getIntervalIncludingArrivalAndWaiting()!=null?temp.getIntervalIncludingArrivalAndWaiting().toDuration().getStandardMinutes():0;
        }
		return avialableMinutes-plannedMinutes;
	}

	
	public DateTime getEarliestStartTime(){
		DateTime earliestStart= null;
		for(AvailabilityRequest availabilityRequest:availabilityList){
			if(earliestStart!=null && earliestStart.isBefore(availabilityRequest.getStartTime())){
				continue;
			}
			earliestStart=availabilityRequest.getStartTime();
		}
		return earliestStart;
	}*/
	
	public String toString(){
		return "E:"+id;//+"-"+getAvailabilityList();//+skillSet+"-
	}
	/*public Affinity getAffinity(Citizen citizen) {
        Affinity affinity = affinityMap.get(citizen);
        if (affinity == null) {
            affinity = Affinity.NONE;
        }
        return affinity;
    }*/
    
    /*public boolean canWorkThisInterval(Interval taskTime){
    	//TODO
    	if(true) return true;
		boolean canWork=false;
		for (AvailabilityRequest availabilityRequest : availabilityList) {
			if(availabilityRequest.containsInterval(taskTime)){
				canWork=true;
				break;
			}
		}
		return canWork;
	}
	public boolean canAttemptedToPlanIhisInterval(Interval taskTime){
		//TODO
    	if(true) return true;
		boolean canBeAttempted=false;
		for (AvailabilityRequest availabilityRequest : availabilityList) {
			if(availabilityRequest.getInterval().overlaps(taskTime)){
				canBeAttempted=true;
				break;
			}
		}
		return canBeAttempted;
	}
	public boolean canAttemptedToPlanIhisIntervals(List<Interval> taskTimes){
		//TODO
    	if(true) return true;
		boolean canBeAttempted=false;
		for(Interval taskTime:taskTimes){
			if(canAttemptedToPlanIhisInterval(taskTime)){
				canBeAttempted=true;
				break;
			}
		}

		return canBeAttempted;
	}*/

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public Interval getWorkInterval(){
        Interval workInterval=null;
        if(nextTask!=null){
            Task currentTask=nextTask;
            DateTime start = currentTask.getIntervalIncludingArrivalAndWaiting().getStart();
            while(currentTask.nextTask!=null){
                currentTask=currentTask.nextTask;
            }
            DateTime end = currentTask.getReachBackUnitInterval().getEnd();
            //TODO Interval check
           /* if(start.isBefore(end)){
                workInterval= new Interval(start,end);
            }*/
			workInterval= new Interval(start,end);
        }
        return workInterval;
    }
    public Integer getWorkMinutes(){
		return getWorkInterval()==null?0:getWorkInterval().toDuration().toStandardMinutes().getMinutes();
	}
    public String getWorkIntervalAsString(){
	    return getWorkInterval()==null?"NONE":getWorkInterval().getStart().toString("HH:mm")+"-"+getWorkInterval().getEnd().toString("HH:mm");
    }
    public boolean workIntervalOverlapsWithSameVehicle(Employee otherEmployee){
    	boolean overlaps=false;
        Optional<Interval> optional= Optional.ofNullable(this.getWorkInterval());
    	if((this.getVehicle() != null && this.getVehicle().equals(otherEmployee.getVehicle()) &&
                optional.isPresent()) && optional.get().overlaps(otherEmployee.getWorkInterval())){
			overlaps=true;
		}
		return overlaps;
	}
	/*public long getExceedingMinutesForTaskInterval(Interval taskInterval){
		long exceedingMins=0l;
		boolean matched=false;
		for (AvailabilityRequest availabilityRequest : availabilityList) {
			if(availabilityRequest.getInterval().overlaps(taskInterval)){
				exceedingMins=taskInterval.toDuration().getStandardMinutes() - (availabilityRequest.getInterval().overlap(taskInterval).toDuration().getStandardMinutes());
				matched=true;
				break;
			}
		}
		if(!matched){
			exceedingMins=taskInterval.toDuration().getStandardMinutes();
		}
		return exceedingMins;
	}*/
}
