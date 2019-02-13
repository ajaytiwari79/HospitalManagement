package com.kairos.persistence.repository.user.language;
import com.kairos.persistence.model.user.language.LanguageLevel;
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
public interface LanguageLevelGraphRepository extends Neo4jBaseRepository<LanguageLevel,Long> {


    List<LanguageLevel> findAll();

    @Query("MATCH (l:LanguageLevel{isEnabled:true})-[:"+BELONGS_TO+"]-(c:Country) where id(c) = {0} return { id:id(l),name:l.name,description:l.description } as result")
    List<Map<String,Object>> getLanguageLevelByCountryId(long countryId);


    @Query("MATCH(country:Country)<-[:" + BELONGS_TO + "]-(languageLevel:LanguageLevel {isEnabled:true}) WHERE id(country)={0} AND id(languageLevel)<>{2} AND languageLevel.name =~{1}  " +
            " WITH count(languageLevel) as totalCount " +
            " RETURN CASE WHEN totalCount>0 THEN TRUE ELSE FALSE END as result")
    Boolean languageLevelExistInCountryByName(Long countryId, String name, Long currentLanguageLevelId);



}
