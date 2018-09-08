package com.kairos.user.staff;

public class ClientStaffInfoDTO {
    private Long clientId;
    private Long staffId;
    public ClientStaffInfoDTO(){
        //default constructor
    }

    public ClientStaffInfoDTO( Long staffId){
        this.staffId=staffId;
    }
    public ClientStaffInfoDTO(Long clientId, Long staffId){
        this.clientId=clientId;
        this.staffId=staffId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }
}
