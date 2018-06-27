package com.kairos.persistence.repository.user.language;
import com.kairos.persistence.model.user.language.LanguageLevel;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by prabjot on 28/11/16.
 */
@Repository
public interface LanguageLevelGraphRepository extends Neo4jBaseRepository<LanguageLevel,Long> {


    List<LanguageLevel> findAll();

    @Query("MATCH (l:LanguageLevel{isEnabled:true})-[:BELONGS_TO]-(c:Country) where id(c) = {0} return { id:id(l),name:l.name,description:l.description } as result")
    List<Map<String,Object>> getLanguageLevelByCountryId(long countryId);



}
