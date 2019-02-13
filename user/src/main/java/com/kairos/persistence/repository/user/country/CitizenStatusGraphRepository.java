package com.kairos.persistence.repository.user.country;
import com.kairos.persistence.model.country.default_data.CitizenStatus;
import com.kairos.persistence.model.country.default_data.CitizenStatusDTO;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.CIVILIAN_STATUS;

/**
 * Created by oodles on 5/1/17.
 */
@Repository
public interface CitizenStatusGraphRepository extends Neo4jBaseRepository<CitizenStatus,Long>{

    @Query("MATCH (country:Country)<-[:"+ CIVILIAN_STATUS +"]-(citizenStatus:CitizenStatus {isEnabled:true}) where id(country)={0} " +
            "RETURN id(citizenStatus) as id, citizenStatus.name as name, citizenStatus.description as description ORDER BY citizenStatus.creationDate DESC")
    List<CitizenStatusDTO> findCitizenStatusByCountryId(long countryId);

    @Query("MATCH (cs:CitizenStatus{isEnabled:true})-[:"+ CIVILIAN_STATUS +"]-(c:Country) where id(c)={0} return {value:id(cs), label:cs.name,description:cs.description} as result")
    List<Map<String,Object>> findCitizenStatusByCountryIdAnotherFormat(long countryId);

    CitizenStatus findByName(String name);

    @Query("MATCH (cs:CitizenStatus{isEnabled:true})-[:"+ CIVILIAN_STATUS +"]-(c:Country) where id(c)={0} AND cs.description={1} return cs")
    CitizenStatus findByDescription(long countryId, String description);

    @Query("MATCH(country:Country)<-[:" + CIVILIAN_STATUS + "]-(citizenStatus:CitizenStatus {isEnabled:true}) WHERE id(country)={0} AND id(citizenStatus)<>{2} AND citizenStatus.name =~{1}  " +
            " WITH count(citizenStatus) as totalCount " +
            " RETURN CASE WHEN totalCount>0 THEN TRUE ELSE FALSE END as result")
    Boolean citizenStatusExistInCountryByName(Long countryId, String name, Long currentCitizenStatusId);
}
