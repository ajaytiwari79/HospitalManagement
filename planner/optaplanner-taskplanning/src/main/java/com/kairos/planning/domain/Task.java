package com.kairos.planning.domain;

import com.kairos.planning.utils.JodaTimeConverter;
import com.kairos.planning.utils.TaskPlanningUtility;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import org.joda.time.*;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@XStreamAlias("Task")
@PlanningEntity(difficultyComparatorClass = TaskDifficultyComparator.class)
public class Task extends TaskOrEmployee {
	public static final int DRIVING_TIME_MULTIPLIER = 1;
	private static Logger log = LoggerFactory.getLogger(Task.class);
	private TaskType taskType;
	@PlanningVariable(valueRangeProviderRefs = { "taskRange",
			"employeeRange" }, graphType = PlanningVariableGraphType.CHAINED, strengthComparatorClass = TaskOrEmployeeComparator.class)
	private TaskOrEmployee previousTaskOrEmployee;
	private String taskName;
	private Long[] brokenHardConstraints;
	@AnchorShadowVariable(sourceVariableName = "previousTaskOrEmployee")
	private Employee employee;
	private String routeId;
	// @CustomShadowVariable(variableListenerClass = TaskChangeListener.class,
	// sources = {@PlanningVariableReference(variableName =
	// "previousTaskOrVehicle")})
	private Route route;
	// @XStreamAlias("initialStartTime")
	@XStreamConverter(JodaTimeConverter.class)
	private DateTime initialStartTime1;
	@XStreamConverter(JodaTimeConverter.class)
	private DateTime initialEndTime1;

	@XStreamConverter(JodaTimeConverter.class)
	private DateTime initialStartTime2;
	@XStreamConverter(JodaTimeConverter.class)
	private DateTime initialEndTime2;
	private Integer slaDurationStart1;
	private Integer slaDurationEnd1;
	private Integer slaDurationStart2;
	private Integer slaDurationEnd2;
	@CustomShadowVariable(variableListenerClass = StartTimeVariableListener.class, sources = {
			@PlanningVariableReference(variableName = "previousTaskOrEmployee") })
	private DateTime plannedStartTime;
	private boolean locked;
	private int indexInTaskType;
	private Citizen citizen;
	private Integer priority;
	private String id;
	private Long externalId;
	private Long relatedTaskId;
	private Integer duration;
	private Location location;
	private Task dependsUpon;
	/*
	 * @CustomShadowVariable(variableListenerClass = BreakTimeListener.class,
	 * sources = {@PlanningVariableReference(variableName =
	 * "previousTaskOrEmployee")}) private boolean followedByBreak;
	 */
	private List<TaskTimeWindow> timeWindows;

	public List<TaskTimeWindow> getTimeWindows() {
		if (timeWindows == null) {
			List<TaskTimeWindow> windows = getPossibleStartIntervals().stream()
					.map(interval -> new TaskTimeWindow(interval.getStart(), interval.getEnd(),
							TaskPlanningUtility.contains(getPossibleStartIntervals(), interval)))
					.collect(Collectors.toList());
			Collections.sort(windows, (a, b) -> a.isExtended() == b.isExtended() ? 0 : (a.isExtended() ? 1 : -1));
			timeWindows = windows;
		}
		return timeWindows;
	}
	public String getTimeWindowsString() {
		StringBuilder sb = new StringBuilder("");
		timeWindows.stream().forEach(tw->sb.append(tw.toString()));
		sb.append("");
		return sb.toString();
		
	}

	public Long getExternalId() {
		return externalId;
	}

	public void setExternalId(Long externalId) {
		this.externalId = externalId;
	}

	public Task getDependsUpon() {
		return dependsUpon;
	}

	public void setDependsUpon(Task dependsUpon) {
		this.dependsUpon = dependsUpon;
	}

	public DateTime getInitialStartTime2() {
		return initialStartTime2;
	}

	public void setInitialStartTime2(DateTime initialStartTime2) {
		this.initialStartTime2 = initialStartTime2;
	}

	public DateTime getInitialEndTime2() {
		return initialEndTime2;
	}

	public void setInitialEndTime2(DateTime initialEndTime2) {
		this.initialEndTime2 = initialEndTime2;
	}

	public Integer getSlaDurationEnd1() {
		return slaDurationEnd1;
	}

	public void setSlaDurationEnd1(Integer slaDurationEnd1) {
		this.slaDurationEnd1 = slaDurationEnd1;
	}

	public Integer getSlaDurationStart2() {
		return slaDurationStart2;
	}

	public void setSlaDurationStart2(Integer slaDurationStart2) {
		this.slaDurationStart2 = slaDurationStart2;
	}

	public Integer getSlaDurationEnd2() {
		return slaDurationEnd2;
	}

	public void setSlaDurationEnd2(Integer slaDurationEnd2) {
		this.slaDurationEnd2 = slaDurationEnd2;
	}

	public Task() {
		//Not in use
	}

	public boolean isTaskDependent() {
		return dependsUpon != null;
	}

	public Long getRelatedTaskId() {
		return relatedTaskId;
	}

	public void setRelatedTaskId(Long relatedTaskId) {
		this.relatedTaskId = relatedTaskId;
	}

	/*
	 * public Task(Long id, TaskOrEmployee previousTaskOrEmployee, Vehicle
	 * vehicle, int duration, Location location, Integer priority, TaskType
	 * taskType) { this.id=id; this.previousTaskOrEmployee =
	 * previousTaskOrEmployee; // this.vehicle = vehicle; //this.employee =
	 * employee; this.duration = duration; this.location = location;
	 * this.priority=priority; this.taskType=taskType;
	 * 
	 * }
	 */

	public Long[] getBrokenHardConstraints() {
		return brokenHardConstraints;
	}

	public void setBrokenHardConstraints(Long[] brokenHardConstraints) {
		this.brokenHardConstraints = brokenHardConstraints;
	}

	public Long getBrokenHardConstraintsSum() {
		Long sum = 0l;
		if (brokenHardConstraints != null) {
			sum = Arrays.stream(brokenHardConstraints).mapToLong(Long::longValue).sum();
		}
		return sum;
	}

	public boolean hasBrokenOrder() {
		boolean brokenOrder = false;
		if (brokenHardConstraints != null && brokenHardConstraints[1] < 0) {
			brokenOrder = true;
		}
		return brokenOrder;
	}

	public boolean hasBrokenBoundries() {
		boolean brokenOrder = false;
		if (brokenHardConstraints != null && brokenHardConstraints[6] < 0) {
			brokenOrder = true;
		}
		return brokenOrder;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	/*
	 * This is sum of reaching time and waiting time
	 */
	public DateTime getPlannedStartTime() {
		return plannedStartTime;
	}

	public void setPlannedStartTime(DateTime plannedStartTime) {
		this.plannedStartTime = plannedStartTime;
	}

	public Interval getPlannedInterval() {
		return new Interval(plannedStartTime, plannedStartTime.plusMinutes(duration));
	}

	public DateTime getPlannedEndTime() {
		return plannedStartTime.plusMinutes(duration);
	}

	public DateTime getInitialStartTime1() {
		return initialStartTime1;
	}

	public void setInitialStartTime1(DateTime initialStartTime1) {
		this.initialStartTime1 = initialStartTime1;
	}

	public DateTime getInitialEndTime1() {
		return initialEndTime1;
	}

	public void setInitialEndTime1(DateTime initialEndTime1) {
		this.initialEndTime1 = initialEndTime1;
	}

	public DateTime getEarliestStartTime1() {
		return initialStartTime1.minusMinutes((int) slaDurationStart1);
	}

	public DateTime getLatestStartTime1() {
		return initialStartTime1.plusMinutes((int) slaDurationEnd1);
	}

	public DateTime getEarliestStartTime2() {
		if (initialStartTime2 == null)
			return null;
		return initialStartTime2.minusMinutes((int) slaDurationStart2);
	}

	public DateTime getLatestStartTime2() {
		return (initialStartTime2 == null) ? null : initialStartTime2.plusMinutes((int) slaDurationEnd2);
	}

	public List<Interval> getPossibleStartIntervals() {
		List<Interval> intervalList = new ArrayList<>();
		intervalList.add(getPossibleStartInterval1());
		if (getPossibleStartInterval2() != null) {
			intervalList.add(getPossibleStartInterval2());
		}
		return intervalList;
	}

	public Interval getPossibleStartInterval1() {

		return new Interval(getEarliestStartTime1(), getLatestStartTime1());
	}

	public Interval getPossibleStartInterval2() {
		return getEarliestStartTime2() == null ? null : new Interval(getEarliestStartTime2(), getLatestStartTime2());
	}

	public boolean isPlannedInFirstInterval() {
		Interval interval = getPossibleStartInterval1();
		return interval.contains(getPlannedStartTime()) || interval.getStart().equals(getPlannedStartTime())
				|| interval.getEnd().equals(getPlannedStartTime());
	}

	public boolean isPlannedInSecondInterval() {
		Interval interval = getPossibleStartInterval2();
		if (interval == null)
			return false;
		return interval.contains(getPlannedStartTime()) || interval.getStart().equals(getPlannedStartTime())
				|| interval.getEnd().equals(getPlannedStartTime());
	}

	public boolean isMultiManTask() {
		return relatedTaskId != null;
	}

	public boolean hasExtendedTimeWindows() {
		return getPossibleStartInterval2() != null && getPossibleStartInterval2().overlaps(getPossibleStartInterval1());
	}

	public boolean isPlannedInExtendedInterval() {
		return hasExtendedTimeWindows() && isPlannedInSecondInterval() && !isPlannedInFirstInterval();
	}

	public boolean isPlannedInPossibleInterval() {
		boolean isPlannedFine= getTimeWindows().stream().filter(tw->tw.contains(plannedStartTime)).findFirst().isPresent();
		return isPlannedFine;
	}
	public boolean isInPossibleInterval(DateTime plannedTime) {
		boolean isPlannedFine= getTimeWindows().stream().filter(tw->tw.contains(plannedTime)).findFirst().isPresent();
		return isPlannedFine;
	}
	public int getIndexInTaskType() {
		return indexInTaskType;
	}
	public void setIndexInTaskType(int indexInTaskType) {
		this.indexInTaskType = indexInTaskType;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public TaskType getTaskType() {
		return taskType;
	}

	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}

	public Citizen getCitizen() {
		return citizen;
	}

	public void setCitizen(Citizen citizen) {
		this.citizen = citizen;
	}

	public TaskOrEmployee getPreviousTaskOrEmployee() {
		return previousTaskOrEmployee;
	}

	public void setPreviousTaskOrEmployee(TaskOrEmployee previousTaskOrEmployee) {
		this.previousTaskOrEmployee = previousTaskOrEmployee;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return id + "_" + priority + "_I_1:(" + initialStartTime1.toString("HH:mm") + "_"
				+ initialEndTime1.toString("HH:mm") + "),"
				+ (initialStartTime2 != null
						? ("I_2:(" + initialStartTime2.toString("HH:mm") + "_" + initialEndTime2.toString("HH:mm"))
						: "")
				+ ")," + "P:" + plannedStartTime.toString("HH:mm") + "_" + getPlannedEndTime().toString("HH:mm")
				+ "PiD:" + "," + getIntervalIncludingArrivalAndWaiting().getStart().toString("HH:mm") + "_"
				+ getIntervalIncludingArrivalAndWaiting().getEnd().toString("HH:mm") + ",Dis:"
				+ getDrivingMinutesFromPreviousTaskOrEmployee() + ",wait:" + getWaitingMinutes()
				+ ",plannedInSecondInterval" + isPlannedInSecondInterval();
	}

	public Integer getSlaDurationStart1() {
		return slaDurationStart1;
	}

	public void setSlaDurationStart1(Integer slaDurationStart1) {
		this.slaDurationStart1 = slaDurationStart1;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Long getDurationIncludingArrivalTime() {
		return getIntervalIncludingArrivalAndWaiting() == null ? 0l
				: getIntervalIncludingArrivalAndWaiting().toDuration().getStandardMinutes();
	}

	public Interval getInitialInterval1() {
		return new Interval(new DateTime(initialStartTime1), new DateTime(initialEndTime1));
	}

	public Interval getInitialInterval2() {
		return new Interval(new DateTime(initialStartTime2), new DateTime(initialEndTime2));
	}

	public Integer getWaitingMinutes() {
		Integer waitingMins = 0;
		if (!getPlannedReachingTime().isAfter(getPlannedStartTime())) {
			/*if (getPlannedReachingTime() == null || getPlannedStartTime() == null) {
				log.error("nullll");
			}*/
			waitingMins = Minutes.minutesBetween(getPlannedReachingTime(), getPlannedStartTime()).getMinutes();
		}
		return waitingMins;
	}

	public Interval getIntervalIncludingArrivalAndWaiting() {
		return new Interval(
				getPlannedStartTime().minusMinutes(getDrivingMinutesFromPreviousTaskOrEmployee() + getWaitingMinutes()),
				getPlannedEndTime());
	}

	@Override
	public Vehicle getVehicle() {
		return employee == null ? null : employee.getVehicle();
	}

	@Override
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Long getDistanceFromPreviousTaskOrEmployee() {
		if (previousTaskOrEmployee == null) {
			throw new IllegalStateException("This method must not be called when the previousTaskOrVehicle ("
					+ previousTaskOrEmployee + ") is not initialized yet.");
		}
		return getDistanceFrom(previousTaskOrEmployee);
	}

	public boolean isAfter(Task prevTask) {
		return this.getIntervalIncludingArrivalAndWaiting().getStart()
				.isAfter(prevTask.getIntervalIncludingArrivalAndWaiting().getEnd())
				|| this.getIntervalIncludingArrivalAndWaiting().getStart()
						.isEqual(prevTask.getIntervalIncludingArrivalAndWaiting().getEnd());
	}

	public boolean isAfterDependentTask() {
		return dependsUpon == null || isAfter(dependsUpon);
	}

	public boolean isAfterPreviousTask() {
		return previousTaskOrEmployee instanceof Employee ? true : isAfter((Task) previousTaskOrEmployee);
	}

	public boolean isTaskChainOrdered() {
		boolean ordered = true;
		if (previousTaskOrEmployee != null) {
			TaskOrEmployee currentTask = this;
			while (currentTask instanceof Task && ((Task) currentTask).isAfterPreviousTask()) {
				currentTask = ((Task) currentTask).getPreviousTaskOrEmployee();
			}
			if (currentTask instanceof Task) {
				ordered = false;
			}
		}
		return ordered;
	}

	public int getDrivingMinutesFromPreviousTaskOrEmployee() {
		if (previousTaskOrEmployee == null) {
			throw new IllegalStateException("This method must not be called when the previousTaskOrVehicle ("
					+ previousTaskOrEmployee + ") is not initialized yet.");
		}
		if (employee.getVehicle() == null) {
			throw new IllegalStateException(
					"This method must not be called when the vehicle (" + employee + ") is not initialized yet.");
		}
		/*
		 * if(this.getId().equals(1979693l) && previousTaskOrEmployee instanceof
		 * Task && ((Task)previousTaskOrEmployee).getId().equals(2047751l)){
		 * System.out.println("?????????"+getDistanceFrom(previousTaskOrEmployee
		 * )+","+employee.getVehicle()
		 * .getSpeedKmpm()+","+getDistanceFrom(previousTaskOrEmployee) /
		 * employee.getVehicle() .getSpeedKmpm()); }
		 */
		int drivingTime = (int) Math
				.ceil((getDistanceFrom(previousTaskOrEmployee) / employee.getVehicle().getSpeedKmpm())
						* DRIVING_TIME_MULTIPLIER);
		return drivingTime;
	}

	public DateTime getPlannedReachingTime() {
		return previousTaskOrEmployee instanceof Task ? ((Task) previousTaskOrEmployee).getPlannedEndTime()
				.plusMinutes(getDrivingMinutesFromPreviousTaskOrEmployee()) : plannedStartTime;
		// :plannedStartTime.minusMinutes(getDrivingMinutesFromPreviousTaskOrEmployee());
	}

	public Long getDistanceFrom(TaskOrEmployee previousTaskOrEmployee) {
		/*
		 * if(previousTaskOrEmployee.getLocation().getId().equals(0l) &&
		 * this.getLocation().getId().equals(13l) ||
		 * previousTaskOrEmployee.getLocation().getId().equals(13l) &&
		 * this.getLocation().getId().equals(0l)){
		 * System.out.println("????????????"+previousTaskOrEmployee.getLocation(
		 * ).getDistanceFrom(this.getLocation())); }
		 */
		return previousTaskOrEmployee.getLocation().getDistanceFrom(this.getLocation());
	}

	public String toString() {
		return "T:" + id + "-" + priority + "-" + getDuration() + "-" + (employee != null ? employee.getId() : "N");// +"-"+location.name;//+"-"+taskType.getRequiredSkillList();
	}

	public int getMissingSkillCount() {
		if (employee == null) {
			return 0;
		}
		int count = 0;
		for (Skill skill : taskType.getRequiredSkillList()) {
			if (!employee.getSkillSet().contains(skill)) {
				count++;
			}
		}
		return count;
	}

	public int getMissingSkillCountForEmployee(Employee employee) {
		if (employee == null) {
			return 0;
		}
		int count = 0;
		for (Skill skill : taskType.getRequiredSkillList()) {
			if (!employee.getSkillSet().contains(skill)) {
				count++;
			}
		}
		return count;
	}

	public void setNextTask(Task nextTask) {
		this.nextTask = nextTask;
	}

	/*
	 * public boolean canAssignedEmployeeWork(){ Interval
	 * interval=this.getIntervalIncludingArrivalAndWaiting(); return
	 * employee!=null && employee.canWorkThisInterval(interval); }
	 */
	/*
	 * public Long getMinutesExceedingAvailability(){ return employee==null?0l:
	 * employee.getExceedingMinutesForTaskInterval(this.
	 * getIntervalIncludingArrivalAndWaiting()); } public Long
	 * getMinutesExceedingAvailabilityForReachingUnit(){ return
	 * employee==null?0l:
	 * employee.getExceedingMinutesForTaskInterval(getReachBackUnitInterval());
	 * }
	 */
	/*
	 * public boolean canAssignedEmployeeReachBack(){ return
	 * (!isLastTaskOfRoute() ||
	 * employee.canWorkThisInterval(getReachBackUnitInterval())); } public
	 * boolean canEmployeeWork(Employee employee){ return employee!=null &&
	 * employee.canWorkThisInterval(this.getIntervalIncludingArrivalAndWaiting()
	 * ) ; } public boolean canEmployeeReachBack(Employee employee){ return
	 * (!isLastTaskOfRoute() ||
	 * employee.canWorkThisInterval(getReachBackUnitInterval())); }
	 */

	public boolean isLastTaskOfRoute() {
		return nextTask == null;
	}

	public Interval getReachBackUnitInterval() {
		Interval interval = new Interval(getPlannedEndTime(),
				getPlannedEndTime().plusMinutes(getTimeToReachBackUnit().intValue()));
		return interval;
	}

	/*
	 * Return minutes
	 */
	public Long getTimeToReachBackUnit() {
		return Math.round(getDistanceFrom(employee) / employee.getVehicle().getSpeedKmpm());
	}

	public String getRouteId() {
		return routeId;
	}

	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}

	public Integer getOtherTaskTimeDifference(Task otherTask, boolean previous) {
		Integer timeDifference1 = -1, timeDifference2 = -1;
		if (previous) {
			if (this.getPlannedStartTime().isAfter(otherTask.getInitialEndTime1())) {
				timeDifference1 = Minutes.minutesBetween(otherTask.getInitialEndTime1(), this.getPlannedStartTime())
						.getMinutes();
			}
			if (otherTask.getInitialEndTime2() != null
					&& this.getPlannedStartTime().isAfter(otherTask.getInitialEndTime2())) {
				timeDifference2 = Minutes.minutesBetween(otherTask.getInitialEndTime2(), this.getPlannedStartTime())
						.getMinutes();
			}
		} else {
			if (this.getPlannedEndTime().isBefore(otherTask.getInitialStartTime1())) {
				timeDifference1 = Minutes.minutesBetween(this.getPlannedEndTime(), otherTask.getInitialStartTime1())
						.getMinutes();
			}
			if (otherTask.getInitialEndTime2() != null
					&& this.getPlannedStartTime().isBefore(otherTask.getInitialEndTime2())) {
				timeDifference2 = Minutes.minutesBetween(this.getPlannedEndTime(), otherTask.getInitialStartTime2())
						.getMinutes();
			}
		}
		if (timeDifference1 == -1 ^ timeDifference2 == -1) {
			return Math.max(timeDifference1, timeDifference2);
		} else {
			return Math.min(timeDifference1, timeDifference2);
		}

	}
}
