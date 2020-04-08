package com.kairos.persistence.repository.user.expertise;

import com.kairos.persistence.model.user.expertise.ChildCareDays;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO_EXPERTISE;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_CARE_DAYS;

@Repository
public interface ChildCareDaysGraphRepository extends Neo4jBaseRepository<ChildCareDays, Long> {

    @Query("MATCH(expertise:Expertise{deleted:false})<-[expRel:" + BELONGS_TO_EXPERTISE + "]->(childCareDays:ChildCareDays{deleted:false})-[careDayRel:"+HAS_CARE_DAYS+"]-(careDays:CareDays) WHERE id(expertise)={0}" +
            " RETURN childCareDays, COLLECT(careDayRel) , COLLECT(careDays) ORDER BY startDate ASC")
    List<ChildCareDays> getChildCareDaysOfExpertise(Long expertiseId);

    @Query("MATCH(childCareDays:ChildCareDays{deleted:false,published:true})-[:" + BELONGS_TO_EXPERTISE + "]->(expertise:Expertise{deleted:false}) WHERE id(expertise)={0} RETURN childCareDays ORDER BY childCareDays.startDate DESC LIMIT 1 ")
    ChildCareDays findLastByExpertiseId(Long expertiseId);

    @Query("MATCH(child:ChildCareDays{deleted:false})-[relation:VERSION_OF]->(childCareDays:ChildCareDays{deleted:false}) " +
            "WHERE id(child)={0} AND id(childCareDays)={1} " +
            "set childCareDays.hasDraftCopy=false  detach delete relation")
    void detachChildCareDays(Long id, Long parentChildCareDaysId);

    @Query("MATCH(child:ChildCareDays{deleted:false})-[relation:VERSION_OF]->(childCareDays:ChildCareDays{deleted:false}) \n" +
            "WHERE id(child)={0} AND id(childCareDays)={1}\n" +
            "set childCareDays.endDate={2} detach delete relation")
    void setEndDateToChildCareDays(Long id, Long childCareDaysId, String endDate);

    @Query("MATCH(expertise:Expertise{deleted:false})<-[expRel:" + BELONGS_TO_EXPERTISE + "]->(ccd:ChildCareDays{deleted:false})-[careDayRel:"+HAS_CARE_DAYS+"]-(careDays:CareDays) " +
            "WHERE id(expertise)={0} AND ccd.startDate <= DATE({1}) AND (ccd.endDate IS NULL  OR DATE({1})<=ccd.endDate) \" +\n" +
            " RETURN ccd,COLLECT(careDayRel),COLLECT(careDays)")
    ChildCareDays findChildCareDaysBySelectedDate(Long expertiseId, String selectedDate);

}
