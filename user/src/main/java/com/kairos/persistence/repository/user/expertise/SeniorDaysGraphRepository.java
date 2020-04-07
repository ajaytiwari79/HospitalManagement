package com.kairos.persistence.repository.user.expertise;

import com.kairos.persistence.model.user.expertise.SeniorDays;
import com.kairos.persistence.model.user.expertise.response.CareDaysQueryResult;
import com.kairos.persistence.model.user.expertise.response.FunctionalPaymentDTO;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.APPLICABLE_FOR_EXPERTISE;
import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO_EXPERTISE;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_CARE_DAYS;

@Repository
public interface SeniorDaysGraphRepository  extends Neo4jBaseRepository<SeniorDays, Long> {

    @Query("MATCH(expertise:Expertise{deleted:false})<-[expRel:" + BELONGS_TO_EXPERTISE + "]->(seniorDays:SeniorDays{deleted:false})-[careDayRel:"+HAS_CARE_DAYS+"]-(careDays:CareDays) WHERE id(expertise)={0}" +
            " RETURN seniorDays, COLLECT(careDayRel) , COLLECT(seniorDays) ORDER BY startDate ASC")
    List<SeniorDays> getSeniorDaysOfExpertise(Long expertiseId);

    @Query("MATCH(seniorDays:SeniorDays{deleted:false,published:true})-[:" + BELONGS_TO_EXPERTISE + "]->(expertise:Expertise{deleted:false}) WHERE id(expertise)={0} RETURN seniorDays ORDER BY seniorDays.startDate DESC LIMIT 1 ")
    SeniorDays findLastByExpertiseId(Long expertiseId);

    @Query("MATCH(child:SeniorDays{deleted:false})-[relation:VERSION_OF]->(seniorDays:SeniorDays{deleted:false}) " +
            "WHERE id(child)={0} AND id(seniorDays)={1} " +
            "set functionalPayment.hasDraftCopy=false  detach delete relation")
    void detachSeniorDays(Long id, Long parentSeniorDaysId);

    @Query("MATCH(child:SeniorDays{deleted:false})-[relation:VERSION_OF]->(seniorDays:SeniorDays{deleted:false}) \n" +
            "WHERE id(child)={0} AND id(seniorDays)={1}\n" +
            "set functionalPayment.endDate={2} detach delete relation")
    void setEndDateToSeniorDays(Long id, Long parentFunctionalPaymentId, String endDate);

    @Query("OPTIONAL MATCH(expertise:Expertise{deleted:false})<-[expRel:" + BELONGS_TO_EXPERTISE + "]-(seniorDays:SeniorDays{deleted:false})-[careDayRel:"+HAS_CARE_DAYS+"]-(careDays:CareDays) WHERE id(expertise) IN {0} " +
            "WITH DISTINCT expertise,COLLECT(seniorDays) as seniorDays,COLLECT(careDayRel) as careDayRel,COLLECT(careDays) as careDays" +
            "OPTIONAL MATCH(expertise)<-[expChildCareDayRel:" + BELONGS_TO_EXPERTISE + "]-(childCareDays:ChildCareDays{deleted:false})-[childCareDayRel:"+HAS_CARE_DAYS+"]-(ccd:CareDays) " +
            " RETURN DISTINCT id(expertise) as expertiseId,seniorDays, careDayRel, COLLECT(childCareDayRel), COLLECT(ccd) ORDER BY startDate ASC")
    List<CareDaysQueryResult> getSeniorAbdChildCareDaysByExpertiseIds(Collection<Long> expertiseIds);
}
