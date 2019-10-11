package com.kairos.persistence.model.user.expertise.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.math.BigDecimal;

@QueryResult
@Getter
@Setter
public class FunctionQR {
    private Long functionId;
    private Long functionName; // this is for FE compatibility
    private BigDecimal amount;  // this  is added to the function
    private boolean amountEditableAtUnit;
}
