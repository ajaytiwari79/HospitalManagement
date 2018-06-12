package com.kairos.persistence.repository.system_setting;

import com.kairos.persistence.model.system_setting.SystemLanguage;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import com.kairos.response.dto.web.system_setting.SystemLanguageDTO;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemLanguageGraphRepository extends Neo4jBaseRepository<SystemLanguage,Long> {

    @Query("Match (language:SystemLanguage) WHERE language.name={0} AND language.deleted=false\n" +
            "RETURN CASE WHEN count(language)>0 THEN true ELSE false END")
    boolean isSystemLanguageExistsWithSameName(String name);

    @Query("Match (language:SystemLanguage{deleted:false}) return language")
    List<SystemLanguage> getListOfSystemLanguage();

    @Query("Match (language:SystemLanguage{defaultLanguage:true,deleted=false})\n" +
            "RETURN CASE WHEN count(language)>0 THEN true ELSE false END")
    boolean isDefaultSystemLanguageExists();

    @Query("Match (language:SystemLanguage{defaultLanguage:true,deleted=false}) WHERE id(language) NOT(id(language)={0}) \n" +
            "RETURN CASE WHEN count(language)>0 THEN true ELSE false END")
    boolean isDefaultSystemLanguageExistsExceptId(Long syatemLanguageId);
}
