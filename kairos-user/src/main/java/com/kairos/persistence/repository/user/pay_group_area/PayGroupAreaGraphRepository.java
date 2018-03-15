package com.kairos.persistence.repository.user.pay_group_area;


import com.kairos.persistence.model.user.pay_group_area.PayGroupArea;
import com.kairos.persistence.model.user.pay_group_area.PayGroupAreaQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by prabjot on 21/12/17.
 */
@Repository
public interface PayGroupAreaGraphRepository extends Neo4jBaseRepository<PayGroupArea, Long> {

    @Query("match(level:Level)  where id(level) = {0}\n" +
            "match(level)-[:" + IN_LEVEL + "]-(payGroupArea:PayGroupArea{deleted:false})-[rel:" + HAS_MUNICIPALITY + "]-(municipality:Municipality)\n" +
            "RETURN  id(payGroupArea) as payGroupAreaId,payGroupArea.name as name,payGroupArea.description as description, municipality as municipality, " +
            "id(level) as levelId,id(rel) as id,rel.endDateMillis as endDateMillis,rel.startDateMillis as startDateMillis")
    List<PayGroupAreaQueryResult> getPayGroupAreaWithMunicipalityByOrganizationLevelId(Long organizationLevelId);


    @Query("match(level:Level)  where id(level) = {0}\n" +
            "match(level)-[:" + IN_LEVEL + "]-(payGroupArea:PayGroupArea{deleted:false})-[rel:" + HAS_MUNICIPALITY + "]-(municipality:Municipality)\n" +
            "RETURN  id(payGroupArea) as id,payGroupArea.name as name,payGroupArea.description as description")
    List<PayGroupAreaQueryResult> getPayGroupAreaByOrganizationLevelId(Long organizationLevelId);

    @Query("MATCH (level:Level)-[:" + IN_LEVEL + "]-(payGroupArea:PayGroupArea{deleted:false})-[rel:" + HAS_MUNICIPALITY + "]-(municipality:Municipality) where id(level)={0} AND id(municipality)={1} AND id(rel) <> {2}\n" +
            "RETURN  id(payGroupArea) as payGroupAreaId,payGroupArea.name as name,payGroupArea.description as description,municipality as municipality, " +
            "id(level) as levelId,id(rel) as id,rel.endDateMillis as endDateMillis,rel.startDateMillis as startDateMillis")
    List<PayGroupAreaQueryResult> findPayGroupAreaByLevelAndMunicipality(Long levelId, Long municipalityId, Long currentRelationId);

    @Query("MATCH (payGroupArea:PayGroupArea{deleted:false})-[rel:" + HAS_MUNICIPALITY + "]-(municipality:Municipality) where id(rel)={0} AND id(payGroupArea)={1} AND id(municipality)={2} \n" +
            "SET rel.endDateMillis ={3}")
    void updateEndDateOfPayGroupArea(Long id, Long payGroupAreaId, Long municipalityId, Long dateOneDayLessStartDate);

    @Query("MATCH (payGroupArea:PayGroupArea{deleted:false})-[rel:" + HAS_MUNICIPALITY + "]-(municipality:Municipality) where id(payGroupArea)={0} AND id(municipality)={1} AND id(rel)={2}\n" +
            "DETACH DELETE rel")
    void removePayGroupAreaFromMunicipality(Long payGroupAreaId, Long municipalityId, Long relationshipId);

    @Query("MATCH (payGroupArea:PayGroupArea{deleted:false})-[rel:" + HAS_MUNICIPALITY + "]-(municipality:Municipality) where id(rel)={0} AND id(payGroupArea)={1} AND id(municipality)={2}\n" +
            "RETURN  id(payGroupArea) as payGroupAreaId,payGroupArea.name as name,payGroupArea.description as description,municipality as municipality, " +
            "rel.endDateMillis as endDateMillis,id(rel) as id,rel.startDateMillis as startDateMillis")
    PayGroupAreaQueryResult findPayGroupAreaByIdAndMunicipality(Long id, Long payGroupAreaId, Long municipalityId);
}
