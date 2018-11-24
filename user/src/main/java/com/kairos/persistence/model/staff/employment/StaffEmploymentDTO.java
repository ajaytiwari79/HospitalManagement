package com.kairos.persistence.model.staff.employment;

import com.kairos.persistence.model.staff.personal_details.Staff;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.LocalDate;

/**
 * Created by yatharth on 13/4/18.
 */
@QueryResult
public class StaffEmploymentDTO {

   private Staff staff;
   private LocalDate employmentStartDate;

    public StaffEmploymentDTO() {

    }

    public StaffEmploymentDTO(Staff staff, LocalDate startDateMillis ) {
        this.staff = staff;
        this.employmentStartDate = startDateMillis;
    }
    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public LocalDate getEmploymentStartDate() {
        return employmentStartDate;
    }

    public void setEmploymentStartDate(LocalDate employmentStartDate) {
        this.employmentStartDate = employmentStartDate;
    }


}
