package com.kairos.persistence.model.staff.position;

import com.kairos.persistence.model.staff.personal_details.Staff;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by yatharth on 13/4/18.
 */
@QueryResult
public class StaffPositionDTO {

   private Staff staff;
   private Long positionStartDate;

    public StaffPositionDTO() {

    }

    public StaffPositionDTO(Staff staff, Long startDateMillis ) {
        this.staff = staff;
        this.positionStartDate = startDateMillis;
    }
    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public Long getPositionStartDate() {
        return positionStartDate;
    }

    public void setPositionStartDate(Long positionStartDate) {
        this.positionStartDate = positionStartDate;
    }


}
