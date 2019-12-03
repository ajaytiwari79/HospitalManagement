package com.kairos.persistence.model.staff;

import com.kairos.persistence.model.staff.personal_details.Staff;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;
import java.util.Map;

/**
 * Created by prabjot on 5/10/17.
 */
@QueryResult
@Getter
@Setter
public class StaffQueryResult {
    private Staff staff;
    private Long contactAddressId;
    private Long contactDetailId;
    private List<Map<String,Object>> skillInfo;
}
