package com.kairos.planning.solution;

import com.kairos.planning.domain.*;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.persistence.xstream.api.score.buildin.bendablelong.BendableLongScoreXStreamConverter;

import java.util.List;

//import org.springframework.data.annotation.Id;
//import com.thoughtworks.xstream.annotations.XStreamAlias;

@PlanningSolution
@XStreamAlias("TaskPlanningSolution")
//@javax.xml.bind.annotation.XmlRootElement
//@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
public class TaskPlanningSolution {

	//@Id
	private String id;

	@ProblemFactCollectionProperty
	@ValueRangeProvider(id = "vehicleRange")
	private List<Vehicle> vehicleList;
	@ProblemFactCollectionProperty
	private List<Citizen> citizenList;
	@PlanningEntityCollectionProperty
	@ValueRangeProvider(id = "taskRange")
	private List<Task> taskList;
	@ProblemFactCollectionProperty
	private List<TaskType> taskTypeList;
	@PlanningEntityCollectionProperty
	@ValueRangeProvider(id = "employeeRange")
	private List<Employee> employeeList;
	@ProblemFactCollectionProperty
	private List<Skill> skillList;
	@ProblemFactCollectionProperty
	private List<AvailabilityRequest> availabilityList;
	private List<UnavailabilityRequest> unavailabilityRequests;
	private int hardConstraints;
	private int softConstraints;
	
	
	public List<UnavailabilityRequest> getUnavailabilityRequests() {
		return unavailabilityRequests;
	}

	public void setUnavailabilityRequests(List<UnavailabilityRequest> unavailabilityRequests) {
		this.unavailabilityRequests = unavailabilityRequests;
	}

	public List<AvailabilityRequest> getAvailabilityList() {
		return availabilityList;
	}

	public void setAvailabilityList(List<AvailabilityRequest> availabilityList) {
		this.availabilityList = availabilityList;
	}

	@ProblemFactCollectionProperty
	private List<Location> locationList;
	public List<Vehicle> getVehicleList() {
		return vehicleList;
	}

	public void setVehicleList(List<Vehicle> vehicleList) {
		this.vehicleList = vehicleList;
	}

	public List<Citizen> getCitizenList() {
		return citizenList;
	}

	public void setCitizenList(List<Citizen> citizenList) {
		this.citizenList = citizenList;
	}

	public List<Task> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<Task> taskList) {
		this.taskList = taskList;
	}

	public List<TaskType> getTaskTypeList() {
		return taskTypeList;
	}

	public void setTaskTypeList(List<TaskType> taskTypeList) {
		this.taskTypeList = taskTypeList;
	}

	public List<Employee> getEmployeeList() {
		return employeeList;
	}

	public void setEmployeeList(List<Employee> employeeList) {
		this.employeeList = employeeList;
	}

	public List<Skill> getSkillList() {
		return skillList;
	}

	public void setSkillList(List<Skill> skillList) {
		this.skillList = skillList;
	}

	public BendableLongScore getScore() {
		return score;
	}

	public void setScore(BendableLongScore score) {
		this.score = score;
	}
	public List<Location> getLocationList() {
		return locationList;
	}

	public void setLocationList(List<Location> locationList) {
		this.locationList = locationList;
	}

	public int getHardConstraints() {
		return hardConstraints;
	}

	public void setHardConstraints(int hardConstraints) {
		this.hardConstraints = hardConstraints;
	}

	public int getSoftConstraints() {
		return softConstraints;
	}

	public void setSoftConstraints(int softConstraints) {
		this.softConstraints = softConstraints;
	}

	@XStreamConverter(BendableLongScoreXStreamConverter.class)
	@PlanningScore()
	//@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.optaplanner.persistence.jaxb.api.score.buildin.bendablelong.BendableLongScoreJaxbXmlAdapter.class)
    private BendableLongScore score;

	public void setBendableScore(long[] hard,long[] soft){
		score = BendableLongScore.valueOf(hard,soft);
	}

}
