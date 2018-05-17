package com.kairos.persistence.repository.user.country;

import com.kairos.persistence.model.timetype.PresenceTypeDTO;
import com.kairos.persistence.model.user.country.PresenceType;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;

/**
 * Created by vipul on 10/11/17.
 */
@Repository
public interface PresenceTypeRepository extends Neo4jBaseRepository<PresenceType,Long> {
    @Query("match(presenceType:PresenceType)-[:" + BELONGS_TO + "]-> (country:Country) where Id(country)={2} AND presenceType.name =~ {0} AND presenceType.deleted={1} return presenceType")
    PresenceType findByNameAndDeletedAndCountryId(String name, boolean deleted, Long countryId);

    @Query("match(presenceType:PresenceType)-[:" + BELONGS_TO + "]-> (country:Country) where Id(country)={0} AND presenceType.deleted={1} return id(presenceType) as id,presenceType.name as name")
    List<PresenceTypeDTO> getAllPresenceTypeByCountryId(Long countryId, boolean deleted);

    @Query("match(presenceType:PresenceType)-[:" + BELONGS_TO + "]-> (country:Country) where Id(country)={0} AND presenceType.deleted={3} AND Id(presenceType) <> {1} AND presenceType.name =~ {2} \n"+
            "return CASE WHEN count(presenceType) >0 THEN  true ELSE false end")
    boolean findByNameAndDeletedAndCountryIdExcludingCurrent(Long countryId, Long presenceTypeId, String presenceTyName, boolean deleted);

}
