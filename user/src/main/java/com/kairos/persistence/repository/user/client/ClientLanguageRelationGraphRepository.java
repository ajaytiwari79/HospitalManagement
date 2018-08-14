package com.kairos.persistence.repository.user.client;
import com.kairos.persistence.model.client.relationships.ClientLanguageRelation;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;

import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 17/1/17.
 */
public interface ClientLanguageRelationGraphRepository extends Neo4jBaseRepository<ClientLanguageRelation,Long> {


    @Query("MATCH (c:Client)-[:KNOWS]-(l:Language{isEnabled:true}) where id(c)={0} return DISTINCT id(l)")
    List<Long> findClientLanguagesIds(Long clientId);

    @Query("MATCH (c:Client)-[r:KNOWS]-(l:Language {isEnabled:true}) where id(c)={0}  return { " +
            "name:l.name, " +
            "description:l.description, " +
            "readLevel:r.readLevel, " +
            "speakLevel:r.speakLevel, " +
            "id:id(l), " +
            "writeLevel:r.writeLevel " +
            "} as result ")
    List<Map<String,Object>> findClientLanguages(Long clientId);


    @Query("MATCH (c:Client)-[r:KNOWS]-(l:Language{isEnabled:true}) where id(c)={0}  return { id:id(l)}")
    List<Long> findClientLanguagesId(Long clientId);

    @Query("MATCH (c:Client)-[r:KNOWS]-(l:Language) where id(c)={0} AND id(l)={1} return r")
    ClientLanguageRelation checkRelationExist(Long clientId, Long languageId);

}
