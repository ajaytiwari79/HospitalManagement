package com.kairos.user.staff;

import javax.validation.constraints.NotNull;
import java.util.List;

public class ContactPersonDTO {

    @NotNull(message = "Serviceid can't be null")
    private Long serviceTypeId;
    private Long  primaryStaffId;
    private Long secondaryStaffId1;
    private Long  secondaryStaffId2;
    private Long secondaryStaffId3;
    private List<Long> houseHoldMembers;

    public ContactPersonDTO(){//default constructor
        }

    public ContactPersonDTO(Long serviceTypeId, Long primaryStaffId, List<Long> houseHoldMembers) {
        this.serviceTypeId = serviceTypeId;
        this.primaryStaffId = primaryStaffId;
        this.houseHoldMembers = houseHoldMembers;
    }

    public Long getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(Long serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public Long getPrimaryStaffId() {
        return primaryStaffId;
    }

    public void setPrimaryStaffId(Long primaryStaffId) {
        this.primaryStaffId = primaryStaffId;
    }

    public Long getSecondaryStaffId1() {
        return secondaryStaffId1;
    }

    public void setSecondaryStaffId1(Long secondaryStaffId1) {
        this.secondaryStaffId1 = secondaryStaffId1;
    }

    public Long getSecondaryStaffId2() {
        return secondaryStaffId2;
    }

    public void setSecondaryStaffId2(Long secondaryStaffId2) {
        this.secondaryStaffId2 = secondaryStaffId2;
    }

    public Long getSecondaryStaffId3() {
        return secondaryStaffId3;
    }

    public void setSecondaryStaffId3(Long secondaryStaffId3) {
        this.secondaryStaffId3 = secondaryStaffId3;
    }

    public List<Long> getHouseHoldMembers() {
        return houseHoldMembers;
    }

    public void setHouseHoldMembers(List<Long> houseHoldMembers) {
        this.houseHoldMembers = houseHoldMembers;
    }
}