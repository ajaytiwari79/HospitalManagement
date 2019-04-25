package com.kairos.planning.executioner;

import com.kairos.planning.domain.*;
import com.kairos.planning.solution.TaskPlanningSolution;
import com.kairos.planning.utils.JodaTimeConverter;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;

import java.util.ArrayList;
import java.util.List;

public class TaskPlanningGenerator {
    private static final String DATA_UNPLANNED_TASK3_XML = "data/unplannedTask3.xml";
    private static final String DATA_PLANNED_TASKS = "data/plannedTasks.xml";
    private static final String FILE_TO_USE=DATA_UNPLANNED_TASK3_XML;


	public TaskPlanningSolution loadUnsolvedSolution() {
        TaskPlanningSolution unresolvedSolution = new TaskPlanningSolution();
        unresolvedSolution.setTaskList(generateTasks());
        unresolvedSolution.setVehicleList(generateVehicleList());
        //unresolvedSolution.setCitizenList(generateCitizenList());
        unresolvedSolution.setLocationList(generateLocationList());
        return unresolvedSolution;
    }

    public TaskPlanningSolution loadUnsolvedSolutionFromXML() {
        //XStream xstream = new XStream();
    	XStream xstream = new XStream(new PureJavaReflectionProvider());


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
        xstream.setMode(XStream.ID_REFERENCES);
        //xstream.setMode(XStream.XPATH_RELATIVE_REFERENCES);
        //2017-08-02 11:45:00.0 UTC
        xstream.registerConverter(new JodaTimeConverter());
       // xstream.registerConverter(new ISO8601GregorianCalendarConverter());

        // xstream.registerConverter(new DateConverter("yyyy-MM-dd HH:mm:ss.S z", new String[]{"yyyy-MM-dd HH:mm:ss.S z"}));
        TaskPlanningSolution unresolvedSolution;
        try {
            //unresolvedSolution = (TaskPlanningSolution) xstream.fromXML("");//unplannedTask.xml
            unresolvedSolution = (TaskPlanningSolution) xstream.fromXML(this.getClass().getClassLoader().getResourceAsStream(FILE_TO_USE));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return unresolvedSolution;
    }


    private List<Vehicle> generateVehicleList() {
        List<Vehicle> vehicleList = new ArrayList<Vehicle>();
        for (int i = 0; i < 2; i++) {
            Vehicle vehicle = new Vehicle();
            vehicle.setId(200000l + i);
            vehicle.setLocation(generateLocationList().get(0));
            vehicle.setType((i * 10) % 2 == 0 ? "car" : "bike");
            vehicleList.add(vehicle);
        }
        return vehicleList;
    }

    private List<Task> generateTasks() {
        List<Task> taskList = new ArrayList<Task>();
        List<Citizen> citizens =null;// generateCitizenList();
        List<Location> locations = generateLocationList();
        for (int i = 0; i < 10; i++) {
            Task task = null;//new Task();
            task.setId(Long.toString(100000l + i));
            //task.setPriority(new Long(i));
            //task.setDuration(10L*i);
            task.setCitizen(citizens.get(i));
            task.setLocation(locations.get(i));
            taskList.add(task);
        }
        return taskList;
    }

    private List<Location> generateLocationList() {
        List<Location> locations = new ArrayList<Location>();
        /*locations.add(new Location(1l,"Oodles1", 11.21300, 11.43344));
        locations.add(new Location("Oodles2", 21.34300, 21.54334));
        locations.add(new Location("Oodles3", 31.45600, 31.32434));
        locations.add(new Location("Oodles4", 41.23234, 41.45634));
        locations.add(new Location("Oodles5", 51.32300, 51.55434));
        locations.add(new Location("Oodles6", 61.23234, 61.54534));*/
        return locations;
    }


    private List<Employee> generateEmployeeList() {
        List<Employee> employees = new ArrayList<Employee>();
        //employees.add(new Employee(101l,"John Doe",new ArrayList<Skill>()));
        //employees.add(new Employee(102l,"Jane Doe",new ArrayList<Skill>()));
        //employees.add(new Employee(103l,"Jean Doe",new ArrayList<Skill>()));
        return employees;
    }

}

