package com.kairos.persistence.repository.user.expertise;

import com.kairos.persistence.model.user.expertise.SeniorDays;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

@Repository
public interface SeniorDaysGraphRepository  extends Neo4jBaseRepository<SeniorDays, Long> {

    @Query("MATCH(expertise:Expertise{deleted:false})<-[expRel:" + BELONGS_TO_EXPERTISE + "]->(seniorDays:SeniorDays{deleted:false}) WHERE id(expertise)={0} " +
            "OPTIONAL MATCH(seniorDays)-[careDayRel:"+HAS_CARE_DAYS+"]-(careDays:CareDays) " +
            " RETURN seniorDays, COLLECT(careDayRel) , COLLECT(careDays) ORDER BY seniorDays.startDate,seniorDays.creationDate")
    List<SeniorDays> getSeniorDaysOfExpertise(Long expertiseId);

    @Query("MATCH(expertise:Expertise{deleted:false})<-[expRel:" + BELONGS_TO_EXPERTISE + "]-(seniorDays:SeniorDays{deleted:false,published:true})-[r:"+HAS_CARE_DAYS+"]-(careDays:CareDays) WHERE id(expertise)={0} RETURN seniorDays,expRel,expertise,COLLECT(r),COLLECT(careDays) ORDER BY seniorDays.startDate DESC LIMIT 1 ")
    SeniorDays findLastByExpertiseId(Long expertiseId);

    @Query("MATCH(child:SeniorDays{deleted:false})-[relation:VERSION_OF]->(seniorDays:SeniorDays{deleted:false}) " +
            "WHERE id(child)={0} AND id(seniorDays)={1} " +
            " detach delete relation")
    void detachSeniorDays(Long id, Long parentSeniorDaysId);

    @Query("MATCH(child:SeniorDays{deleted:false})-[relation:VERSION_OF]->(seniorDays:SeniorDays{deleted:false}) \n" +
            "WHERE id(child)={0} AND id(seniorDays)={1}\n" +
            "set seniorDays.endDate={2} detach delete relation")
    void setEndDateToSeniorDays(Long id, Long parentSeniorDaysId, String endDate);

    @Query("MATCH(expertise:Expertise{deleted:false})<-[expRel:" + BELONGS_TO_EXPERTISE + "]->(seniorDays:SeniorDays{deleted:false})-[careDayRel:"+HAS_CARE_DAYS+"]-(careDays:CareDays) " +
            "WHERE id(expertise)={0} AND seniorDays.startDate <= DATE({1}) AND (seniorDays.endDate IS NULL  OR DATE({1})<=seniorDays.endDate) " +
            " RETURN seniorDays,COLLECT(careDayRel),COLLECT(careDays)")
    SeniorDays findSeniorDaysBySelectedDate(Long expertiseId, String selectedDate);

    @Query("MATCH(seniorDays:SeniorDays{deleted:false,published:false}) WHERE id(seniorDays)={0}" +
            "OPTIONAL MATCH(seniorDays)-[rel:"+VERSION_OF+"]-(version:SeniorDays) " +
            "SET seniorDays.deleted=true,version.oneTimeUpdatedAfterPublish=false RETURN count(seniorDays)>0")
    boolean deleteSeniorDays(Long seniorDaysId);

}
