package com.kairos.planning.executioner;

import com.kairos.planning.domain.*;
import com.kairos.planning.graphhopper.GraphHopper;
import com.kairos.planning.solution.TaskPlanningSolution;
import com.kairos.planning.utils.JodaTimeConverter;
import com.kairos.planning.utils.TaskPlanningUtility;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.lang.ArrayUtils;
import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaplanner.core.api.solver.event.SolverEventListener;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirector;
import org.optaplanner.persistence.xstream.api.score.buildin.bendablelong.BendableLongScoreXStreamConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.StringReader;
import java.util.*;

public class TaskPlanningSolver {


	public static String config = "com/kairos/planning/configuration/OutdoorTaskPlanning.solver.xml";
    public static String benchMarkerConfig = "com/kairos/planning/configuration/OutdoorTaskPlanningBenchmark.solver.xml";

    private static Logger log= LoggerFactory.getLogger(TaskPlanningSolver.class);
	Solver<TaskPlanningSolution> solver;
	DroolsScoreDirector<TaskPlanningSolution> director;
	
	SolverFactory<TaskPlanningSolution> solverFactory;
	static{
        System.setProperty("user.timezone", "UTC");
    }
	public TaskPlanningSolver(){
		solverFactory = SolverFactory.createFromXmlResource(config);
		//SolverConfig solverConfig = solverFactory.getSolverConfig().getScoreDirectorFactoryConfig().setScoreDrlFileList();
		/*solver.addEventListener(new SolverEventListener<TaskPlanningSolution>(){

			@Override
			public void bestSolutionChanged(BestSolutionChangedEvent<TaskPlanningSolution> event) {
				//log.info("called:"+event);
				DefaultSolver<TaskPlanningSolution> solver=(DefaultSolver<TaskPlanningSolution>)event.getSource();
				TaskPlanningUtility.updateInsertedAvialabilities(solver.getSolverScope().getScoreDirector());
				
				
			}
			
		});*/
		//director=(DroolsScoreDirector<TaskPlanningSolution>)((DefaultSolver)solver).getSolverScope().getScoreDirector();
	}

	public static void main(String[] s ){
		new TaskPlanningSolver().runSolver();
	}

	public TaskPlanningSolution runSolver() {
		try {
			Object[] solvedSolution = getSolution();
			printSolvedSolution(solvedSolution);
			return (TaskPlanningSolution)solvedSolution[0];
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private void printSolvedSolution(Object[] solution) {
		log.info("-------Printing solution:-------");
		//List<Task> tasks= solution.getTaskList();
		//log.info("-------Tasks:-------");
		log.info(toDisplayString(solution));
		//tasks.forEach(task->log.info(task.toString()));
		log.info("-------Printing solution Finished:-------");
	}

	public Object[] getSolution() throws Exception{
		
		TaskPlanningSolution unsolvedSolution=getUnsolvedSolution();
		log.info("Number of locations:"+unsolvedSolution.getLocationList().size());
		log.info("Number of Tasks:"+unsolvedSolution.getTaskList().size());
		log.info("Number of Vehicless:"+unsolvedSolution.getVehicleList().size());
		log.info("Number of Employees:"+unsolvedSolution.getEmployeeList().size());
		Long availableEmp=0l, availableMinutes=0l;
		/*for(Employee emp: unsolvedSolution.getEmployeeList()){
			if(emp.getAvailabilityList()==null) continue;
			availableMinutes+=emp.getAvailableMinutes();
			if(emp.getAvailableMinutes()>0){
				availableEmp++;
			}
		}*/
		log.info("Number of Available Employees:"+unsolvedSolution.getAvailabilityList().size());
		try {
			toXml(unsolvedSolution, null, "problem");
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		printUnsolvedSolution(unsolvedSolution);
		long start= System.currentTimeMillis();
		
		//director.setWorkingSolution(unsolvedSolution);
		 
		
		
		/*KnowledgeBaseImpl kbase=(KnowledgeBaseImpl)scoreDirectorFactory.getKieBase();//.newKieSession();
		//kbase.kieBaseListeners
		kbase.addEventListener(new WorkingMemoryLogger(){

			@Override
			public void logEventCreated(LogEvent logEvent) {
				log.info(logEvent.toString());
				
			}
			
		});*/
		
		
		//assignments(unsolvedSolution);
		solver.solve(unsolvedSolution);
		log.info("final generated availability requests:");
		TaskPlanningUtility.updatedList.forEach(req->{
			log.info(req.toString());
		});
		//solver.addEventListener(eventListener);
		/*}catch(Exception e){
			log.error("Exception",e);
		}*/
		TaskPlanningSolution solution = solver.getBestSolution();
		log.info("Solver took secs:"+(System.currentTimeMillis()-start)/1000);
		
		
		
		//StatefulKnowledgeSessionImpl newKieSession = (StatefulKnowledgeSessionImpl)kbase.newKieSession();
		//InternalWorkingMemory internalWorkingMemory = newKieSession.getInternalWorkingMemory();
		/*InternalWorkingMemory internalWorkingMemory =((StatefulKnowledgeSessionImpl)kbase.getWorkingMemories()[0]).getInternalWorkingMemory();
		
		Collection abc= internalWorkingMemory.getFactHandles(new ObjectFilter() {
			@Override
			public boolean accept(Object object) {
				if(object instanceof AvailabilityRequest){
					return ((AvailabilityRequest)object).isAutogenerated();
				}
				return false;
			}
		});
		int s= abc.size();
		log.info(String.valueOf(s));*/
		
		
	/*	KnowledgeBaseImpl base=(KnowledgeBaseImpl)sess.getKieBase();
		WorkingMemory[] mems=base.getWorkingMemories();
		Arrays.stream(mems).forEach(mem->{
			StatefulKnowledgeSessionImpl ses=(StatefulKnowledgeSessionImpl) mem;
			//ses.getInternalWorkingMemory().getf
			log.info(ses.toString());
		});
		//log.info(sess.);
		//sess.inser
		//solver.
*/		
		//LegacyDroolsScoreDirectorFactory<TaskPlanningSolution> scoreDirectorFactory = (LegacyDroolsScoreDirectorFactory)director.getScoreDirectorFactory();
		DroolsScoreDirector<TaskPlanningSolution> director=(DroolsScoreDirector<TaskPlanningSolution>)solver.getScoreDirectorFactory().buildScoreDirector();

		director.setWorkingSolution(solution);
		Map<Task,Indictment> indictmentMap=(Map)director.getIndictmentMap();

		//printEntitiesThatBrokeContraints(indictmentMap);
		//return solver.getBestSolution();
		return new Object[]{solution,indictmentMap,director.getConstraintMatchTotals()};
	}

	private void assignments(final TaskPlanningSolution unsolvedSolution) {
		unsolvedSolution.getEmployeeList().forEach(emp->{
			if(emp.getNextTask()!=null){
				Task task = emp.getNextTask();
				emp.setVehicle(unsolvedSolution.getVehicleList().get(0));
				while(task!=null && task.isLocked()){
					task.setEmployee(emp);
					task.setPlannedStartTime(task.getInitialStartTime1());
					task=task.getNextTask();

				}
			}
		});
		
	}
	public void benchmarkForSolution() {
        TaskPlanningSolution unsolvedSolution=getUnsolvedSolution();
		//SolverFactory<TaskPlanningSolution> solverFactory = SolverFactory.createFromXmlResource(benchMarkerConfig);
		//PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.createFromSolverFactory(solverFactory);
        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.createFromXmlResource(benchMarkerConfig);
		PlannerBenchmark plannerBenchmark = benchmarkFactory.buildPlannerBenchmark(unsolvedSolution);
		plannerBenchmark.benchmark();
	}

	private long sumHardScore(BendableLongScore score){
		long hardScoreSum=Arrays.stream(score.getHardScores()).count();
		return hardScoreSum;
	}

	private void printEntitiesThatBrokeContraints(Map<Task,Indictment> indictmentMap) {
		log.info("***********Indictment***********");
		indictmentMap.forEach((task,indictment)->{
			log.info(task.getLabel()+":"+indictment.getScoreTotal());

		});
		log.info("***********End of Indictment***********");
	}

	private void printDistanceMatrix(TaskPlanningSolution unsolvedSolution) {
		List<Location> locations = unsolvedSolution.getLocationList();
		locations.forEach(location -> {
			log.info("--["+location.getName()+"]--");
			locations.forEach(location1 -> {
				log.info(location1.getName()+":"+location.getDistanceFrom(location1)+","+location1.getDistanceFrom(location));
			});
		});
	}

	private void printUnsolvedSolution(TaskPlanningSolution unsolvedSolution) {
		//printDistanceMatrix(unsolvedSolution);
		List<Location> locations= unsolvedSolution.getLocationList();
		List<Citizen> citizens= unsolvedSolution.getCitizenList();
		List<Task> tasks= unsolvedSolution.getTaskList();
		List<Vehicle> vehicles= unsolvedSolution.getVehicleList();
		List<Employee> employees= unsolvedSolution.getEmployeeList();
		log.info("-------Printing problem dataset:-------");
		/*log.info("-------Locations:-------");
		locations.forEach(location->log.info(location.toString()));
		log.info("-------Citizens:-------");
		citizens.forEach(citizen->log.info(citizen.toString()));
		log.info("-------Vehicles:-------");
		vehicles.forEach(vehicle->log.info(vehicle.toString()));
		log.info("-------Tasks:-------");
		tasks.forEach(task->log.info(task.toString()));*/
		log.info("-------Employees:-------");
		/*employees.forEach(employee->{
			if(employee.getAvailableMinutes()>0)
				log.info(employee.getName()+":"+employee.getAvailableMinutes().toString()+employee.getAvailableMinutesAsString());
		});*/

		log.info("-------Printing problem dataset completed-------");
	}
	private TaskPlanningSolution getUnsolvedSolution() {
		//return new TaskPlanningGenerator().loadUnsolvedSolution();

        TaskPlanningSolution unsolvedSolution=new TaskPlanningGenerator().loadUnsolvedSolutionFromXML();
       // assignEmployeeToAvaiability(unsolvedSolution.getEmployeeList(),true);
        GraphHopper graphHopper = new GraphHopper();
        graphHopper.getLocationData(unsolvedSolution.getLocationList());
        unsolvedSolution.getEmployeeList().forEach(emp->{
            emp.setAvialableMinutes( unsolvedSolution.getAvailabilityList().stream().filter(ar->ar.getEmployee().getId().equals(emp.getId())).mapToLong(ar->ar.getMinutes()).sum());
        });
        return unsolvedSolution;
	}
	private void assignEmployeeToAvaiability(List<Employee> employeeList,boolean assign) {
		/*employeeList.forEach(employee->{
			employee.getAvailabilityList().forEach(avail->{
				avail.setEmployee(assign?employee:null);
			});
		});*/
		
	}
	public String toDisplayString(Object[] array) {

		TaskPlanningSolution solution=(TaskPlanningSolution)array[0];
		log.info("---Task CH Filter attemts::---"+TaskMoveCHFilter.attemts);
		checkForLogicalFact(solution);
		//unassignTaskFromUnavailableEmployees(solution);
		Map<Task,Indictment> indictmentMap =(Map<Task,Indictment>) array[1];
        Collection<ConstraintMatchTotal> constraintMatchTotals= (Collection<ConstraintMatchTotal>) array[2];
        constraintMatchTotals.forEach(constraintMatchTotal -> {
            log.info(constraintMatchTotal.getConstraintName()+":"+"Total:"+constraintMatchTotal.toString()+"=="+"Reason(entities):");
            constraintMatchTotal.getConstraintMatchSet().forEach(constraintMatch -> {
                constraintMatch.getJustificationList().forEach(o -> {
                    log.info("---"+o);
                });
            });

        });

        StringBuilder displayString = new StringBuilder();
        StringBuilder taskChain = new StringBuilder("Task Chain:\n");
        displayString.append("\nTask assignment:");
        Map<String,Long> empMins= new HashMap<String,Long>();
        Set<Long> processChainPivot= new HashSet<Long>();
		Map<Long,Route> routes= new HashMap<Long,Route>();
		solution.getTaskList().forEach(task->{
			task.setBrokenHardConstraints(ArrayUtils.toObject(((BendableLongScore) indictmentMap.get(task).getScoreTotal()).getHardScores()));
		});
		try {
			toXml(solution, indictmentMap,"solution");
		}catch(Exception e){
			e.printStackTrace();
		}

		log.info("---emp mins:---"+empMins);
		StringBuilder employeeRoute= new StringBuilder();
		employeeRoute.append("\nEmployee assignment:\n");
        solution.getEmployeeList().forEach(employee->{
        	if(employee.getNextTask()==null) return;
			employeeRoute.append("\nEmployee :"+employee+", Vehicle:"+employee.getVehicle()+", Interval:"+employee.getWorkIntervalAsString()+""
					+(indictmentMap.get(employee)==null?"":(Arrays.toString(((BendableLongScore) indictmentMap.get(employee).getScoreTotal()).getHardScores())))
					+"\n");
        	Task nextTask=employee.getNextTask();
			//Employee employee= vehicle.getEmployee();
			//vehicleRoute.append("Employee :"+employee+"\n");
        	while(nextTask!=null){
				employeeRoute.append(nextTask.getLabel());//employeeRoute.append(nextTask.getName()+"->["+nextTask.getVehicle()+"]");
				//vehicleRoute.append("-["+nextTask.getName()+"]-");
				//vehicleRoute.append("----");
				if(indictmentMap.containsKey(nextTask) && sumHardScore((BendableLongScore) indictmentMap.get(nextTask).getScoreTotal())>0){
					employeeRoute.append(""+Arrays.toString(((BendableLongScore) indictmentMap.get(nextTask).getScoreTotal()).getHardScores())+"");
				}
				employeeRoute.append("->");
        		nextTask=nextTask.getNextTask();
        	}
			employeeRoute.append("\n");
        });
        //return displayString.append("\n").append(taskChain).toString();
		return employeeRoute.toString();
    }

	private void checkForLogicalFact(TaskPlanningSolution solution) {
	    int taskCounter=0;
		for (Employee employee : solution.getEmployeeList()) {
			Task currentTask= employee.getNextTask();
			while(currentTask!=null){
                taskCounter++;
				if(currentTask.isLocked()){
				    log.info("LOGICAL TASK FOUND");
                }
			    currentTask=currentTask.getNextTask();
			}
		}
		log.info("Tasks found:"+taskCounter);
	}

	private List<Task> unassignTaskFromUnavailableEmployees(TaskPlanningSolution solution) {
		List<Task> unassignedTask = new ArrayList<>();
		final int[] unassignedEmps = {0};
		solution.getEmployeeList().forEach(employee->
		{

			Task task =employee.getNextTask();
			if(task==null) unassignedEmps[0]++;
			//if(employee.getAvailableMinutes()>0l) return;
			Task nextTask= task;
			employee.setNextTask(null);

			while(nextTask!=null){
				nextTask.setEmployee(null);
				nextTask.setPreviousTaskOrEmployee(null);
				unassignedTask.add(nextTask);
				task=nextTask;
				nextTask=nextTask.getNextTask();
				task.setNextTask(null);
			}
		});
		log.info("Unassigned Tasks:"+unassignedTask.size());
		log.info("Unassigned Emps:"+unassignedEmps[0]);
		return unassignedTask;
	}

	private void toXml(TaskPlanningSolution solution, Map<Task, Indictment> indictmentMap, String fileName) throws Exception {
		try {
		    if(fileName.equals("solution"))
			    checkForCyclicProblems(solution);
			XStream xstream = new XStream();
            //XStream xstream = new XStream(new PureJavaReflectionProvider());

            //assignEmployeeToAvaiability(solution.getEmployeeList(),true);
			//xstream.setMode(XStream.XPATH_RELATIVE_REFERENCES);
			xstream.setMode(XStream.ID_REFERENCES);
			xstream.registerConverter(new JodaTimeConverter());
			// xstream.registerConverter(new JodaTimeConverterNoTZ());
			xstream.registerConverter(new BendableLongScoreXStreamConverter());
			String xmlString = xstream.toXML(solution);
			writeXml(xmlString, fileName);
		}catch(Throwable e){
			log.error("soe:",e);
			throw e;
		}
	}

	private void checkForCyclicProblems(TaskPlanningSolution solution) {
		solution.getEmployeeList().forEach(emp->{
		    Task task = emp.getNextTask();
		    int i=0;
		    try {
                while (task != null) {
                    i++;
                    task = task.getNextTask();
                }
            log.info("employee:{} tasks:{}",emp.getId(),i);
            }catch(Throwable e){
		        log.error("problem with emp:{}",emp.getId(),e);
            }
        });
	}

	public void writeXml(String xmlString,String fileName) throws Exception{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
			// Use String reader
			Document document = builder.parse( new InputSource(
					new StringReader( xmlString ) ) );

			TransformerFactory tranFactory = TransformerFactory.newInstance();
			Transformer aTransformer = tranFactory.newTransformer();
			Source src = new DOMSource( document );
			Result dest = new StreamResult( new File("E:\\temp\\"+fileName+".xml") );
			aTransformer.transform( src, dest );
	}


}
