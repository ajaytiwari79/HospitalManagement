package com.kairos.planning.executioner;


public class RequestedTask {
	/*public static final int TASK_LIMIT = 100;
	Logger log= LoggerFactory.getLogger(RequestedTask.class);

	Driver driver;
	Session session;
	List<BasicDBObject> dbTask = new ArrayList();
	Map<Long,List<BasicDBObject>> shiftDb = new HashMap<Long,List<BasicDBObject>>();
	List<Map> clientResultMap = new ArrayList<>();
	List<Map> stafflistMap = new ArrayList<>();
	List<Vehicle> vehicles = new ArrayList<>();
	List<Citizen> citizens = new ArrayList<>();
	List<Task> tasks = new ArrayList<>();
	List<TaskType> taskTypes = new ArrayList<>();
	List<Skill> skills = new ArrayList<>();
	List<Location> locations = new ArrayList<>();
	List<AvailabilityRequest> availabilityRequests = new ArrayList<>();
	List<Employee> employees = new ArrayList<>();
	{
		Skill skill = new Skill();
		skill.setId(0l);
		skill.setName("Alle ydelser");
		skills.add(skill);
	}
	public void readMongoTask() {
		MongoClient mongo = new MongoClient(new ServerAddress("localHost", 27017));
		DB mongodb = mongo.getDB("kairos");
		DBCollection mongoCollection = mongodb.getCollection("tasks");
		BasicDBObject query = new BasicDBObject();
		query.put("taskStatus", "GENERATED");
		//Date start= new DateTime().withTimeAtStartOfDay().minusDays(36).toDate(),
		//		end =new DateTime().withTimeAtStartOfDay().minusDays(35).toDate();
		Date start= DateTime.parse("07/02/2017", DateTimeFormat.forPattern("MM/dd/yyyy")).toDate(),
				end =DateTime.parse("07/03/2017", DateTimeFormat.forPattern("MM/dd/yyyy")).toDate();
		query.put("timeCareExternalId", new BasicDBObject("$exists", false));
		query.put("timeFrom",  BasicDBObjectBuilder.start("$gte", start).add("$lt",end).get());
		long count = mongoCollection.count(query);
		//for(int i = 0;i<=count;i+=100){
		log.info(query.toString());
			DBCursor list = mongoCollection.find(query).limit(TASK_LIMIT);
			while (list.hasNext()) {
				dbTask.add((BasicDBObject)list.next());
			}
			query = new BasicDBObject();
			query.put("timeCareExternalId", new BasicDBObject("$ne", null));
		query.put("startDate",  BasicDBObjectBuilder.start("$gte", start).add("$lt", end).get());
		log.info(query.toString());
			list = mongoCollection.find(query).limit(20000);
			while (list.hasNext()) {
				BasicDBObject shift= (BasicDBObject)list.next();
				Long key = shift.get("staffId")==null?shift.getLong("staffAnonymousId"):shift.getLong("staffId");
				List shifts = shiftDb.get(key)==null?new ArrayList():shiftDb.get(key);
				shifts.add(shift);
				shiftDb.put(key,shifts);
				//shiftDb.add();
			}
				
		//}
		mongo.close();
		//readNeo4j();
	}

	public void readNeo4j() {
		driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "oodles"));
		session = driver.session();
		StatementResult staffResult = session.run("Match (n:Staff) return {id:id(n),firstName:n.firstName,lastName:n.lastName} as data");
		StatementResult clientResult = session.run("Match (n:Client) return n");
		// StatementResult result = transaction.run("Match (n:Country) return
		// n");

		for (Record record : clientResult.list()) {
			List<Value> data = record.values();
			data.forEach(value -> {
				Map<String, Object> resdata = value.asMap();
				clientResultMap.add(resdata);
			});
		}
		
		
		for (Record record : staffResult.list()) {
			List<Value> data = record.values();
			data.forEach(value -> {
				Map<String, Object> resdata = value.asMap();
				stafflistMap.add(resdata);
			});
		}
		

	}
	
	private void makeListFromDbData(){
		locationsList();
		makeTaskList(dbTask);
		//makeAvailabilityRequestList(shiftDb);
		makeEmployeeList(stafflistMap);
		makeVehicleList();
		makeXMlFromData();
		
	}
	
	private void locationsList(){
		locations.add(new Location(0l,"Gurgaon",28.4595,77.0266));
		locations.add(new Location(1l,"Noida",28.5355,77.3910));
		locations.add(new Location(2l,"Delhi",28.7041,77.1025));
		locations.add(new Location(3l,"Rohtak",28.8955,76.6066));
		locations.add(new Location(4l,"Faridabad",28.4089,77.3178));
		locations.add(new Location(5l,"Palwal",28.1487,77.3320));
		locations.add(new Location(6l,"Ghaziabad",28.6692,77.4538));
		locations.add(new Location(7l,"Hapur",28.7306,77.7759));


		GraphHopper graphHopper = new GraphHopper();
		graphHopper.getLocationData(locations);
	}

	private void makeVehicleList() {
		*//*List<Skill> skill = new ArrayList<>();
		Vehicle vehicle = new Vehicle();
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
		vehicle = new Vehicle();
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
		vehicle = new Vehicle();
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
		vehicles.add(vehicle);*//*

		LongStream.range(0, 30).forEach(i->{
			vehicles.add(createVehicle(i));
				}

		);
	}
	private Vehicle createVehicle(Long id){
		Vehicle vehicle = new Vehicle();
		vehicle.setId(id);
		vehicle.setType("Car");
		vehicle.setSpeedKmpm(1.4);
		vehicle.setLocation(locations.get(0));
		vehicle.setRequiredSkillList(new ArrayList<>());
		return vehicle;
	}
	private void makeTaskList(List<BasicDBObject> dbtask1) {
		Task task;
		long id = 0;
		List<Skill> skill;
		Skill skl;
		TaskType taskType;
		for (BasicDBObject task1 : dbtask1) {
			skill = new ArrayList<>();
			task = new Task();
			Address address;
			if(task1.containsKey("address")){
				Map<String, Object> resdatas = (Map)task1.get("address");
				address = new Address((String)resdatas.get("street"),"",(String)resdatas.get("country"),(String)resdatas.get("houseNumber"),(Integer)resdatas.get("zip"),(String)resdatas.get("city"));
			}
			else address = null;
				Citizen citizen = new Citizen((Long)task1.get("citizenId"),"",address);
			citizens.add(citizen);
			task.setCitizen(citizen);
			task.setId(id);
			task.setPriority((Integer)task1.get("priority"));
			task.setLocation(locations.get(ThreadLocalRandom.current().nextInt(1, 7)));
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
			Date dateFrom = (Date)task1.get("timeFrom");
			Date dateTo = (Date)task1.get("timeTo");
			task.setInitialStartTime1(new DateTime(dateFrom));
			task.setInitialEndTime1(new DateTime(dateTo));
			//task.setEarlyStartMinutes((Integer)task1.get("slaStartDuration"));
			task.setSlaDurationStart1((Integer)task1.get("slaStartDuration"));
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
		*//*long id = 0;
		AvailabilityRequest availabilityRequest;
		for (BasicDBObject basicDBObject : shiftDb) {
			availabilityRequest = new AvailabilityRequest();
			availabilityRequest.setId(id);
			availabilityRequest.setStartTime(DateFormatUtils.format((Date)basicDBObject.get("startDate"), "yyyy-MM-dd HH:mm:SS"));
			availabilityRequest.setEndTimeString(DateFormatUtils.format((Date)basicDBObject.get("endDate"), "yyyy-MM-dd HH:mm:SS"));
			availabilityRequests.add(availabilityRequest);
		}*//*
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
			employee.setLocation(locations.get(0));
			employees.add(employee);
			//id++;
		}
	}

	public void loadXMLFromDB(){
		readMongoTask();
		readNeo4j();
		makeListFromDbData();
	}
	
	private void makeXMlFromData(){
		XStream xStream = new XStream();
		//xStream.setMode(XStream.ID_REFERENCES);
		//xStream.setMode(XStream.NO_REFERENCES);
		xStream.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);
		TaskPlanningSolution taskPlanningSolution = new TaskPlanningSolution();
		taskPlanningSolution.setTaskTypeList(taskTypes);
		taskPlanningSolution.setSkillList(skills);
		taskPlanningSolution.setTaskList(tasks);
		taskPlanningSolution.setAvailabilityList(availabilityRequests);
		taskPlanningSolution.setEmployeeList(employees);
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
	}
	
	public boolean subtract(Date fromDate, Date toDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(fromDate);
		cal.add(Calendar.DATE, -10);
		Date date14Days = cal.getTime();
		cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -7);
		Date date7Days = cal.getTime();
		if(date14Days.getTime()<fromDate.getTime() && date7Days.getTime()>toDate.getTime())
		return true;
		else return false;
	}*/
}
