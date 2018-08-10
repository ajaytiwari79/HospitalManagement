package com.kairos.persistence.repository.user.pay_table;

import com.kairos.persistence.model.pay_table.PayGrade;
import com.kairos.persistence.model.pay_table.PayTableMatrixQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_PAY_GROUP_AREA;

/**
 * Created by vipul on 20/3/18.
 */
@Repository
public interface PayGradeGraphRepository extends Neo4jBaseRepository<PayGrade, Long> {
    @Query("MATCH (payGrade:PayGrade) where id(payGrade)={0}\n" +
            "OPTIONAL match(payGrade)-[rel:" + HAS_PAY_GROUP_AREA + "]-(pga:PayGroupArea)\n" +
            "detach delete rel\n" +
            "set payGrade.deleted=true")
    void removeAllPayGroupAreasFromPayGradeAndDeletePayGrade(Long payGradeId);

    @Query("Match(payGrade)-[rel:" + HAS_PAY_GROUP_AREA + "]-(pga:PayGroupArea{deleted:false}) where id(payGrade)={0} \n" +
            "return id(pga) as payGroupAreaId,pga.name as payGroupAreaName,rel.payGroupAreaAmount as payGroupAreaAmount  ORDER BY  rel.state")
    HashSet<PayTableMatrixQueryResult> getPayGradeMatrixByPayGradeId(Long PayGradeId);

    @Query("MATCH (payGrade:PayGrade) where id(payGrade)={0}\n" +
            "OPTIONAL match(payGrade)-[rel:" + HAS_PAY_GROUP_AREA + "]-(pga:PayGroupArea)\n" +
            "detach delete rel")
    void removeAllPayGroupAreasFromPayGrade(Long payGradeId);

    @Query("MATCH (payGrade:PayGrade{deleted:false}) where id(payGrade) IN {0}\n" +
            "return payGrade")
    List<PayGrade> getAllPayGradesById(Set<Long> payGradeIds);
}
