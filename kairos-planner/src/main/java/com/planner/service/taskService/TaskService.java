package com.planner.service.taskService;

import com.planner.domain.task.Task;
import com.planner.repository.taskRepository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

/**
 * @author pradeep
 * @date - 7/6/18
 */
@Service
public class TaskService {


    @Autowired private TaskRepository taskRepository;

    public void saveTasks(List<Task> taskList){
        taskRepository.saveAll(taskList);
    }

    public List<com.kairos.planner.vrp.taskplanning.model.Task> getUniqueTask(){
        List<Task> taskList = taskRepository.findAll();
        List<com.kairos.planner.vrp.taskplanning.model.Task> tasks = new ArrayList<>();
        Map<Integer,Integer> intallationandDuration = taskList.stream().collect(groupingBy(Task::getIntallationNo,summingInt(Task::getDuration)));
        Map<Integer,Set<String>> intallationandSkill = taskList.stream().collect(groupingBy(Task::getIntallationNo,mapping(Task::getSkill,toSet())));
        Map<Integer,Task> taskMap= taskList.stream().collect(Collectors.toMap(t->t.getIntallationNo(),t->t));
        intallationandDuration.entrySet().forEach(t->{
            Task task = taskMap.get(t.getKey());
            tasks.add(new com.kairos.planner.vrp.taskplanning.model.Task(task.getId(),task.getIntallationNo(),task.getLattitude(),task.getLongitude(),intallationandSkill.get(task.getIntallationNo()),t.getValue(),task.getStreetName(),task.getHouseNo(),task.getBlock(),task.getFloorNo(),task.getPost(),task.getCity()));
        });
        return tasks;
    }

}
