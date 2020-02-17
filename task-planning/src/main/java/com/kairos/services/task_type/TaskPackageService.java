package com.kairos.services.task_type;
import com.kairos.persistence.model.task.TaskPackage;
import com.kairos.persistence.model.task_demand.TaskDemand;
import com.kairos.repositories.task_type.TaskDemandMongoRepository;
import com.kairos.repositories.task_type.TaskPackageMongoRepository;
import com.kairos.service.MongoBaseService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by prabjot on 16/11/16.
 */
@Service
public class TaskPackageService extends MongoBaseService {

    @Inject
    TaskPackageMongoRepository taskPackageMongoRepository;
    @Inject
    TaskDemandMongoRepository taskDemandMongoRepository;

    @Inject
    TaskDemandService taskDemandService;

    public TaskPackage createTaskPackage(TaskPackage taskPackage) {

        save(taskPackage);
        return taskPackage;
    }

    public List<TaskPackage> getTaskPackages(long unitId) {

        return taskPackageMongoRepository.findAllByUnitIdAndIsDeleted(unitId,false);
    }

    public List<TaskDemand> createTaskDemandByPackage(String taskPackageId, long clientId, long staffId) {

        TaskPackage taskPackage = taskPackageMongoRepository.findOne(new BigInteger(taskPackageId));
        if (taskPackage == null) {
            return null;
        }

        List<String> taskDemandIds = new ArrayList<>(taskPackage.getTaskDemandIds().size());

        /*List<String> taskDemandIds = new ArrayList<>(taskPackage.getTaskTypeDetail().size());
        for (Map<String, Object> map : taskPackage.getTaskTypeDetail()) {
            taskDemandIds.add((String) map.get("taskDemandId"));
        }*/
        List<TaskDemand> taskDemands = taskDemandMongoRepository.findByIdIn(taskDemandIds);

        List<TaskDemand> copyTaskDemandObj = new ArrayList<>();
        for (TaskDemand taskDemand : taskDemands) {
            TaskDemand copyObj = new TaskDemand();
            copyObj.setCitizenId(clientId);
//            copyObj.setStaff(staffId);
            copyTaskDemandObj.add(copyObj);
        }
        taskDemandService.save(copyTaskDemandObj);
        return copyTaskDemandObj;
    }

    public boolean deleteTaskPackage(String taskPackageId){
        TaskPackage taskPackage = taskPackageMongoRepository.findOne(new BigInteger(taskPackageId));
        if (taskPackage == null) {
            return false;
        }
        taskPackage.setDeleted(true);
        save(taskPackage);
        return true;
    }

}
