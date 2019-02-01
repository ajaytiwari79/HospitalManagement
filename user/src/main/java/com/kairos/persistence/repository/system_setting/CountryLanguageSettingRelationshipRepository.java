package com.kairos.persistence.repository.system_setting;

import com.kairos.persistence.model.system_setting.CountryLanguageSettingRelationship;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_SYSTEM_LANGUAGE;

@Repository
public interface CountryLanguageSettingRelationshipRepository extends Neo4jBaseRepository<CountryLanguageSettingRelationship,Long> {

    @Query("Match (c:Country)-[rel:"+ HAS_SYSTEM_LANGUAGE +"]-(language:SystemLanguage) WHERE id(c)={0} return id(rel)")
    List<Long> findAllByCountryId(Long CountryId);

    @Query("Match (c:Country)-[rel:"+ HAS_SYSTEM_LANGUAGE +"]-(language:SystemLanguage) WHERE id(c)={0} And id(language)={1} return rel")
    CountryLanguageSettingRelationship findByCountryIdAndSystemLanguageId(Long countryId,Long systemLanguageId);

}
