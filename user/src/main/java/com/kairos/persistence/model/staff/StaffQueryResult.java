package com.kairos.persistence.model.staff;

import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by prabjot on 5/10/17.
 */
@QueryResult
public class StaffQueryResult {
    private Staff staff;
    private Long contactAddressId;
    private Long contactDetailId;

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public Long getContactAddressId() {
        return contactAddressId;
    }

    public void setContactAddressId(Long contactAddressId) {
        this.contactAddressId = contactAddressId;
    }

    public Long getContactDetailId() {
        return contactDetailId;
    }

    public void setContactDetailId(Long contactDetailId) {
        this.contactDetailId = contactDetailId;
    }
}
