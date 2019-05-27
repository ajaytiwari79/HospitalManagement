package com.kairos.persistence.repository.user.country;

import com.kairos.persistence.model.country.equipment.Equipment;
import com.kairos.persistence.model.country.equipment.EquipmentQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by prerna on 12/12/17.
 */
@Repository
public interface EquipmentGraphRepository extends Neo4jBaseRepository<Equipment,Long> {

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_EQUIPMENT+"]->(equipment:Equipment)\n" +
            "WHERE equipment.name={0} AND id(country) = {1} AND equipment.deleted={2} \n" +
            "RETURN CASE WHEN count(equipment)>0 THEN true ELSE false END")
    boolean isEquipmentExistsWithSameName(String name, Long countryId, boolean isDeleted);

    @Query("MATCH (c:Country) WHERE id(c)={0} CREATE (c)-[:"+COUNTRY_HAS_EQUIPMENT+"]->(equipment:Equipment{name:{1}, description:{2}, deleted:false, creationDate:{3}, lastModificationDate:{3}}) return equipment")
    Equipment createEquipment(Long countryId, String equipmentName, String description, Long date);

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_EQUIPMENT+"]->(equipment:Equipment)\n" +
            "WHERE id(equipment)={0} AND id(country) = {1} AND equipment.deleted={2} \n" +
            "RETURN equipment")
    Equipment getEquipmentById(Long equipmentId, Long countryId, boolean isDeleted);

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_EQUIPMENT+"]->(equipment:Equipment)\n" +
            "WHERE id(country)={0} AND equipment.deleted= {1} AND lower(equipment.name) contains lower({2})\n" +
            "WITH equipment\n" +
            "MATCH (equipment)-[:"+EQUIPMENT_HAS_CATEGORY+"]-(equipCat:EquipmentCategory) \n" +
            "return id(equipment) as id, equipment.name as name, equipment.description as description, {id:id(equipCat) , name:equipCat.name, description:equipCat.description} as category")
    List<EquipmentQueryResult> getListOfEquipment(Long countryId , boolean deleted, String searchTextRegex);

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_EQUIPMENT+"]->(equipment:Equipment)\n"+
            "WHERE id(country)={0} AND equipment.name = {1} AND equipment.deleted= {2} return equipment")
    Equipment getEquipmentByName(long countryId, String name, boolean deleted);

    @Query("MATCH (c:Country),(e:Equipment)\n" +
            "WHERE id(c)={0} AND id(e)={1}\n" +
            "CREATE (c)-[r:"+COUNTRY_HAS_EQUIPMENT+"]->(e)")
    void addEquipmentInCountry(long countryId, long equipmentId);

    @Query("MATCH (equipment:Equipment)-[r:"+EQUIPMENT_HAS_CATEGORY+"]->(equipmentCat:EquipmentCategory)\n"+
            "DELETE r")
    void detachEquipmentCategory(long equipmentId);


    @Query("MATCH (o:Unit)-[:"+ORGANIZATION_HAS_RESOURCE+"]->(res:Resource{deleted:false})-[:"+RESOURCE_HAS_EQUIPMENT+"]->(equipment:Equipment{deleted:{2}}) where id(o)={0} AND id(res)={1}\n" +
            "return id(equipment) as id, equipment.name as name, equipment.description as description")
    List<EquipmentQueryResult> getResourcesSelectedEquipments(Long organizationId, Long resourceId, boolean deleted);

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_EQUIPMENT+"]->(equipment:Equipment)\n" +
            "WHERE id(country)={0} AND equipment.deleted= {1} AND id(equipment) IN {2} return equipment")
    List<Equipment> getListOfEquipmentByIds(Long countryId , boolean deleted, List<Long> equipmentIds);

    @Query("MATCH (resource:Resource)-[r:"+RESOURCE_HAS_EQUIPMENT+"]->(equipment:Equipment) WHERE id(resource)={0}\n"+
            "DELETE r")
    void detachResourceEquipments(long resourceId);
}
