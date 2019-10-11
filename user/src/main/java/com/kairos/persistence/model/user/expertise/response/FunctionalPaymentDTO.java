package com.kairos.persistence.model.user.expertise.response;


import com.kairos.enums.shift.PaidOutFrequencyEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDate;

@QueryResult
@Getter
@Setter
public class FunctionalPaymentDTO {
    @Min(0)
    private Long expertiseId;
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private PaidOutFrequencyEnum paymentUnit;
    private boolean published;
    private BigDecimal percentageValue;
    private boolean oneTimeUpdatedAfterPublish;
}
