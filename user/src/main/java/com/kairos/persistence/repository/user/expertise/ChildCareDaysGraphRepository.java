package com.kairos.persistence.repository.user.expertise;

import com.kairos.persistence.model.user.expertise.ChildCareDays;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

@Repository
public interface ChildCareDaysGraphRepository extends Neo4jBaseRepository<ChildCareDays, Long> {

    @Query("MATCH(expertise:Expertise{deleted:false})<-[expRel:" + BELONGS_TO_EXPERTISE + "]->(childCareDays:ChildCareDays{deleted:false}) WHERE id(expertise)={0} " +
            "OPTIONAL MATCH(childCareDays)-[careDayRel:"+HAS_CARE_DAYS+"]-(careDays:CareDays) " +
            " RETURN childCareDays, COLLECT(careDayRel) , COLLECT(careDays) ORDER BY childCareDays.startDate,childCareDays.creationDate")
    List<ChildCareDays> getChildCareDaysOfExpertise(Long expertiseId);

    @Query("MATCH(expertise:Expertise{deleted:false})<-[expRel:" + BELONGS_TO_EXPERTISE + "]-(childCareDays:ChildCareDays{deleted:false,published:true})-[r:"+HAS_CARE_DAYS+"]-(careDays:CareDays) WHERE id(expertise)={0} RETURN childCareDays,expRel,expertise,COLLECT(r),COLLECT(careDays) ORDER BY childCareDays.startDate DESC LIMIT 1 ")
    ChildCareDays findLastByExpertiseId(Long expertiseId);

    @Query("MATCH(child:ChildCareDays{deleted:false})-[relation:VERSION_OF]->(childCareDays:ChildCareDays{deleted:false}) " +
            "WHERE id(child)={0} AND id(childCareDays)={1} " +
            " detach delete relation")
    void detachChildCareDays(Long id, Long parentChildCareDaysId);

    @Query("MATCH(child:ChildCareDays{deleted:false})-[relation:VERSION_OF]->(childCareDays:ChildCareDays{deleted:false}) \n" +
            "WHERE id(child)={0} AND id(childCareDays)={1}\n" +
            "set childCareDays.endDate={2} detach delete relation")
    void setEndDateToChildCareDays(Long id, Long childCareDaysId, String endDate);

    @Query("MATCH(expertise:Expertise{deleted:false})<-[expRel:" + BELONGS_TO_EXPERTISE + "]->(ccd:ChildCareDays{deleted:false})-[careDayRel:"+HAS_CARE_DAYS+"]-(careDays:CareDays) " +
            "WHERE id(expertise)={0} AND ccd.startDate <= DATE({1}) AND (ccd.endDate IS NULL  OR DATE({1})<=ccd.endDate) " +
            " RETURN ccd,COLLECT(careDayRel),COLLECT(careDays)")
    ChildCareDays findChildCareDaysBySelectedDate(Long expertiseId, String selectedDate);

    @Query("MATCH(child:ChildCareDays{deleted:false,published:false}) WHERE id(child)={0}" +
            "OPTIONAL MATCH(child)-[rel:"+VERSION_OF+"]-(version:ChildCareDays) " +
            "SET child.deleted=true,version.oneTimeUpdatedAfterPublish=false RETURN count(child)>0")
    boolean deleteChildCareDays(Long childCareId);

}
