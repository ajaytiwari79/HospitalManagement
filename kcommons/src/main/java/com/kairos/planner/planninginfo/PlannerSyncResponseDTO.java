package com.kairos.planner.planninginfo;

public class PlannerSyncResponseDTO {
    public boolean isSyncStarted() {
        return syncStarted;
    }

    public void setSyncStarted(boolean syncStarted) {
        this.syncStarted = syncStarted;
    }

    boolean syncStarted;

    public PlannerSyncResponseDTO(boolean syncStarted) {
        this.syncStarted = syncStarted;
    }

    public PlannerSyncResponseDTO() {
    }
}
