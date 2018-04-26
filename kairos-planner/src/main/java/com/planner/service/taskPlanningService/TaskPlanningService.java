package com.planner.service.taskPlanningService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.optaplanner.persistence.xstream.api.score.buildin.bendablelong.BendableLongScoreXStreamConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kairos.planning.domain.Affinity;
import com.kairos.planning.domain.AvailabilityRequest;
import com.kairos.planning.domain.Citizen;
import com.kairos.planning.domain.Employee;
import com.kairos.planning.domain.Location;
import com.kairos.planning.domain.Skill;
import com.kairos.planning.domain.Task;
import com.kairos.planning.domain.TaskType;
import com.kairos.planning.domain.UnavailabilityRequest;
import com.kairos.planning.domain.Vehicle;
import com.kairos.planning.solution.TaskPlanningSolution;
import com.kairos.planning.utils.JodaTimeConverter;
import com.planner.appConfig.appConfig.AppConfig;
import com.planner.commonUtil.StaticField;
import com.planner.domain.taskPlanning.PlanningProblem;
import com.planner.repository.customRepository.CustomRepository;
import com.planner.service.citizenService.CitizenService;
import com.planner.service.locationService.GraphHopperService;
import com.planner.service.locationService.LocationService;
import com.planner.service.skillService.SkillService;
import com.planner.service.staffService.ShiftService;
import com.planner.service.staffService.StaffService;
import com.planner.service.taskService.TaskService;
import com.planner.service.taskService.TaskTypeService;
import com.planner.service.vehicleService.VehicleService;
import com.thoughtworks.xstream.XStream;

@Service
public class TaskPlanningService {

	private static final Logger log = LoggerFactory.getLogger(TaskPlanningService.class);

	@Autowired private TaskService taskService;
	@Autowired private TaskTypeService taskTypeService;
	@Autowired private SkillService skillService;
	@Autowired private StaffService staffService;
	@Autowired private VehicleService vehicleService;
	@Autowired private CitizenService citizenService;
	@Autowired private LocationService locationService;
	@Autowired private ShiftService shiftService;
	@Autowired private GraphHopperService graphHopperService;
	@Autowired private KieService optaPlannerService;
	@Autowired private AppConfig appConfig;
	@Autowired private CustomRepository customRepository;

	/*static{
		java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
	    System.setProperty("user.timezone", "UTC");
	}*/

	public List<Map> loadUnsolvedSolutionFromXML() {
		XStream xstream = new XStream();
		xstream.processAnnotations(Employee.class);
		xstream.ignoreUnknownElements();
		xstream.setMode(XStream.ID_REFERENCES);
		List<Employee> employees = (List<Employee>) xstream
				.fromXML(new File("/media/pradeep/bak/multiOpta/task-planner/src/main/resources/data/emp.xml"));
		List<Map> updatedEmployees = new ArrayList<Map>();
		List<Map> tasks = new ArrayList<>();
		int i = 1;
		for (Employee emp : employees) {
			Map<String, Object> map = new HashMap<>();
			makeEmployeeList(tasks, emp.getNextTask());
			map.put("employeeName", emp.getName());
			map.put("employeeNumber", i);
			map.put("employeeId", emp.getId());
			//map.put("availableTime", emp.getAvailabilityList().get(0));
			map.put("nextTasks", tasks);
			tasks = new ArrayList<>();
			updatedEmployees.add(map);
			i++;
		}

		return updatedEmployees;
	}
	public Map renderSolutionFromXML(String xmlString){
		TaskPlanningSolution solution=makeTaskPlanningObjectFromXml(xmlString);
		Map model=getModelBySolution(solution);
		return model;
	}

	public TaskPlanningSolution makeTaskPlanningObjectFromXml(String xml) {
		XStream xstream = new XStream();
		xstream.processAnnotations(Employee.class);
		xstream.processAnnotations(Citizen.class);
		xstream.processAnnotations(Task.class);
		xstream.processAnnotations(TaskType.class);
		xstream.processAnnotations(Skill.class);
		xstream.processAnnotations(Vehicle.class);
		xstream.processAnnotations(TaskPlanningSolution.class);
		xstream.processAnnotations(Affinity.class);
		xstream.processAnnotations(Location.class);
		xstream.processAnnotations(AvailabilityRequest.class);
		xstream.processAnnotations(UnavailabilityRequest.class);
		xstream.setMode(XStream.ID_REFERENCES);
		//xstream.setMode(XStream.XPATH_RELATIVE_REFERENCES);
		xstream.ignoreUnknownElements();
		xstream.registerConverter(new JodaTimeConverter());
		//xstream.registerConverter(new JodaTimeConverterNoTZ());
		xstream.registerConverter(new BendableLongScoreXStreamConverter());
		TaskPlanningSolution taskPlanningSolution = (TaskPlanningSolution) xstream.fromXML(xml);
		// taskPlanningSolution.setLocationList(graphHopperService.getLocationData(taskPlanningSolution.getLocationList()));
		return taskPlanningSolution;
	}

	private PlanningProblem savePlanningProblem(String id,String problemXml) {
		/*PlanningProblem planningProblem = (PlanningProblem) customRepository.findOne("_id",id,PlanningProblem.class);
		if(planningProblem==null) {
			planningProblem = new PlanningProblem();
			planningProblem.setPlanningId(id);
			planningProblem.setProblemXml(problemXml);
			planningProblem.setStatus(PlanningStatus.SOLVING);
			return (PlanningProblem) customRepository.save(planningProblem);
		}else{
			planningProblem.setProblemXml(problemXml);
			planningProblem.setStatus(PlanningStatus.SOLVING);
			return (PlanningProblem) customRepository.save(planningProblem);
		}*/
		return null;
	}

	public String makeXMLfromSolution(TaskPlanningSolution taskPlanningSolution) {
		XStream xStream = new XStream();
		xStream.setMode(XStream.ID_REFERENCES);
		return xStream.toXML(taskPlanningSolution);
	}

	/*public void updateTaskplanning(TaskPlanningSolution taskPlanningSolution) {
		List<task> tasks = new ArrayList<>();
		List<location> locations = new ArrayList<>();
		List<LocationDistance> locationDistances = customRepository.selectAll(LocationDistance.class);
		for (Employee emp : taskPlanningSolution.getEmployees()) {
			location location = getLocationWithDistanceData(locationDistances, emp.getLocation());
			emp.setLocation(location);
			if (!locations.contains(location))
				locations.add(location);
		}
		for (vehicle vehicle : taskPlanningSolution.getVehicleList()) {
			vehicle.setLocation(getLocationWithDistanceData(locationDistances, vehicle.getLocation()));
		}
		for (task task : taskPlanningSolution.getTaskList()) {
			location location = getLocationWithDistanceData(locationDistances, task.getLocation());
			if (location != null && location.getLocationInfos() != null) {
				task.setLocation(location);
				if (!locations.contains(location))
					locations.add(location);
				tasks.add(task);
			}
		}
		taskPlanningSolution.setLocationList(locations);
		taskPlanningSolution.setTaskList(tasks);
	}*/


/*	public Map submitXmlToPlanner(Map requestedData){
		TestTodo testTodo = new TestTodo("5000","sadasdasdsa",5,"singh");
		customRepository.save(testTodo);
		String planningProblemId = (String)requestedData.get("Id");
		String problemXml = (String)requestedData.get("xml");
		PlanningProblem planningProblem = savePlanningProblem(planningProblemId,problemXml);
		//TaskPlanningSolution solution = new TaskPlanningSolver().runSolver(problemXml);
		String solutonXml = makeXMLfromSolution(solution);
		List<String> solutions = planningProblem.getSolutionXml();
		solutions.add(solutonXml);
		planningProblem.setSolutionXml(solutions);
		sendSolutionToApp(solutonXml);
		return getsolutionbyXml(solution);
	}*/

	private void sendSolutionToApp(String xml){
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(StaticField.KAIROSURL);
		HttpEntity entity = null;
		HttpResponse response = null;
		post.setHeader("Authorization", appConfig.getKairosAuth());
		post.setHeader("content-type", "application/json");
		try {
			entity = new ByteArrayEntity(xml.getBytes("UTF-8"));
			post.setEntity(entity);
			response = client.execute(post);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public Map getModelBySolution(TaskPlanningSolution solution) {
		List<Map> updatedEmployees = new ArrayList<Map>();
		List<Map> availableButNotAssigned = new ArrayList<Map>();
		List<Map> tasksList = new ArrayList<>();
		List<Map> unAvailableEmp = new ArrayList<>();
		//assignAvailablitiesToRender(solution);
		int i = 1;
		int assignedEmp=0, availableEmps=0;
		List<AvailabilityRequest> availabilityRequests = solution.getAvailabilityList();
		for (Employee emp : solution.getEmployeeList()) {
			List<AvailabilityRequest> empRequests= getEmployeeAvailabilityRequests(availabilityRequests,emp);
			if(emp.getNextTask()!=null ){//&& emp.getAvailabilityList().size()>0
				Map<String, Object> map = new HashMap<>();
				List<Map> tasks = new ArrayList<>();
				makeEmployeeList(tasks, emp.getNextTask());
				map.put("employeeName", emp.getName());
				map.put("employeeId", emp.getId());
				map.put("employeeNumber", i);
				map.put("vehicleId", emp.getVehicle()==null?-1:emp.getVehicle().getId());
				map.put("employeeId", emp.getId());
				map.put("employeeLocation", emp.getLocation());
				map.put("availableTime",empRequests.isEmpty()?"none": empRequests.get(0).getStartTime().toString("MM/dd/yyyy HH:mm:ss")+"-"+empRequests.get(0).getEndTime().toString("MM/dd/yyyy HH:mm:ss"));
				map.put("plannedTime", emp.getWorkIntervalAsString());
				map.put("nextTasks", tasks);
				updatedEmployees.add(map);
				assignedEmp++;
				if(empRequests.size()>0)
				availableEmps++;
				i++;
				}else if(empRequests.size()>0){
				Map<String, Object> map = new HashMap<>();
				map.put("employeeName", emp.getName());
				map.put("employeeId", emp.getId());
				map.put("employeeNumber", i);
				map.put("vehicleId", emp.getVehicle()==null?-1:emp.getVehicle().getId());
				map.put("employeeId", emp.getId());
				map.put("employeeLocation", emp.getLocation());
				//map.put("availableTime", emp.getAvailabilityList().get(0));
				map.put("availableTime",empRequests.isEmpty()?"none": empRequests.get(0).getStartTime().toString("MM/dd/yyyy HH:mm:ss")+"-"+empRequests.get(0).getEndTime().toString("MM/dd/yyyy HH:mm:ss"));
				map.put("plannedTime", emp.getWorkIntervalAsString());
				map.put("nextTasks", new ArrayList<>());
				availableButNotAssigned.add(map);
				availableEmps++;
			}
			else{
				Map<String,Object> map = new HashMap<>();
				map.put("employeeName", emp.getName());
				map.put("employeeNumber", i);
				map.put("employeeId", emp.getId());
				unAvailableEmp.add(map);
			}
		}
		for (Task task : solution.getTaskList()) {
			if(task.getEmployee()==null){
				Map<String,Object> map = new HashMap<>();
				map.put("id", task.getId());
				map.put("hardcontrants", task.getBrokenHardConstraints());
				map.put("taskLocation", task.getLocation());
				map.put("taskName", task.getTaskName());
				map.put("citizenName", task.getCitizen().getName());
                map.put("startTime1", task.getInitialStartTime1().toDate());
                map.put("endTime1", task.getInitialEndTime1().toDate());
                map.put("startTime2", task.getInitialStartTime2().toDate());
                map.put("endTime2", task.getInitialEndTime2().toDate());
				map.put("plannedStartTime", task.getPlannedStartTime().toDate());
				map.put("plannedEndTime", task.getPlannedEndTime().toDate());
                map.put("slaStart1", task.getSlaDurationStart1());
                map.put("slaEnd1", task.getSlaDurationEnd1());
                map.put("slaStart2", task.getSlaDurationStart2());
                map.put("slaEnd2", task.getSlaDurationEnd2());
				tasksList.add(map);
			}
		}
		Collections.sort(tasksList, new Comparator<Map>(){

			@Override
			public int compare(Map arg0, Map arg1) {
				//((Date)arg0.get("startTime"))
				return ((Date)arg0.get("startTime1")).compareTo((Date)arg1.get("startTime1"));
			}
			
			
		});
		updatedEmployees.addAll(availableButNotAssigned);
		Map<String,Object> resp = new HashMap<>();
		resp.put("taskListSize", solution.getTaskList().size());
		resp.put("citizenList", solution.getCitizenList().size());
		resp.put("employeeList", solution.getEmployeeList().size());
		resp.put("locationList", solution.getLocationList().size());
		resp.put("vehicleList", solution.getVehicleList().size());
		resp.put("assignedEmp", assignedEmp);
		resp.put("unAssignEmp", unAvailableEmp.size());
		resp.put("plannerScore", solution.getScore().toString());
		
		resp.put("avialableEmp", availableEmps);
		resp.put("unassignTaskList", tasksList.size());
		resp.put("unassignTask", tasksList);
		resp.put("unavailableEmployees", unAvailableEmp);
		
		resp.put("emplyees", updatedEmployees);
		return resp;
	}

	private List<AvailabilityRequest> getEmployeeAvailabilityRequests(List<AvailabilityRequest> availabilityRequests, Employee emp) {
        return availabilityRequests.stream().filter(ar->ar.getEmployee().getId().equals(emp.getId())).collect(Collectors.toList());
	}
	/*private void assignAvailablitiesToRender(TaskPlanningSolution solution) {
		//TODO: need to remove
		//solution.getAvailabilityList().forEach(ar->{
		for(AvailabilityRequest ar : solution.getAvailabilityList()){
//			solution.getEmployees().forEach(emp->{
			for(Employee emp:solution.getEmployees()){
				if(ar.getEmployee().getId().equals(emp.getId())){
					List list = emp.getAvailabilityList()==null? new ArrayList(): emp.getAvailabilityList();
					list.add(new AvailabilityRequest(ar.getId(), ar.getStartTime(), ar.getEndTime(), null));
					log.info("adding..."+list);
					emp.setAvailabilityList(list);
				}
			}
		}
		for(Employee emp:solution.getEmployees()){
			if(emp.getAvailabilityList()==null){
				emp.setAvailabilityList(new ArrayList());
			}
		}
	}*/





/*	private location getLocationWithDistanceData(List<LocationDistance> locationDistances, location location) {
		List<LocationInfo> locationInfos = new ArrayList<>();
		for (LocationDistance locationDistance : locationDistances) {
			if (location.getLatitude() == 0.0000000000000000)
				return null;
			if (locationDistance.getFirstLocationId()==location.getId()) {
				LocationInfo locationInfo = new LocationInfo();
				locationInfo.setLocationId(locationDistance.getSecondLocationId());
				locationInfo.setDistance(locationDistance.getDistanceByCar());
				locationInfo.setTime(locationDistance.getTimeByCar());
				locationInfos.add(locationInfo);
			}
			location.setLocationInfos(locationInfos);
		}

		return location;
	}*/

	private Location getOptaLocationByTask(List<Location> locations, Task task) {
		for (Location location : locations) {
			if (task.getLocation().getLatitude() == location.getLatitude()
					&& task.getLocation().getLongitude() == location.getLongitude())
				return location;
		}
		return null;
	}

	public void makeEmployeeList(List<Map> tasks, Task nextTask) {
		if (nextTask != null) {
			Map<String, Object> map = new HashMap<>();
			map.put("id", nextTask.getId());
			map.put("taskName", nextTask.getTaskName());
			map.put("citizenName", nextTask.getCitizen().getName());
			map.put("startTime1", nextTask.getInitialStartTime1().toDate());
			map.put("endTime1", nextTask.getInitialEndTime1().toDate());
			map.put("startTime2", nextTask.getInitialStartTime2().toDate());
			map.put("endTime2", nextTask.getInitialEndTime2().toDate());
            map.put("plannedInSecondInterval", nextTask.isPlannedInSecondInterval());
            map.put("plannedInExtendedInterval", nextTask.isPlannedInExtendedInterval());


            map.put("slaStart1", nextTask.getSlaDurationStart1());
            map.put("slaEnd1", nextTask.getSlaDurationEnd1());
            map.put("slaStart2", nextTask.getSlaDurationStart2());
            map.put("slaEnd2", nextTask.getSlaDurationEnd2());


			map.put("plannedStartTime", nextTask.getPlannedStartTime().toDate());
			map.put("plannedEndTime", nextTask.getPlannedEndTime().toDate());
			map.put("brokenhardcontrants", nextTask.getBrokenHardConstraintsSum()<0);
            map.put("brokenOrder", nextTask.hasBrokenOrder());
			map.put("brokenBoundries", nextTask.hasBrokenBoundries());
			map.put("hardcontrants", nextTask.getBrokenHardConstraints());

			map.put("taskLocation", nextTask.getLocation());
			map.put("arrivaltime", nextTask.getDrivingMinutesFromPreviousTaskOrEmployee());
			map.put("waitingtime", nextTask.getWaitingMinutes());

            map.put("multiMan", nextTask.isMultiManTask());
            map.put("relatedTaskId", nextTask.getRelatedTaskId());
            if( nextTask.getDependsUpon()!=null){
				map.put("dependsUponId", nextTask.getDependsUpon().getId());
			}
			if(nextTask.getNextTask()==null) map.put("timeReachToUnit", nextTask.getTimeToReachBackUnit());
			tasks.add(map);
			makeEmployeeList(tasks, nextTask.getNextTask());
		}
	}

	/*public List<PlanningProblem> getAllPlanning() {
		return (List<PlanningProblem>) customRepository.selectAll(PlanningProblem.class);
	}

	private void saveDatatoDB(List<task> tasks) {
		customRepository.insert(tasks);
		log.info("data saved succesfully");
	}*/

/*
	public List<Map> getSolutionById(String id) {
		String xml = optaPlannerService.getSolutionFromKieServer(id);
		if (!xml.contains("NOT_SOLVING"))
			return null;
		String solutionString = "<com.kairos.planner.solution.TaskPlanningSolution>"
				+ (xml.substring(xml.indexOf("<vehicleList>"), xml.indexOf("</solver-instance>")))
						.replace("</best-solution>", "</com.kairos.planner.solution.TaskPlanningSolution>");
		XStream xstream = new XStream();
		xstream.processAnnotations(TaskPlanningSolution.class);
		xstream.ignoreUnknownElements();
		xstream.setMode(XStream.XPATH_RELATIVE_REFERENCES);
		TaskPlanningSolution solution = (TaskPlanningSolution) xstream.fromXML(solutionString);
		PlanningProblem planningProblem = getPlanningProblemByid(id);
		planningProblem.setStatus(PlanningStatus.SOLVED);
		customRepository.save(planningProblem);
		List<Map> updatedEmployees = new ArrayList<Map>();
		List<Map> tasks = new ArrayList<>();
		int i = 1;
		for (Employee emp : solution.getEmployees()) {
			Map<String, Object> map = new HashMap<>();
			makeEmployeeList(tasks, emp.getNextTask());
			map.put("employeeName", emp.getName());
			map.put("employeeNumber", i);
			map.put("employeeId", emp.getId());
			if (!emp.getAvailabilityList().isEmpty())
				map.put("availableTime", emp.getAvailabilityList().get(0));
			map.put("nextTasks", tasks);
			tasks = new ArrayList<>();
			updatedEmployees.add(map);
			i++;
		}
		return updatedEmployees;

	}
*/

}
