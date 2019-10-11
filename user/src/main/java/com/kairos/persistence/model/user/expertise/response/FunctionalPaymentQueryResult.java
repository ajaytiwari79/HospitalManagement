package com.kairos.persistence.model.user.expertise.response;
/*
 *Created By Pavan on 22/11/18
 *
 */

import com.kairos.enums.shift.PaidOutFrequencyEnum;
import com.kairos.persistence.model.user.expertise.Expertise;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.LocalDate;
import java.util.List;

@QueryResult
@Getter
@Setter
public class FunctionalPaymentQueryResult {
   private Long id;
   private LocalDate startDate;
   private LocalDate endDate;
   private List<FunctionalPaymentMatrixQueryResult> functionalPaymentMatrices;
   private Expertise expertise;
   private PaidOutFrequencyEnum paymentUnit;
   private boolean published;
}
