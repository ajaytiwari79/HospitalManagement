package com.kairos.persistence.repository.user.pay_table;

import com.kairos.persistence.model.pay_table.PayGradePayGroupAreaRelationShip;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_PAY_GRADE;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_PAY_GROUP_AREA;

/**
 * Created by vipul on 16/3/18.
 */
@Repository
public interface PayTableRelationShipGraphRepository extends Neo4jBaseRepository<PayGradePayGroupAreaRelationShip, Long> {

    @Query("MATCH(payTable:PayTable{deleted:false})-[:"+HAS_PAY_GRADE+"]-(payGrade:PayGrade)-[pgaRel:"+HAS_PAY_GROUP_AREA+"]-(payGroupArea:PayGroupArea) where id(payTable)={0} return pgaRel")
    List<PayGradePayGroupAreaRelationShip> findAllByPayTableId(Long payTableId);
}

