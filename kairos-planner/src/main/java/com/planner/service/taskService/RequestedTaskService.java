package com.planner.service.taskService;


import org.springframework.stereotype.Service;

@Service
public class RequestedTaskService {
	
	  /*List<BasicDBObject> dbTask = new ArrayList();
		Map<Long,List<BasicDBObject>> shiftDb = new HashMap<Long,List<BasicDBObject>>();
		List<Map> clientResultMap = new ArrayList<>();
		List<Map> stafflistMap = new ArrayList<>();
		List<vehicle> vehicles = new ArrayList<>();
		List<citizen> citizens = new ArrayList<>();
		List<task> tasks = new ArrayList<>();
		List<TaskType> taskTypes = new ArrayList<>();
		List<Skill> skills = new ArrayList<>();
		List<location> locations = new ArrayList<>();
		List<AvailabilityRequest> availabilityRequests = new ArrayList<>();
		List<Employee> employees = new ArrayList<>();
	
	 private void makeListFromDbData(){
			locationsList();
			makeTaskList(dbTask);
			//makeAvailabilityRequestList(shiftDb);
			makeEmployeeList(stafflistMap);
			makeVehicleList();
			makeXMlFromData();
			
		}
		
		private void locationsList(){
			locations.add(new location("Gurgaon",28.4595,77.0266));
			locations.add(new location("Noida",28.5355,77.3910));
			locations.add(new location("Delhi",28.7041,77.1025));
			locations.add(new location("Rohtak",28.8955,76.6066));
			locations.add(new location("Faridabad",28.4089,77.3178));
			locations.add(new location("Palwal",28.1487,77.3320));
			locations.add(new location("Ghaziabad",28.6692,77.4538));
			locations.add(new location("Hapur",28.7306,77.7759));
			//GraphHopper graphHopper = new GraphHopper();
			//locations = graphHopper.getLocationData(locations);
		}

		private void makeVehicleList() {
			List<Skill> skill = new ArrayList<>();
			vehicle vehicle = new vehicle();
			vehicle.setId((long)0);
			vehicle.setType("Car");
			vehicle.setSpeedKmpm(70.4);
			vehicle.setLocation(locations.get(0));
			Skill skl = new Skill();
			skl.setId((long) 0);
			skl.setName("Car driving");
			skill.add(skl);
			vehicle.setRequiredSkillList(skill);
			vehicles.add(vehicle);
			vehicle = new vehicle();
			vehicle.setId((long)0);
			vehicle.setType("Bike");
			vehicle.setSpeedKmpm(70.4);
			vehicle.setLocation(locations.get(0));
			skl = new Skill();
			skl.setId((long) 1);
			skl.setName("Bike driving");
			skill = new ArrayList<>();
			skill.add(skl);
			vehicle.setRequiredSkillList(skill);
			vehicles.add(vehicle);
			vehicle = new vehicle();
			vehicle.setId((long)0);
			vehicle.setType("Bicycle");
			vehicle.setSpeedKmpm(70.4);
			vehicle.setLocation(locations.get(0));
			skl = new Skill();
			skl.setId((long) 2);
			skl.setName("Bicycle driving");
			skill = new ArrayList<>();
			skill.add(skl);
			vehicle.setRequiredSkillList(skill);
			vehicles.add(vehicle);

			LongStream.range(0, 30).forEach(i->{
				vehicles.add(createVehicle(i));
					}

			);
		}
		private vehicle createVehicle(Long id){
			vehicle vehicle = new vehicle();
			vehicle.setId(id);
			vehicle.setType("Car");
			vehicle.setSpeedKmpm(70.4);
			vehicle.setLocation(locations.get(0));
			vehicle.setRequiredSkillList(new ArrayList<>());
			return vehicle;
		}
		private void makeTaskList(List<BasicDBObject> dbtask1) {
			task task;
			long id = 0;
			List<Skill> skill;
			Skill skl;
			TaskType taskType;
			for (BasicDBObject task1 : dbtask1) {
				skill = new ArrayList<>();
				task = new task();
				Address address;
				if(task1.containsKey("address")){
					Map<String, Object> resdatas = (Map)task1.get("address");
					address = new Address((String)resdatas.get("street"),"",(String)resdatas.get("country"),(String)resdatas.get("houseNumber"),(Integer)resdatas.get("zip"),(String)resdatas.get("city"));
				}
				else address = null;
					citizen citizen = new citizen((Long)task1.get("citizenId"),"",address);
				citizens.add(citizen);
				task.setCitizen(citizen);
				task.setId(id);
				task.setPriority((Integer)task1.get("priority"));
				task.setLocation(locations.get(new Random().nextInt(3)));
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
				Date dateFrom = (Date)task1.get("timeFrom");
				Date dateTo = (Date)task1.get("timeTo");
				task.setStartTime(dateFrom);
				task.setEndTime(dateTo);
				task.setEarlyStartMinutes((Integer)task1.get("slaStartDuration"));
				taskType = new TaskType();
				taskType.setBaseDuration((long)0);
				taskType.setCode("01254F");
				taskType.setTitle("motor");
				skill.add(skills.get(0));
				taskType.setRequiredSkillList(skill);
				taskTypes.add(taskType);
				task.setTaskType(taskType);
				tasks.add(task);
				id++;
			}
		}


		private void makeAvailabilityRequestList(List<BasicDBObject> shiftDb) {
			long id = 0;
			AvailabilityRequest availabilityRequest;
			for (BasicDBObject basicDBObject : shiftDb) {
				availabilityRequest = new AvailabilityRequest();
				availabilityRequest.setId(id);
				availabilityRequest.setStartTime(DateFormatUtils.format((Date)basicDBObject.get("startDate"), "yyyy-MM-dd HH:mm:SS"));
				availabilityRequest.setEndTimeString(DateFormatUtils.format((Date)basicDBObject.get("endDate"), "yyyy-MM-dd HH:mm:SS"));
				availabilityRequests.add(availabilityRequest);
			}
		}

		private void makeEmployeeList(List<Map> dbstaff1) {

			//long skillId = 0;
			for (Map staffmap : dbstaff1) {
				Set<Skill> skillSet = new HashSet<>();
				skillSet.add(skills.get(0));
				long employeeId = (long)staffmap.get("id");
				Employee employee = new Employee(employeeId,(String)staffmap.get("firstName")+" "+staffmap.get("lastName"));
				//Long availblerequestId = 0l;
				List<AvailabilityRequest> availabilityList = new ArrayList<>();
				AvailabilityRequest availabilityRequest;
				List<BasicDBObject> shifts = shiftDb.get(employeeId)==null?new ArrayList<>():shiftDb.get(employeeId);
				for (BasicDBObject shift : shifts) {
					//String staffId = "";
					//String shiftId = "";
					//if(shift.containsKey("staffId")) staffId = shift.get("staffId")!=null?Long.toString((long)shift.get("staffId")):Long.toString((long)shift.get("anonymousStaffId"));
					//if(shift.containsKey("id")) shiftId = Long.toString((long) staffmap.get("id"));
	 				//if(staffId!=null && shiftId!=null){
	 					availabilityRequest = new AvailabilityRequest();
	 					availabilityRequest.setId(Long.parseLong(shift.get("_id").toString()));
						availabilityRequest.setStartTime(DateFormatUtils.format((Date)shift.get("startDate"), "MM/dd/yyyy HH:mm:ss"));
						availabilityRequest.setEndTimeString(DateFormatUtils.format((Date)shift.get("endDate"), "MM/dd/yyyy HH:mm:ss"));
						availabilityRequests.add(availabilityRequest);
						availabilityList.add(availabilityRequest);

						//availblerequestId+=availblerequestId;
	 				//}
				}
				employee.setAvailabilityList(availabilityList);
				employee.setSkillSet(skillSet);
				employees.add(employee);
				//id++;
			}
		}

		public void loadXMLFromDB(){
			makeListFromDbData();
		}
		
		private void makeXMlFromData(){
			XStream xStream = new XStream();
			xStream.setMode(XStream.ID_REFERENCES);
			//xStream.setMode(XStream.NO_REFERENCES);
			TaskPlanningUnSolved taskPlanningSolution = new TaskPlanningUnSolved();
			taskPlanningSolution.setTaskTypeList(taskTypes);
			taskPlanningSolution.setSkillList(skills);
			taskPlanningSolution.setTaskList(tasks);
			taskPlanningSolution.setAvailabilityList(availabilityRequests);
			taskPlanningSolution.setEmployees(employees);
			taskPlanningSolution.setCitizenList(citizens);
			taskPlanningSolution.setVehicleList(vehicles);
			taskPlanningSolution.setLocationList(locations);
			String xml = xStream.toXML(taskPlanningSolution);
			try {
	            PrintWriter out = new PrintWriter("src/main/resources/data/unplannedTask.xml");
	            out.println(xml);
	            out.close();
	            System.out.println("file complete");
	        } catch (FileNotFoundException ex) {
	            System.out.println(ex.getMessage());
	        }
		}*/

}
