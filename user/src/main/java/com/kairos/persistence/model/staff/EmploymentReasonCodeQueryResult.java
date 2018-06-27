package com.kairos.persistence.model.staff;

import com.kairos.persistence.model.country.ReasonCode;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by yatharth on 3/5/18.
 */
@QueryResult
public class EmploymentReasonCodeQueryResult {
    private Employment employment;
    private ReasonCode reasonCode;

    public Employment getEmployment() {
        return employment;
    }

    public void setEmployment(Employment employment) {
        this.employment = employment;
    }

    public ReasonCode getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(ReasonCode reasonCode) {
        this.reasonCode = reasonCode;
    }
}
