package com.kairos.persistence.repository.system_setting;

import com.kairos.persistence.model.system_setting.SystemLanguage;
import com.kairos.persistence.model.system_setting.SystemLanguageQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import static com.kairos.persistence.model.constants.RelationshipConstants.*;

@Repository
public interface SystemLanguageGraphRepository extends Neo4jBaseRepository<SystemLanguage,Long> {

    @Query("MATCH (language:SystemLanguage{deleted:false}) WHERE lower(language.name)=lower({0})\n" +
            "RETURN CASE WHEN count(language)>0 THEN true ELSE false END")
    boolean isSystemLanguageExistsWithSameName(String name);

    @Query("MATCH (language:SystemLanguage{deleted:false}) RETURN language")
    List<SystemLanguage> getListOfSystemLanguage();


    @Query("MATCH (language:SystemLanguage{deleted:false,active:true})  RETURN language")
    List<SystemLanguage> getActiveSystemLanguages();

    @Query("MATCH (language:SystemLanguage{defaultLanguage:true,deleted:false})\n" +
            "RETURN CASE WHEN count(language)>0 THEN true ELSE false END")
    boolean isDefaultSystemLanguageExists();

    @Query("MATCH (language:SystemLanguage{defaultLanguage:true,deleted:false, active:true}) WHERE NOT(id(language)={0}) \n" +
            "RETURN CASE WHEN count(language)>0 THEN true ELSE false END")
    boolean isDefaultSystemLanguageExistsExceptId(Long syatemLanguageId);

    @Query("MATCH (c:Country)-[:"+ HAS_SYSTEM_LANGUAGE+"{defaultLanguage:true}]-(language:SystemLanguage) WHERE id(language)={0} \n" +
            "RETURN CASE WHEN count(language)>0 THEN true ELSE false END")
    boolean isSystemLanguageSetInAnyCountry(Long syatemLanguageId);

    @Query("MATCH (c:Country)-[:"+ HAS_SYSTEM_LANGUAGE +"]-(language:SystemLanguage) WHERE id(c)={0} RETURN language")
    SystemLanguage getSystemLanguageOfCountry(Long countryId);


    @Query("MATCH (language:SystemLanguage{deleted:false}) WHERE lower(language.name)=lower({0})\n" +
            "RETURN language")
    SystemLanguage findSystemLanguageByName(String name);

    @Query("MATCH (language:SystemLanguage{deleted:false}) SET language.defaultLanguage={0}")
    void setDefaultStatusForAllLanguage(boolean defaultStatus);

    @Query("MATCH (language:SystemLanguage{deleted:false, defaultLanguage:true}) RETURN language")
    SystemLanguage getDefaultSystemLangugae();

    @Query("MATCH (language:SystemLanguage{deleted:false}) WHERE id(language)={0} RETURN language")
    SystemLanguage findSystemLanguageById(Long id);


    @Query("MATCH (c:Country)-[rel:"+ HAS_SYSTEM_LANGUAGE +"]->(language:SystemLanguage{active:true}) WHERE id(c)={0} " +
            "RETURN id(language) as id,language.name as name,language.active as active,rel.defaultLanguage as defaultLanguage")
    List<SystemLanguageQueryResult> findSystemLanguagesByCountryId(Long countryId);

}
