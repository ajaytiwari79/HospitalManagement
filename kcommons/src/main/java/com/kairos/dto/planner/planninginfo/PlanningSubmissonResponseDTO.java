package com.kairos.dto.planner.planninginfo;

public class PlanningSubmissonResponseDTO {
    boolean syncStarted;

    public PlanningSubmissonResponseDTO(boolean syncStarted) {
        this.syncStarted = syncStarted;
    }

    public PlanningSubmissonResponseDTO() {
    }

    public boolean isSyncStarted() {
        return syncStarted;
    }

    public void setSyncStarted(boolean syncStarted) {
        this.syncStarted = syncStarted;
    }
}
