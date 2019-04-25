package com.kairos.persistence.repository.user.region;

import com.kairos.persistence.model.user.region.Province;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 7/1/17.
 */
@Repository
public interface ProvinceGraphRepository extends Neo4jBaseRepository<Province,Long>{
    List<Province> findAll();


    @Query("MATCH (r:Region{isEnable:true})-[:REGION]-(p:Province {isEnable:true}) where id(r)={0} return {name:p.name, code:p.code, geoFence:p.geoFence, latitude:p.latitude, longitude:p.longitude,id: id(p)} as result")
    List<Map<String,Object>> findAllProvinceByRegionId(Long regionId);

    @Query("MATCH (p:Province {isEnable:true})-[:PROVINCE]-(m:Municipality) where id(m)={0} return p")
    Province findProvinceByMunicipalityId(Long id);

    Province findByName(String name);
}
