package com.kairos.persistence.model.staff.position;

import com.kairos.persistence.model.country.reason_code.ReasonCode;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by yatharth on 3/5/18.
 */
@QueryResult
public class PositionReasonCodeQueryResult {
    private Position position;
    private ReasonCode reasonCode;

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public ReasonCode getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(ReasonCode reasonCode) {
        this.reasonCode = reasonCode;
    }
}
