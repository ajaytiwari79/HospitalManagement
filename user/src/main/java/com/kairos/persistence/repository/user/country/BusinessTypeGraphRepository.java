package com.kairos.persistence.repository.user.country;
import com.kairos.persistence.model.country.default_data.BusinessType;
import com.kairos.persistence.model.country.default_data.BusinessTypeDTO;
import com.kairos.persistence.model.country.default_data.OwnershipTypeDTO;
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
public interface BusinessTypeGraphRepository extends Neo4jBaseRepository<BusinessType,Long>{

    List<BusinessType> findAll();

    @Query("MATCH (country:Country)<-[:"+ BELONGS_TO +"]-(businessType:BusinessType {isEnabled:true}) where id(country)={0} " +
            "RETURN id(businessType) as id, businessType.name as name, businessType.description as description ORDER BY businessType.creationDate DESC")
    List<BusinessTypeDTO> findBusinessTypeByCountry(long countryId);

    @Query("MATCH (businessType:BusinessType) where id(businessType) in {0} return businessType")
    List<BusinessType> findByIdIn(List<Long> ids);

    @Query("MATCH (country:Country)<-[:" + BELONGS_TO + "]-(businessType:BusinessType {isEnabled:true}) where id(country)={0} return businessType")
    List<BusinessType> findBusinessTypesByCountry(long countryId);

    @Query("MATCH(country:Country)<-[:" + BELONGS_TO + "]-(businessType:BusinessType {isEnabled:true}) WHERE id(country)={0} AND id(businessType)<>{2} AND businessType.name =~{1}  " +
            " WITH count(businessType) as totalCount " +
            " RETURN CASE WHEN totalCount>0 THEN TRUE ELSE FALSE END as result")
    Boolean businessTypeExistInCountryByName(Long countryId, String name, Long currentBusinessTypeId);
}
