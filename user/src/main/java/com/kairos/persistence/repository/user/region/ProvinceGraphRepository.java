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


    @Query("MATCH (r:Region{isEnable:true})-[:REGION]-(p:Province {isEnable:true}) where id(r)={0} return {" +
            "translations :{english :{name: CASE WHEN p.`translatedNames.english` IS NULL THEN '' ELSE p.`translatedNames.english` END, description : CASE WHEN p.`translatedDescriptions.english` IS NULL THEN '' ELSE p.`translatedDescriptions.english` END},\n" +
            "hindi:{name: CASE WHEN p.`translatedNames.hindi` IS NULL THEN '' ELSE p.`translatedNames.hindi` END, description : CASE WHEN p.`translatedDescriptions.hindi` IS NULL THEN '' ELSE p.`translatedDescriptions.hindi` END},\n" +
            "danish:{name: CASE WHEN p.`translatedNames.danish` IS NULL THEN '' ELSE p.`translatedNames.danish` END, description : CASE WHEN p.`translatedDescriptions.danish` IS NULL THEN '' ELSE p.`translatedDescriptions.danish` END},\n" +
            "britishenglish:{name: CASE WHEN p.`translatedNames.britishenglish` IS NULL THEN '' ELSE p.`translatedNames.britishenglish` END, description : CASE WHEN p.`translatedDescriptions.britishenglish` IS NULL THEN '' ELSE p.`translatedDescriptions.britishenglish` END}},\n" +
            "name:p.name, code:p.code, geoFence:p.geoFence, latitude:p.latitude, longitude:p.longitude,id: id(p)} as result")
    List<Map<String,Object>> findAllProvinceByRegionId(Long regionId);

    @Query("MATCH (p:Province {isEnable:true})-[:PROVINCE]-(m:Municipality) where id(m)={0} return p")
    Province findProvinceByMunicipalityId(Long id);

    Province findByName(String name);
}
