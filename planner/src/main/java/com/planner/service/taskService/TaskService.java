package com.planner.service.taskService;

import com.kairos.commons.utils.ObjectUtils;
import com.planner.domain.task.Task;
import com.planner.repository.taskRepository.TaskRepository;
import com.planner.service.locationService.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

/**
 * @author pradeep
 * @date - 7/6/18
 */
@Service
public class TaskService {


    @Autowired private TaskRepository taskRepository;
    @Autowired private LocationService locationService;

    public void saveTasks(List<Task> taskList){
        taskRepository.saveAll(taskList);
    }

    public List<com.kairos.planner.vrp.taskplanning.model.Task> getUniqueTask(){
        List<Task> taskList = taskRepository.findAll();
        List<Task> uniqueTaskList = taskList.stream().filter(ObjectUtils.distinctByKey(task -> task.getInstallationNumber())).collect(toList());
        List<com.kairos.planner.vrp.taskplanning.model.Task> tasks = new ArrayList<>();
        Map<Long,Integer> intallationandDuration = taskList.stream().collect(groupingBy(Task::getInstallationNumber,summingInt(t->(int)t.getDuration())));
        Map<Long,Set<String>> intallationandSkill = taskList.stream().collect(groupingBy(Task::getInstallationNumber,mapping(Task::getSkill,toSet())));
        Map<Long,Task> taskMap= uniqueTaskList.stream().collect(Collectors.toMap(t->t.getInstallationNumber(), t->t));
        intallationandDuration.entrySet().forEach(t->{
            Task task = taskMap.get(t.getKey());
            tasks.add(new com.kairos.planner.vrp.taskplanning.model.Task(task.getId().toString(),task.getInstallationNumber(),task.getLatitude(),task.getLongitude(),intallationandSkill.get(task.getInstallationNumber()),t.getValue(),task.getStreetName(),task.getHouseNo(),task.getBlock(),task.getFloorNo(),task.getPost(),task.getCity(),false));

        });
        return tasks;
    }



}
