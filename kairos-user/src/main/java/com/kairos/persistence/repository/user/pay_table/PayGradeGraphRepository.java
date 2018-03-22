package com.kairos.persistence.repository.user.pay_table;

import com.kairos.persistence.model.user.pay_table.PayGrade;
import com.kairos.persistence.model.user.pay_table.PayTableMatrixQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.HashSet;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_PAY_GROUP_AREA;

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

    @Query("Match(payGrade)-[rel:" + HAS_PAY_GROUP_AREA + "]-(pga:PayGroupArea{deleted:false}) where id(payGrade)={0} \n" +
            "return id(pga) as payGroupAreaId,pga.name as payGroupAreaName,rel.state as state,rel.payGroupAreaAmount as payGroupAreaAmount  ORDER BY  rel.state")
    HashSet<PayTableMatrixQueryResult> getPayGradeMatrixByPayGradeId(Long PayGradeId);

}
