package com.kairos.persistence.repository.user.country;

import com.kairos.persistence.model.user.country.equipment.Equipment;
import com.kairos.persistence.model.user.country.equipment.EquipmentQueryResult;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.COUNTRY_HAS_EQUIPMENT;
import static com.kairos.persistence.model.constants.RelationshipConstants.COUNTRY_HAS_FEATURE;

/**
 * Created by prerna on 12/12/17.
 */
@Repository
public interface EquipmentGraphRepository extends GraphRepository<Equipment> {

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_EQUIPMENT+"]->(equipment:Equipment)\n" +
            "WHERE equipment.name={0} AND id(country) = {1} AND equipment.deleted={2} \n" +
            "RETURN CASE WHEN count(equipment)>0 THEN true ELSE false END")
    boolean isEquipmentExistsWithSameName(String name, Long countryId, boolean isDeleted);

    @Query("MATCH (c:Country) WHERE id(c)={0} CREATE (c)-[:"+COUNTRY_HAS_EQUIPMENT+"]->(equipment:Equipment{name:{1}, description:{2}, deleted:false, creationDate:{3}, lastModificationDate:{3}}) return equipment")
    Equipment createEquipment(Long countryId, String equipmentName, String description, Long date);

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_EQUIPMENT+"]->(equipment:Equipment)\n" +
            "WHERE id(equipment)={0} AND id(country) = {1} AND equipment.deleted={2} \n" +
            "RETURN equipment")
    Equipment getEquipmentById(Long tagId, Long countryId, boolean isDeleted);

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_EQUIPMENT+"]->(equipment:Equipment)\n" +
            "WHERE id(country)={0} AND equipment.deleted= {1} AND lower(equipment.name) contains lower({2})\n" +
            "return id(equipment) as id, equipment.name as name, equipment.description as description, equipment.category as category")
    List<EquipmentQueryResult> getListOfEquipment(Long countryId , boolean deleted, String searchTextRegex);

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_EQUIPMENT+"]->(equipment:Equipment)\n"+
            "WHERE id(country)={0} AND equipment.name = {1} AND equipment.deleted= {2} return equipment")
    Equipment getEquipmentByName(long countryId, String name, boolean deleted);
}
