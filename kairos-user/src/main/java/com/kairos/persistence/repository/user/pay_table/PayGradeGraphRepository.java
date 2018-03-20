package com.kairos.persistence.repository.user.pay_table;

import com.kairos.persistence.model.user.pay_table.PayGrade;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by vipul on 20/3/18.
 */
@Repository
public interface PayGradeGraphRepository extends Neo4jBaseRepository<PayGrade, Long> {
    @Query("MATCH (payGrade:PayGrade) where id(payGrade)={0}\n" +
            "OPTIONAL match(payGrade)-[rel:HAS_PAY_GROUP_AREA]-(pga:PayGroupArea)\n" +
            "detach delete rel\n" +
            "set payGrade.deleted=true")
    void removeAllPayGroupAreasFromPayGrade(Long payGradeId);
}
