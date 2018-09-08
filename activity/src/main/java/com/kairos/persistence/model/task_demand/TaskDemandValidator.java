package com.kairos.persistence.model.task_demand;
import com.kairos.dto.user.client.Client;
import com.kairos.persistence.model.task_type.TaskType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by prabjot on 4/11/16.
 */
public class TaskDemandValidator {

    private TaskDemandValidator() {
    }

    private static Map<String,Object> validateCountry(long citizenCountryId,long taskTypeCountryId){
        Map<String,Object> response = new HashMap(2);
        if(citizenCountryId == taskTypeCountryId){
            response.put("status",true);
            return response;
        }
        response.put("status",false);
        response.put("message","task demand can not generate for this country");
        return response;
    }

    private static Map<String,Object> validateGender(Client client, TaskType taskType){

        Map<String,Object> response = new HashMap(2);


        return response;
    }

    public Map<String,Object> validate(TaskDemand taskDemand){

        return null;
    }

}
