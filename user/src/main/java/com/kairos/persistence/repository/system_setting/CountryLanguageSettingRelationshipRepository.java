package com.kairos.persistence.repository.system_setting;

import com.kairos.persistence.model.system_setting.CountryLanguageSettingRelationship;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_SYSTEM_LANGUAGE;

@Repository
public interface CountryLanguageSettingRelationshipRepository extends Neo4jBaseRepository<CountryLanguageSettingRelationship,Long> {

    @Query("MATCH(country:Country)-[rel:"+ HAS_SYSTEM_LANGUAGE +"]-(language:SystemLanguage) WHERE id(country)={0} RETURN id(rel)")
    List<Long> findAllByCountryId(Long CountryId);

    @Query("MATCH(country:Country)-[rel:"+ HAS_SYSTEM_LANGUAGE +"]-(language:SystemLanguage) WHERE id(country)={0} And id(language)={1} RETURN rel")
    CountryLanguageSettingRelationship findByCountryIdAndSystemLanguageId(Long countryId,Long systemLanguageId);

}
