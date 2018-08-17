package com.kairos.persistence.repository.user.expertise;

import com.kairos.persistence.model.user.expertise.FunctionalPaymentMatrix;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FunctionalPaymentMatrixRepository  extends Neo4jBaseRepository<FunctionalPaymentMatrix, Long> {

}
