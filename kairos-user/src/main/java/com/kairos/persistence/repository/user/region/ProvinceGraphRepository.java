package com.kairos.persistence.repository.user.region;

import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import com.kairos.persistence.model.user.region.Province;

/**
 * Created by oodles on 7/1/17.
 */
@Repository
public interface ProvinceGraphRepository extends GraphRepository<Province>{
    List<Province> findAll();


    @Query("MATCH (r:Region{isEnable:true})-[:REGION]-(p:Province {isEnable:true}) where id(r)={0} return {name:p.name, code:p.code, geoFence:p.geoFence, latitude:p.latitude, longitude:p.longitude,id: id(p)} as result")
    List<Map<String,Object>> findAllProvinceByRegionId(Long regionId);

    @Query("MATCH (p:Province {isEnable:true})-[:PROVINCE]-(m:Municipality) where id(m)={0} return p")
    Province findProvinceByMunicipalityId(Long id);

    Province findByName(String name);
}
