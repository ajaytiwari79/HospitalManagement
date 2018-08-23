package com.kairos.persistence.repository.user.country;

import com.kairos.persistence.model.country.default_data.CompanyCategory;
import com.kairos.persistence.model.organization.company.CompanyCategoryResponseDTO;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;

/**
 * Created by pavan on 6/4/18.
 */
@Repository
public interface CompanyCategoryGraphRepository extends Neo4jBaseRepository<CompanyCategory, Long> {

    @Query("MATCH (country:Country)-[:" + BELONGS_TO + "]-(companyCategory:CompanyCategory{deleted:false}) where id(country)={0} return id(companyCategory) as id, companyCategory.name as name," +
            " companyCategory.description as description ")
    List<CompanyCategoryResponseDTO> findCompanyCategoriesByCountry(Long countryId);

    @Query("MATCH (country:Country)-[:" + BELONGS_TO + "]-(companyCategory:CompanyCategory{deleted:false}) where id(country)={0} AND id(companyCategory)= {1} return companyCategory")
    CompanyCategory findByCountryAndCompanycategory(Long countryId, Long companyCategoryId);

    @Query("MATCH (country:Country)-[:" + BELONGS_TO + "]-(companyCategory:CompanyCategory{deleted:false}) where id(country)={0} AND id(companyCategory) <> {1} AND companyCategory.name=~{2} " +
            "with count(companyCategory) as companyCategoryCount return CASE when companyCategoryCount>0 THEN  true ELSE false END as response ")
    boolean findByCountryAndNameExcludingCurrent(Long countryId, Long companyCategoryId, String name);

}
