package com.kairos.planning.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;

@PlanningEntity
public abstract class TaskOrEmployee {

    public void setNextTask(Task nextTask) {
        this.nextTask = nextTask;
    }

    // Shadow variables
    @InverseRelationShadowVariable(sourceVariableName = "previousTaskOrEmployee")
    protected Task nextTask;
    
  //not for any calc
  	public Task getNextTask(){
  		return nextTask;
  	}

    
    public abstract Vehicle getVehicle();
    
    public abstract Location getLocation();
    public abstract String getId();
}
