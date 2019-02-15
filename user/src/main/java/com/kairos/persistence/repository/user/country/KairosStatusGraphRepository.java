package com.kairos.persistence.repository.user.country;
import com.kairos.persistence.model.country.default_data.KairosStatus;
import com.kairos.persistence.model.country.default_data.KairosStatusDTO;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;

/**
 * Created by oodles on 9/1/17.
 */
@Repository
public interface KairosStatusGraphRepository extends Neo4jBaseRepository<KairosStatus,Long>{

    List<KairosStatus> findAll();

    @Query("MATCH (country:Country)<-[:"+ BELONGS_TO +"]-(kairosStatus:KairosStatus {isEnabled:true}) where id(country)={0} " +
            "RETURN id(kairosStatus) as id, kairosStatus.name as name, kairosStatus.description as description ORDER BY kairosStatus.creationDate DESC")
    List<KairosStatusDTO> findKairosStatusByCountry(long countryId);

    @Query("MATCH(country:Country)<-[:" + BELONGS_TO + "]-(kairosStatus:KairosStatus {isEnabled:true}) WHERE id(country)={0} AND id(kairosStatus)<>{2} AND kairosStatus.name =~{1}  " +
            " WITH count(kairosStatus) as totalCount " +
            " RETURN CASE WHEN totalCount>0 THEN TRUE ELSE FALSE END as result")
    Boolean kairosStatusExistInCountryByName(Long countryId, String name, Long currentKairosStatusId);
}
