package com.kairos.dto.user.employment;

public class EmploymentIdDTO {

    private Long oldEmploymentId;
    private Long newEmploymentId;
    private Long employmentLineId;

    public EmploymentIdDTO() {

    }

    public EmploymentIdDTO(Long oldEmploymentId, Long newEmploymentId, Long employmentLineId) {
        this.oldEmploymentId = oldEmploymentId;
        this.newEmploymentId = newEmploymentId;
        this.employmentLineId = employmentLineId;
    }

    public Long getOldEmploymentId() {
        return oldEmploymentId;
    }

    public void setOldEmploymentId(Long oldEmploymentId) {
        this.oldEmploymentId = oldEmploymentId;
    }

    public Long getNewEmploymentId() {
        return newEmploymentId;
    }

    public void setNewEmploymentId(Long newEmploymentId) {
        this.newEmploymentId = newEmploymentId;
    }

    public Long getEmploymentLineId() {
        return employmentLineId;
    }

    public void setEmploymentLineId(Long employmentLineId) {
        this.employmentLineId = employmentLineId;
    }
}
