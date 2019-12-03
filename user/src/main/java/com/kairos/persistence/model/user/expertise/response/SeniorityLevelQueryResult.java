package com.kairos.persistence.model.user.expertise.response;

import com.kairos.persistence.model.country.functions.FunctionDTO;
import com.kairos.persistence.model.pay_table.PayGrade;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by vipul on 29/3/18.
 */
@QueryResult
@Getter
@Setter
public class SeniorityLevelQueryResult {
    private PayGrade payGrade;
    private BigDecimal pensionPercentage;
    private BigDecimal freeChoicePercentage;
    private BigDecimal freeChoiceToPension;
    private Integer from;
    private Integer to;
    private Long id;
    private List<FunctionDTO> functions;
}
