package com.kairos.persistence.repository.user.country;
import com.kairos.persistence.model.country.default_data.OwnershipType;
import com.kairos.persistence.model.country.default_data.OwnershipTypeDTO;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;

/**
 * Created by oodles on 9/1/17.
 */
@Repository
public interface OwnershipTypeGraphRepository extends Neo4jBaseRepository<OwnershipType,Long>{

    @Query("MATCH (c:Country)-[:"+ BELONGS_TO +"]-(ot:OwnershipType {isEnabled:true}) where id(c)={0} " +
            "RETURN id(ot) as id, ot.name as name, ot.description as description ORDER BY ot.creationDate DESC")
    List<OwnershipTypeDTO> findOwnershipTypeByCountry(long countryId);

    @Query("MATCH(country:Country)<-[:" + BELONGS_TO + "]-(ownershipType:OwnershipType {isEnabled:true}) WHERE id(country)={0} AND id(ownershipType)<>{2} AND ownershipType.name =~{1}  " +
            " WITH count(ownershipType) as totalCount " +
            " RETURN CASE WHEN totalCount>0 THEN TRUE ELSE FALSE END as result")
    Boolean ownershipTypeExistInCountryByName(Long countryId, String name, Long currentOwnershipTypeId);
}
