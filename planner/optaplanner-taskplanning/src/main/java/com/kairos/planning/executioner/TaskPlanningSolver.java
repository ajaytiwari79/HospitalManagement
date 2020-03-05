package com.kairos.planning.executioner;

import com.kairos.planning.domain.Employee;
import com.kairos.planning.domain.Task;
import com.kairos.planning.domain.TaskMoveCHFilter;
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
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirector;
import org.optaplanner.persistence.xstream.api.score.buildin.bendablelong.BendableLongScoreXStreamConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TaskPlanningSolver {


	public static final String ERROR = "error {}";
	public static final String SOLVER_XML = "com/kairos/planning/configuration/OutdoorTaskPlanning.solver.xml";
    public static final String BENCHMARK_SOLVER_XML = "com/kairos/planning/configuration/OutdoorTaskPlanningBenchmark.solver.xml";

    private static Logger log= LoggerFactory.getLogger(TaskPlanningSolver.class);
	Solver<TaskPlanningSolution> solver;
	DroolsScoreDirector<TaskPlanningSolution> director;
	
	SolverFactory<TaskPlanningSolution> solverFactory;
	static{
        System.setProperty("user.timezone", "UTC");
    }
	public TaskPlanningSolver(){
		solverFactory = SolverFactory.createFromXmlResource(SOLVER_XML);
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
			log.info(ERROR,e.getMessage());
			return null;
		}
	}

	private void printSolvedSolution(Object[] solution) {
		log.info("-------Printing solution:-------");
		log.info("{}", toDisplayString(solution));
		log.info("-------Printing solution Finished:-------");
	}

	public Object[] getSolution(){
		
		TaskPlanningSolution unsolvedSolution=getUnsolvedSolution();
		log.info("Number of locations:"+unsolvedSolution.getLocationList().size());
		log.info("Number of Tasks:"+unsolvedSolution.getTaskList().size());
		log.info("Number of Vehicless:"+unsolvedSolution.getVehicleList().size());
		log.info("Number of Employees:"+unsolvedSolution.getEmployeeList().size());
		log.info("Number of Available Employees:"+unsolvedSolution.getAvailabilityList().size());
		try {
			toXml(unsolvedSolution,  "problem");
		}catch(Exception e){
			log.info(ERROR,e.getMessage());
		}
		long start= System.currentTimeMillis();
		solver.solve(unsolvedSolution);
		log.info("final generated availability requests:");
		TaskPlanningUtility.updatedList.forEach(req->
			log.info(req.toString())
		);
		TaskPlanningSolution solution = solver.getBestSolution();
		log.info("Solver took secs: {}",(System.currentTimeMillis()-start)/1000);
		DroolsScoreDirector<TaskPlanningSolution> taskPlanningSolutionDroolsScoreDirector=(DroolsScoreDirector<TaskPlanningSolution>)solver.getScoreDirectorFactory().buildScoreDirector();

		taskPlanningSolutionDroolsScoreDirector.setWorkingSolution(solution);
		Map<Task,Indictment> indictmentMap=(Map)taskPlanningSolutionDroolsScoreDirector.getIndictmentMap();
		return new Object[]{solution,indictmentMap,taskPlanningSolutionDroolsScoreDirector.getConstraintMatchTotals()};
	}

	public void benchmarkForSolution() {
        TaskPlanningSolution unsolvedSolution=getUnsolvedSolution();
		PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.createFromXmlResource(BENCHMARK_SOLVER_XML);
		PlannerBenchmark plannerBenchmark = benchmarkFactory.buildPlannerBenchmark(unsolvedSolution);
		plannerBenchmark.benchmark();
	}

	private long sumHardScore(BendableLongScore score){
		return Arrays.stream(score.getHardScores()).count();
	}

	private TaskPlanningSolution getUnsolvedSolution() {
		TaskPlanningSolution unsolvedSolution=new TaskPlanningGenerator().loadUnsolvedSolutionFromXML();
        GraphHopper graphHopper = new GraphHopper();
        graphHopper.getLocationData(unsolvedSolution.getLocationList());
        unsolvedSolution.getEmployeeList().forEach(emp->
            emp.setAvialableMinutes( unsolvedSolution.getAvailabilityList().stream().filter(ar->ar.getEmployee().getId().equals(emp.getId())).mapToLong(ar->ar.getMinutes()).sum())
        );
        return unsolvedSolution;
	}

	public String toDisplayString(Object[] array) {

		TaskPlanningSolution solution=(TaskPlanningSolution)array[0];
		log.info("---Task CH Filter attemts::---"+TaskMoveCHFilter.attemts);
		checkForLogicalFact(solution);
		Map<Task,Indictment> indictmentMap =(Map<Task,Indictment>) array[1];
        Collection<ConstraintMatchTotal> constraintMatchTotals= (Collection<ConstraintMatchTotal>) array[2];
        constraintMatchTotals.forEach(constraintMatchTotal -> {
            log.info(" {} : Total: {} == Reason(entities):",constraintMatchTotal.getConstraintName(),constraintMatchTotal.toString());
            constraintMatchTotal.getConstraintMatchSet().forEach(constraintMatch ->
                constraintMatch.getJustificationList().forEach(o ->
                    log.info("--- {}",o)
                )
            );

        });
        StringBuilder displayString = new StringBuilder();
        displayString.append("\nTask assignment:");
        Map<String,Long> empMins= new HashMap<>();
        solution.getTaskList().forEach(task->
			task.setBrokenHardConstraints(ArrayUtils.toObject(((BendableLongScore) indictmentMap.get(task).getScoreTotal()).getHardScores()))
		);
		try {
			toXml(solution,"solution");
		}catch(Exception e){
			log.info(ERROR,e.getMessage());
		}

		log.info("---emp mins:--- {} ",empMins);
		StringBuilder employeeRoute= new StringBuilder();
		employeeRoute.append("\nEmployee assignment:\n");
        solution.getEmployeeList().forEach(employee->{
        	if(employee.getNextTask()==null) return;
			employeeRoute.append("\nEmployee :"+employee+", Vehicle:"+employee.getVehicle()+", Interval:"+employee.getWorkIntervalAsString()+""
					+(indictmentMap.get(employee)==null?"":(Arrays.toString(((BendableLongScore) indictmentMap.get(employee).getScoreTotal()).getHardScores())))
					+"\n");
        	Task nextTask=employee.getNextTask();
			while(nextTask!=null){
				employeeRoute.append(nextTask.getLabel());
				if(indictmentMap.containsKey(nextTask) && sumHardScore((BendableLongScore) indictmentMap.get(nextTask).getScoreTotal())>0){
					employeeRoute.append(""+Arrays.toString(((BendableLongScore) indictmentMap.get(nextTask).getScoreTotal()).getHardScores())+"");
				}
				employeeRoute.append("->");
        		nextTask=nextTask.getNextTask();
        	}
			employeeRoute.append("\n");
        });
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
		log.info("Tasks found: {}",taskCounter);
	}

	private void toXml(TaskPlanningSolution solution, String fileName){
		try {
		    if(fileName.equals("solution"))
			    checkForCyclicProblems(solution);
			XStream xstream = new XStream();
           xstream.setMode(XStream.ID_REFERENCES);
			xstream.registerConverter(new JodaTimeConverter());
			xstream.registerConverter(new BendableLongScoreXStreamConverter());
			String xmlString = xstream.toXML(solution);
			writeXml(xmlString, fileName);
		}catch(Exception e){
			log.info(ERROR,e.getMessage());
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

	public void writeXml(String xmlString,String fileName){
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();

		Document document = builder.parse( new InputSource(
					new StringReader( xmlString ) ) );

			TransformerFactory tranFactory = TransformerFactory.newInstance();
			Transformer aTransformer = tranFactory.newTransformer();
			Source src = new DOMSource( document );
			Result dest = new StreamResult( new File("E:\\temp\\"+fileName+".xml") );
			aTransformer.transform( src, dest );
		} catch (ParserConfigurationException | TransformerException | SAXException | IOException e) {
			log.info(ERROR,e.getMessage());
		}
	}


}
