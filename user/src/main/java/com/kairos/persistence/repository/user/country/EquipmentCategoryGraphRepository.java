package com.kairos.persistence.repository.user.country;

import com.kairos.persistence.model.country.equipment.EquipmentCategory;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by prerna on 12/12/17.
 */
@Repository
public interface EquipmentCategoryGraphRepository extends Neo4jBaseRepository<EquipmentCategory,Long> {

    @Query("Match (ec:EquipmentCategory) with count(ec) as equipmentSize RETURN CASE WHEN equipmentSize >0 THEN true ELSE false END")
    Boolean ifEquipmentCategoryExists();

    @Query("Match (ec:EquipmentCategory) return ec")
    List<EquipmentCategory> getEquipmentCategories();

}
