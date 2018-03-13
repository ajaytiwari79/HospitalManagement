package com.kairos.persistence.repository.user.pay_level;


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

    @Query("match(country:Country)-[:" + HAS_LEVEL + "]->(level:Level)  where id(country) = {0}\n" +
            "match(level)-[:" + IN_LEVEL + "]-(payGroupArea:PayGroupArea{deleted:false})-[rel:" + HAS_MUNICIPALITY + "]-(municipality:Municipality)\n" +
            "RETURN  id(payGroupArea) as id,payGroupArea.name as name,payGroupArea.description as description, id(municipality) as municipalityId, " +
            "id(level) as levelId,rel.endDateMillis as endDateMillis,rel.startDateMillis as startDateMillis")
    List<PayGroupAreaQueryResult> getPayGroupAreaByCountry(Long countryId);

    @Query("MATCH (level:Level)-[:" + IN_LEVEL + "]-(payGroupArea:PayGroupArea{deleted:false})-[rel:" + HAS_MUNICIPALITY + "]-(municipality:Municipality) where id(level)={0} AND id(municipality)={1}\n" +
            "RETURN  id(payGroupArea) as id,payGroupArea.name as name,payGroupArea.description as description, id(municipality) as municipalityId, " +
            "id(level) as levelId,rel.endDateMillis as endDateMillis,rel.startDateMillis as startDateMillis")
    List<PayGroupArea> findPayGroupAreaByLevelAndMunicipality(Long levelId, Long municipalityId);
}
