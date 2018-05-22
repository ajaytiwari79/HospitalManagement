package com.kairos.response.dto.web.staff;

import java.util.List;

public class EligibleNightWorkerStaffIdListDTO {

    private List<Long> eligibleNightWorkers;
    private List<Long> nonEligibleNightWorkerStaff;

    public EligibleNightWorkerStaffIdListDTO(){
        // default constructor
    }

    public List<Long> getEligibleNightWorkers() {
        return eligibleNightWorkers;
    }

    public void setEligibleNightWorkers(List<Long> eligibleNightWorkers) {
        this.eligibleNightWorkers = eligibleNightWorkers;
    }

    public List<Long> getNonEligibleNightWorkerStaff() {
        return nonEligibleNightWorkerStaff;
    }

    public void setNonEligibleNightWorkerStaff(List<Long> nonEligibleNightWorkerStaff) {
        this.nonEligibleNightWorkerStaff = nonEligibleNightWorkerStaff;
    }
}
