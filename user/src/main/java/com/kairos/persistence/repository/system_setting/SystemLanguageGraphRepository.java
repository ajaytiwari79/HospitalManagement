package com.kairos.persistence.repository.system_setting;

import com.kairos.persistence.model.system_setting.SystemLanguage;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import static com.kairos.persistence.model.constants.RelationshipConstants.*;

@Repository
public interface SystemLanguageGraphRepository extends Neo4jBaseRepository<SystemLanguage,Long> {

    @Query("Match (language:SystemLanguage{deleted:false}) WHERE lower(language.name)=lower({0})\n" +
            "RETURN CASE WHEN count(language)>0 THEN true ELSE false END")
    boolean isSystemLanguageExistsWithSameName(String name);

    @Query("Match (language:SystemLanguage{deleted:false}) return language")
    List<SystemLanguage> getListOfSystemLanguage();

    @Query("Match (language:SystemLanguage{deleted:false}) WHERE language.active={0} return language")
    List<SystemLanguage> getListOfSystemLanguageByActiveStatus(Boolean active);

    @Query("Match (language:SystemLanguage{defaultLanguage:true,deleted:false})\n" +
            "RETURN CASE WHEN count(language)>0 THEN true ELSE false END")
    boolean isDefaultSystemLanguageExists();

    @Query("Match (language:SystemLanguage{defaultLanguage:true,deleted:false, active:true}) WHERE NOT(id(language)={0}) \n" +
            "RETURN CASE WHEN count(language)>0 THEN true ELSE false END")
    boolean isDefaultSystemLanguageExistsExceptId(Long syatemLanguageId);

    @Query("Match (c:Country)-[:"+ HAS_SYSTEM_LANGUAGE +"]-(language:SystemLanguage) WHERE id(language)={0} \n" +
            "RETURN CASE WHEN count(language)>0 THEN true ELSE false END")
    boolean isSystemLanguageSetInAnyCountry(Long syatemLanguageId);

    @Query("Match (c:Country)-[:"+ HAS_SYSTEM_LANGUAGE +"]-(language:SystemLanguage) WHERE id(c)={0} return language")
    SystemLanguage getSystemLanguageOfCountry(Long syatemLanguageId);


    @Query("Match (language:SystemLanguage{deleted:false}) WHERE lower(language.name)=lower({0})\n" +
            "RETURN language")
    SystemLanguage findSystemLanguageByName(String name);

    @Query("Match (language:SystemLanguage{deleted:false}) SET language.defaultLanguage={0}")
    void setDefaultStatusForAllLangugae(boolean defaultStatus);

    @Query("Match (language:SystemLanguage{deleted:false, defaultLanguage:true}) return language")
    SystemLanguage getDefaultSystemLangugae();

    @Query("Match (language:SystemLanguage{deleted:false}) WHERE id(language)={0} RETURN language")
    SystemLanguage findSystemLanguageById(Long id);

}
