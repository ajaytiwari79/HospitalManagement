package com.kairos.persistence.model.user.expertise.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
@Getter
@Setter
public class SeniorityLevelFunctionQR {
    private Long seniorityLevelId;
    private Integer from; // added these 2 fields just FE needs them
    private Integer to;
    private List<FunctionQR> functions;
}
