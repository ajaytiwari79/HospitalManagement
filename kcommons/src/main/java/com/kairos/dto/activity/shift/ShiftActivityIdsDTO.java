package com.kairos.dto.activity.shift;
/*
 *Created By Pavan on 8/10/18
 *
 */

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

public class ShiftActivityIdsDTO {
    private Set<BigInteger> activitiesToAdd=new HashSet();
    private Set<BigInteger> activitiesToEdit=new HashSet();
    private Set<BigInteger> activitiesToDelete=new HashSet();

    public ShiftActivityIdsDTO() {
        //Default Constructor
    }

    public ShiftActivityIdsDTO(Set<BigInteger> activitiesToAdd, Set<BigInteger> activitiesToEdit, Set<BigInteger> activitiesToDelete) {
        this.activitiesToAdd = activitiesToAdd;
        this.activitiesToEdit = activitiesToEdit;
        this.activitiesToDelete = activitiesToDelete;
    }

    public Set<BigInteger> getActivitiesToAdd() {
        return activitiesToAdd;
    }

    public void setActivitiesToAdd(Set<BigInteger> activitiesToAdd) {
        this.activitiesToAdd = activitiesToAdd;
    }

    public Set<BigInteger> getActivitiesToEdit() {
        return activitiesToEdit;
    }

    public void setActivitiesToEdit(Set<BigInteger> activitiesToEdit) {
        this.activitiesToEdit = activitiesToEdit;
    }

    public Set<BigInteger> getActivitiesToDelete() {
        return activitiesToDelete;
    }

    public void setActivitiesToDelete(Set<BigInteger> activitiesToDelete) {
        this.activitiesToDelete = activitiesToDelete;
    }

    public Set<BigInteger> getAllActivities(){
        Set<BigInteger> allActivityId=new HashSet<>();
        allActivityId.addAll(this.activitiesToAdd);
        allActivityId.addAll(this.activitiesToDelete);
        allActivityId.addAll(this.activitiesToEdit);
        return allActivityId;
    }
}
