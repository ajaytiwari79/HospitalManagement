package com.kairos.persistence.repository.user.language;

import com.kairos.persistence.model.user.language.Language;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;

/**
 * Created by prabjot on 28/11/16.
 */
@Repository
public interface LanguageGraphRepository extends Neo4jBaseRepository<Language,Long> {


    @Override
    @Query("Match (language:Language{isEnabled:true}) return language")
    List<Language> findAll();

    @Query("MATCH (l:Language{isEnabled:true})-[:"+ BELONGS_TO +"]-(c:Country) where id(c) = {0} return l")
    List<Language> getLanguageByCountryId(long countryId);


    @Query("MATCH (l:Language{isEnabled:true})-[:"+ BELONGS_TO +"]-(c:Country) where id(c) = {0} return { value:id(l),label:l.name,description:l.description } as result")
    List<Map<String,Object>> getLanguageByCountryIdAnotherFormat(long countryId);


    @Query("MATCH (c:Client)-[r:KNOWS]-(l:Language) where id(c)={0} delete  r")
    void removeAllLanguagesFromClient(Long id);

    @Query("MATCH(country:Country)<-[:" + BELONGS_TO + "]-(language:Language {isEnabled:true}) WHERE id(country)={0} AND id(language)<>{2} AND language.name =~{1}  " +
            " WITH count(language) as totalCount " +
            " RETURN CASE WHEN totalCount>0 THEN TRUE ELSE FALSE END as result")
    Boolean languageExistInCountryByName(Long countryId, String name, Long currentLanguageId);
}
