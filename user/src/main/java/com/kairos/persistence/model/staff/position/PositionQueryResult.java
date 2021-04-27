package com.kairos.persistence.model.staff.position;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.math.BigInteger;

/**
 * Created by yatharth on 13/4/18.
 */

@QueryResult
@Getter
@Setter
public class PositionQueryResult {

    private Long id;
    private String name;
    private Long startDateMillis;
    private Long endDateMillis;
    private BigInteger reasonCodeId;
    private Long accessGroupIdOnPositionEnd;

    public PositionQueryResult(Long id, Long startDateMillis, Long endDateMillis) {
        this.id = id;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;

    }
    public PositionQueryResult(Long id, Long startDateMillis, Long endDateMillis , BigInteger reasonCodeId, Long accessGroupIdOnPositionEnd) {
        this.id = id;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
        this.accessGroupIdOnPositionEnd = accessGroupIdOnPositionEnd;
        this.reasonCodeId = reasonCodeId;
    }
}
