package com.planner.domain.staff;

import com.planner.domain.common.BaseEntity;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("unit_staff")
public class UnitStaffRelationShip extends BaseEntity {

    private String staffId;
    private String workingTimeAgreementId;
    private String costTimeAgreementId;

    public String getWorkingTimeAgreementId() {
        return workingTimeAgreementId;
    }

    public void setWorkingTimeAgreementId(String workingTimeAgreementId) {
        this.workingTimeAgreementId = workingTimeAgreementId;
    }

    public String getCostTimeAgreementId() {
        return costTimeAgreementId;
    }

    public void setCostTimeAgreementId(String costTimeAgreementId) {
        this.costTimeAgreementId = costTimeAgreementId;
    }

    public String getStaffId() {
        return staffId;
    }

    public UnitStaffRelationShip(String staffId,Long unitId){
        this.staffId = staffId;
        setUnitId(unitId);
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }
}
