package com.kairos.activity.util;

import com.kairos.activity.client.dto.TimeSlot;
import com.kairos.activity.client.dto.client.Client;
import com.kairos.activity.persistence.model.task_demand.TaskDemand;
import com.kairos.activity.persistence.model.task_type.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by prabjot on 4/11/16.
 */
public class TaskDemandValidator {

    private final static Logger LOGGER = LoggerFactory.getLogger(TaskDemandValidator.class);

    private static TaskDemandValidator taskDemandValidator;

    public static TaskDemandValidator getInstance(){
        if(taskDemandValidator == null){
            taskDemandValidator = new TaskDemandValidator();
        }
        return taskDemandValidator;
    }



    private TaskDemandValidator() {
    }

    private boolean validateCountry(TaskDemand taskDemand, Client client, TaskType taskType){
       /* List<Long> clientCountries = new ArrayList<>();
        for(Country country : client.getCountryList()){
            clientCountries.add(country.getId());
        }

        boolean status = false;

        if(clientCountries.contains(taskType.getCountryId())){
            status = true;
        }
        return status;*/
       return false;
    }




    public boolean validate(TaskDemand taskDemand,Client client,TaskType taskType,TimeSlot timeSlot){

return false;
    }

}
