package com.kairos.persistence.repository.user.pay_table;

import com.kairos.persistence.model.pay_table.PayGradePayGroupAreaRelationShip;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by vipul on 16/3/18.
 */
@Repository
public interface PayTableRelationShipGraphRepository extends Neo4jBaseRepository<PayGradePayGroupAreaRelationShip, Long> {
}
