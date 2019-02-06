package com.kairos.persistence.repository.user.country;
import com.kairos.persistence.model.country.default_data.VatType;
import com.kairos.persistence.model.country.default_data.VatTypeDTO;
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
public interface VatTypeGraphRepository extends Neo4jBaseRepository<VatType,Long>{

    @Query("MATCH (c:Country)-[:"+ BELONGS_TO +"]-(vt:VatType {isEnabled:true}) where id(c)={0} " +
            " RETURN id(vt) as id, vt.name as name, vt.code as code, vt.description as description, vt.percentage as percentage ORDER BY vt.creationDate DESC")
    List<VatTypeDTO> findVatTypesByCountry(long countryId);

    @Query("MATCH(country:Country)<-[:" + BELONGS_TO + "]-(vatType:VatType {isEnabled:true}) WHERE id(country)={0} AND id(vatType)<>{3} AND (vatType.name =~{1} OR vatType.code={2}) " +
            " WITH count(vatType) as totalCount " +
            " RETURN CASE WHEN totalCount>0 THEN TRUE ELSE FALSE END as result")
    Boolean vatTypeExistInCountryByNameOrCode(Long countryId, String name, int code, Long currentVatTypeId);
}
