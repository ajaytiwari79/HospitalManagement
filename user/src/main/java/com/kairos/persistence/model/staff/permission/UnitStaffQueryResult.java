package com.kairos.persistence.model.staff.permission;

import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetailQueryResult;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.ArrayList;
import java.util.List;

@QueryResult
@Getter
@Setter
public class UnitStaffQueryResult {

    private Long unitId;
    private List<StaffPersonalDetailQueryResult> staffList = new ArrayList<>();
}
